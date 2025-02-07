package cc.polarastrum.service.taboolib.common

import taboolib.common.LifeCycle
import taboolib.common.TabooLib
import taboolib.common.classloader.IsolatedClassLoader

/**
 * PolarAstrumService
 * cc.polarastrum.service.TabooLib
 *
 * @author mical
 * @since 2025/2/8 00:48
 */
object TabooLibCommon {

    init {
        IsolatedClassLoader.init(TabooLibCommon::class.java)
    }

    fun testSetup() {
        TabooLib.lifeCycle(LifeCycle.CONST)
        TabooLib.lifeCycle(LifeCycle.INIT)
        TabooLib.lifeCycle(LifeCycle.LOAD)
        TabooLib.lifeCycle(LifeCycle.ENABLE)
    }

    fun testCancel() {
        TabooLib.lifeCycle(LifeCycle.DISABLE);
    }
}