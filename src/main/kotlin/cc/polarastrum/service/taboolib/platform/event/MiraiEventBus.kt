package cc.polarastrum.service.taboolib.platform.event

import taboolib.common.Inject
import taboolib.common.LifeCycle
import taboolib.common.event.InternalEvent
import taboolib.common.event.InternalEventBus
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
import taboolib.common.platform.Ghost
import taboolib.common.platform.Platform
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.*
import taboolib.common.util.optional
import taboolib.common.util.t
import taboolib.library.reflex.ClassAnnotation
import taboolib.library.reflex.ClassMethod
import taboolib.library.reflex.ReflexClass
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Awake
@Inject
class MiraiEventBus : ClassVisitor(-1) {

    private val continuation = object : Continuation<Unit> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<Unit>) {
            result.onFailure { it.printStackTrace() }
        }

    }

    @Suppress("UNCHECKED_CAST")
    override fun visit(method: ClassMethod, owner: ReflexClass) {
        if (method.isAnnotationPresent(SubscribeEvent::class.java) && (method.parameter.size == 1 || (method.parameter.size == 2 && method.parameterTypes[1] == Continuation::class.java))) {
            val anno = method.getAnnotation(SubscribeEvent::class.java)
            val bind = anno.property("bind", "")
            val optionalEvent = if (bind.isNotEmpty()) {
                try {
                    Class.forName(bind)
                } catch (ex: ClassNotFoundException) {
                    null
                }
            } else {
                null
            }

            if (method.parameter.size == 2) {
                if (method.parameterTypes[1] != Continuation::class.java) {
                    error("${owner.name}#${method.name} must have 1 parameter, the first parameter must be an event type and the second must be Continuation")
                }
            } else if (method.parameter.size != 1) {
                error("${owner.name}#${method.name} must have 1 parameter and must be an event type")
            }

            val listenType = try {
                method.parameter[0].instance
            } catch (_: ClassNotFoundException) {
                null
            }
            // 未找到事件类
            if (listenType == null) {
                // 忽略警告
                if (!method.isAnnotationPresent(Ghost::class.java)) {
                    warning(
                        """
                            事件 ${method.parameter[0].name} 未能找到，可使用 @Ghost 关闭此警告。
                            ${method.parameter[0].name} not found, use @Ghost to turn off this warning.
                        """.t()
                    )
                }
                return
            }
            optional(anno) {
                val obj = findInstance(owner)
                // 内部事件处理
                if (InternalEvent::class.java.isAssignableFrom(listenType)) {
                    val priority = anno.enum("priority", EventPriority.NORMAL)
                    val ignoreCancelled = anno.property("ignoreCancelled", false)
                    InternalEventBus.listen(listenType as Class<InternalEvent>, priority.level, ignoreCancelled) { invoke(obj, method, it) }
                    return
                }
                // 判定运行平台
                if (runningPlatform == Platform.APPLICATION) {
                    registerBukkit(method, optionalEvent, anno, obj)
                }
            }
        }
    }

    private fun registerBukkit(method: ClassMethod, optionalBind: Class<*>?, event: ClassAnnotation, obj: Any?) {
        val priority = event.enum("priority", EventPriority.NORMAL)
        val ignoreCancelled = event.property("ignoreCancelled", false)
        val listenType = method.parameterTypes[0]
        if (listenType == OptionalEvent::class.java) {
            if (optionalBind != null) {
                registerBukkitListener(optionalBind, priority, ignoreCancelled) { invoke(obj, method, it, true) }
            }
        } else {
            registerBukkitListener(listenType, priority, ignoreCancelled) { invoke(obj, method, it) }
        }
    }

    private fun invoke(obj: Any?, method: ClassMethod, it: Any, optional: Boolean = false) {
        // Continuation
        if (method.parameter.size == 2) {
            if (obj != null) {
                method.invoke(obj, if (optional) OptionalEvent(it) else it, continuation)
            } else {
                method.invokeStatic(if (optional) OptionalEvent(it) else it, continuation)
            }
        } else {
            if (obj != null) {
                method.invoke(obj, if (optional) OptionalEvent(it) else it)
            } else {
                method.invokeStatic(if (optional) OptionalEvent(it) else it)
            }
        }
    }

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.ENABLE
    }
}