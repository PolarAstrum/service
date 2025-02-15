@file:Suppress("UNCHECKED_CAST", "UNUSED")

package cc.polarastrum.service.taboolib.platform

import cc.polarastrum.service.util.AnnotationUtils
import net.mamoe.mirai.event.*
import taboolib.common.Inject
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.ProxyListener
import taboolib.common.platform.service.PlatformListener
import java.lang.reflect.Method

/**
 * TabooLib
 * taboolib.platform.BukkitAdapter
 *
 * @author sky
 * @since 2021/6/17 12:22 上午
 */
@Awake
@Inject
@PlatformSide(Platform.APPLICATION)
class MiraiListener : PlatformListener {

    private val jvmMethodListenersInternal: Any = Class.forName("net.mamoe.mirai.internal.event.JvmMethodListenersInternal")
        .getDeclaredField("INSTANCE")
        .also { it.isAccessible = true }
        .get(null)

    private val registerEventHandlerMethod: Method = Class.forName("net.mamoe.mirai.internal.event.JvmMethodListenersInternal")
        .declaredMethods
        .first { it.name == "registerEventHandler\$mirai_core_api" }
        .also { it.isAccessible = true }
//        .getDeclaredMethod(
//            "registerEventHandler",
//            Method::class.java,
//            Any::class.java,
//            EventChannel::class.java,
//            EventHandler::class.java,
//            CoroutineContext::class.java
//        )

    val plugin: MiraiPlugin
        get() = MiraiPlugin.instance

    override fun <T> registerListener(
        event: Class<T>,
        priority: EventPriority,
        ignoreCancelled: Boolean,
        func: (T) -> Unit
    ): ProxyListener {
        val listener = MiraiListener(event) { func(it as T) }
        val miraiPriority = net.mamoe.mirai.event.EventPriority.valueOf(priority.name)
        val miraiEventHandler = AnnotationUtils.createAnnotation(
            EventHandler::class.java, mapOf(
                "priority" to miraiPriority,
                "ignoreCancelled" to ignoreCancelled,
                "concurrency" to ConcurrencyKind.CONCURRENT
            )
        )

        listener.miraiListener = registerEventHandlerMethod.invoke(
            jvmMethodListenersInternal,
            MiraiListener.handleMethod,
            listener,
            plugin.globalEventChannel(),
            miraiEventHandler,
            plugin.coroutineContext
        ) as Listener<Event>

        return listener
    }

    override fun unregisterListener(proxyListener: ProxyListener) {
        (proxyListener as MiraiListener).miraiListener.complete()
    }

    class MiraiListener(private val clazz: Class<*>, val consumer: (Event) -> Unit) : SimpleListenerHost(), ProxyListener {

        lateinit var miraiListener: Listener<Event>

        fun handle(event: Event) {
            if (clazz.isAssignableFrom(event.javaClass)) {
                consumer(event)
            }
        }

        companion object {

            val handleMethod: Method = MiraiListener::class.java.getDeclaredMethod("handle", Event::class.java).also {
                it.isAccessible = true
            }
        }
    }
}