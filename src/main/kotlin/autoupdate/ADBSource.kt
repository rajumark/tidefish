package autoupdate

import adb.ADBOS
import adb.OperatingSystem

object ADBSource {

    var adb_source_win="https://github.com/rajumark/adbcontent/raw/refs/heads/main/platform-tools-windows.zip"
    var adb_source_mac="https://github.com/rajumark/adbcontent/raw/refs/heads/main/platform-tools-macos.zip"
    var adb_source_linux="https://github.com/rajumark/adbcontent/raw/refs/heads/main/platform-tools-linux.zip"

    fun getDownloadSourceURL(): String {
        val currentOS= ADBOS.getOperatingSystem()
        return when (currentOS) {
            OperatingSystem.WINDOWS -> adb_source_win
            OperatingSystem.MAC -> adb_source_mac
            OperatingSystem.LINUX -> adb_source_linux
            OperatingSystem.UNKNOWN -> adb_source_win
        }
    }
}