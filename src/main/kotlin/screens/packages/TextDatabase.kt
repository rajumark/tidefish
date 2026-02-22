package screens.packages

import adb.ZipHelper
import java.io.File

object TextDatabase {

    val dbFileName_pin="pinned_packages.txt"


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