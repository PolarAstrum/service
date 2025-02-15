package cc.polarastrum.service.asm

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes

/**
 * PolarAstrumService
 * cc.polarastrum.service.asm.UserClassVisitor
 *
 * @author mical
 * @since 2025/2/13 17:18
 */
class UserClassVisitor(
    classVisitor: ClassVisitor,
    private val clazz: String, // com/mcstarrysky/aiyatsbus/module/bukkit/DefaultAiyatsbusBooster
    private val field: String,
    private val userId: Long
) : ClassVisitor(Opcodes.ASM9, classVisitor) {

    private var isTargetClass = false

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        isTargetClass = name == clazz
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        return if (isTargetClass && name == field && descriptor == "Ljava/lang/Long;") {
            super.visitField(access, name, descriptor, signature, userId)
        } else {
            super.visitField(access, name, descriptor, signature, value)
        }
    }
}