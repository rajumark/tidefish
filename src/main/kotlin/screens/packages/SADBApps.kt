package screens.packages

import adb.ADBConst

object SADBApps {
    fun getApps(id: String, type: AppType): List<String> {
        try {

            //val pinList = TextDatabase.getPinList()
            val command = when (type) {
                AppType.ALL_APPS -> arrayOf(ADBConst.path, "-s", id, "shell", "pm", "list", "packages")
                AppType.USER_APPS -> arrayOf(ADBConst.path, "-s", id, "shell", "pm", "list", "packages","-3")
                AppType.SYSTEM_APPS -> arrayOf(ADBConst.path, "-s", id, "shell", "pm", "list", "packages","-s")
                AppType.ENABLED_APPS -> arrayOf(ADBConst.path, "-s", id, "shell", "pm", "list", "packages","-e")
                AppType.DISABLED_APPS -> arrayOf(ADBConst.path, "-s", id, "shell", "pm", "list", "packages","-d")
                AppType.UNINSTALLED_APPS -> arrayOf(ADBConst.path, "-s", id, "shell", "pm", "list", "packages","-u")
            }
            val process = Runtime.getRuntime().exec(command)

            val output = process.inputStream.bufferedReader().readLines()
            val packagelist = output.filter { it.startsWith("package:") }
                .map { it.substring(8) }.map { pkgName ->
                    pkgName
                }
            return packagelist
        } catch (e: Exception) {
            return emptyList()
        }
    }
}

