package screens.settings

import adb.ZipHelper
import java.io.File

object SettingsPinDatabase {


    val dbFileName_pin="settingsPin.txt"


    fun pinPackageName(packageName: String) {
        val file = File(ZipHelper.getUserFolderForCurrentOS(), dbFileName_pin)
        file.appendText("$packageName\n")
    }

    fun getPinList(): List<String> {
        val file = File(ZipHelper.getUserFolderForCurrentOS(), dbFileName_pin)
        return if (file.exists()) {
            file.readLines()
        } else {
            emptyList()
        }
    }
    fun unPinPackage(packageName: String) {
        val packageNames = getPinList().toMutableList()
        packageNames.remove(packageName)
        val file = File(ZipHelper.getUserFolderForCurrentOS(), dbFileName_pin)
        file.writeText(packageNames.joinToString("\n"))
    }
}