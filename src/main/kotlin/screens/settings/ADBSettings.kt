package screens.settings

import adb.ADBConst

object ADBSettings {
    fun getSettingsList(): List<SettingsBox> {
        try {
            val pinList = SettingsPinDatabase.getPinList()
            return listSettings.map {
                SettingsBox(it, pinList.contains(it))
            }.sortedByDescending { it.isPined } // Remove "package:" prefix
        } catch (e: Exception) {
            return emptyList()
        }
    }

    fun openSettingByName(id:String,sett: String) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf(ADBConst.path, "-s",id,"shell", "am", "start", "-a", "$sett"))
            process.waitFor() // Wait for the command to complete
           // println("settings opened successfully.")
        } catch (e: Exception) {
            e.printStackTrace()
           // println("Failed to open Wi-Fi settings.")
        }
    }
}

