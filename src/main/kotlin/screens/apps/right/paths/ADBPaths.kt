package screens.apps.right.paths

import adb.ADBConst

object ADBPaths {
    fun getPackagePaths(deviceId: String, packageName: String): String {
        return try {
            val command = arrayOf(ADBConst.path, "-s", deviceId, "shell", "pm", "path", packageName)
            val process = Runtime.getRuntime().exec(command)
            val output = process.inputStream.bufferedReader().use { it.readText() }
            process.waitFor()
            // Prepend command echo for visibility
             output.trim()
        } catch (e: Exception) {
            "Error executing ADB command: ${e.message}"
        }
    }

    fun pullFile(deviceId: String, remotePath: String, downloadsDir: String) {
        val cmd = arrayOf(ADBConst.path, "-s", deviceId, "pull", remotePath, downloadsDir)
        val process = Runtime.getRuntime().exec(cmd)
        process.inputStream.bufferedReader().use { it.readText() }
        process.errorStream.bufferedReader().use { it.readText() }
        process.waitFor()
    }
}


