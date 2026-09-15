package com.lumia.os.control

import rikka.shizuku.Shizuku

object ShizukuHelper {
    fun isAvailable(): Boolean = try { Shizuku.pingBinder() } catch(e: Exception) { false }
    fun runPmCommand(pkg: String, disable: Boolean): Boolean {
        if (!isAvailable()) return false
        val cmd = if (disable) "pm disable-user --user 0 $pkg" else "pm enable --user 0 $pkg"
        return try {
            val method = Shizuku::class.java.getMethod("newProcess", Array<String>::class.java, Array<String>::class.java, String::class.java)
            val process = method.invoke(null, arrayOf("sh", "-c", cmd), null, null) as Process
            process.waitFor() == 0
        } catch(e: Exception) { false }
    }
}
