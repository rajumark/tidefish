package screens.versionchange


import adb.ZipHelper
import java.io.File

object VersionSeenStore {

    private const val versionFileName = "last_seen_version.txt"

    private fun getVersionFile(): File {
        return File(ZipHelper.getUserFolderForCurrentOS(), versionFileName)
    }

    fun getLastSeenVersion(): String {
        val file = getVersionFile()
        return if (file.exists()) {
            file.readText().trim()
        } else {
            ""
        }
    }

    fun setLastSeenVersion(version: String) {
        val file = getVersionFile()
        file.parentFile.mkdirs() // Ensure directory exists
        file.writeText(version)
    }

    fun shouldShowForVersion(currentVersion: String): Boolean {
        val lastSeen = getLastSeenVersion()
        return if (currentVersion != lastSeen) {
            setLastSeenVersion(currentVersion)
            true
        } else {
            false
        }
    }
}
