package adb

import adb.ADBHelper.getCurrentVersion
import adb.ADBOS
import autoupdate.ADBDownload
import autoupdate.ADBSource
import androidx.compose.ui.res.useResource
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.swing.SwingUtilities

object ZipHelper {

    var user_home: String? = "Unknown"
    var currentOS: OperatingSystem = OperatingSystem.UNKNOWN
    var adb_version: String = "Unknown"

    fun refreshDeviceData() {
        currentOS = ADBOS.getOperatingSystem()
        user_home = getUserFolderNameRootPath()
        adb_version = getCurrentVersion()
    }

    fun getResourcePathString(): File {
        return File(System.getProperty("compose.application.resources.dir"))
    }

    fun getAdbPathZip(): File {
        return File(getUserFolderForCurrentOS(), "platform-tools/adb")
    }

    fun getAdbPathZipForEXe(): String {
        return getUserFolderForCurrentOS().absolutePath + "/platform-tools/adb"
    }

    fun getAdbPathZipEXisti(): Boolean {
        return File(getUserFolderForCurrentOS(), "platform-tools").exists()
    }

    fun createUserLibraryFolder(folderName: String) {
        val userLibraryDir = System.getProperty("user.home") + "/Library"
        val appFolder = File(userLibraryDir, folderName)
        if (!appFolder.exists()) {
            appFolder.mkdirs() // Create directories if needed
        }
    }

    fun getUserFolderNameRootPath(): String? {
        val os = ADBOS.getOperatingSystem()
        val userLibraryDir = when (os) {
            OperatingSystem.WINDOWS -> System.getProperty("user.home")
            OperatingSystem.MAC -> System.getProperty("user.home")
            OperatingSystem.LINUX -> System.getProperty("user.home")
            OperatingSystem.UNKNOWN -> System.getProperty("user.home")
        }
        return userLibraryDir
    }

    fun getUserFolderForCurrentOS(): File {
        val os = ADBOS.getOperatingSystem()
        val userLibraryDir = when (os) {
            OperatingSystem.WINDOWS -> System.getProperty("user.home") + "/AppData/Local/"
            OperatingSystem.MAC -> System.getProperty("user.home") + "/Library"
            OperatingSystem.LINUX -> System.getProperty("user.home")
            OperatingSystem.UNKNOWN -> System.getProperty("user.home")
        }
        //val userLibraryDir = System.getProperty("user.home") + "/Library"
        //val userLibraryDir = System.getProperty("user.home") + "/Documents"
        val appFolder = File(userLibraryDir, "Tidefish")
        if (!appFolder.exists()) {
            appFolder.mkdirs() // Create directories if needed
        }
        return appFolder
    }


    fun makeReadyADB(onReady: () -> Unit) {
        val os = ADBOS.getOperatingSystem()
        val destinationFolder = getUserFolderForCurrentOS()
        val zipFileLocation = when (os) {
            OperatingSystem.WINDOWS -> "platform-tools-windows.zip"
            OperatingSystem.MAC -> "platform-tools-macos.zip"
            OperatingSystem.LINUX -> "platform-tools-linux.zip"
            OperatingSystem.UNKNOWN -> "platform-tools-windows.zip"
        }
        
        val destinationFile = File(destinationFolder, zipFileLocation)
        
        if (getAdbPathZipEXisti().not()) {
            // Trigger download dialog
            SwingUtilities.invokeLater {
                ADBDownload.showDownloadDialog(null, destinationFile)
                onReady()
            }
        } else {
            onReady()
        }
    }
    fun unzipFile(zipFile: File, destinationDir: File) {
        ZipInputStream(BufferedInputStream(zipFile.inputStream())).use { zipInput ->
            var entry = zipInput.nextEntry

            while (entry != null) {
                val newFile = File(destinationDir, entry.name)

                if (entry.isDirectory) {
                    newFile.mkdirs()
                } else {
                    newFile.parentFile.mkdirs()
                    FileOutputStream(newFile).use { output ->
                        val buffer = ByteArray(1024)
                        var length: Int
                        while (zipInput.read(buffer).also { length = it } > 0) {
                            output.write(buffer, 0, length)
                        }
                    }
                }

                zipInput.closeEntry()
                entry = zipInput.nextEntry
            }
        }
    }

}


fun unzipFileWorking(inputCode: InputStream, destDirectory: String) {
    val destDir = File(destDirectory)
    if (!destDir.exists()) destDir.mkdir()

    ZipInputStream(inputCode).use { zipStream ->
        var entry: ZipEntry?
        while (zipStream.nextEntry.also { entry = it } != null) {
            if (entry!!.name.contains("__MACOSX") || entry!!.name.contains(".DS_Store")) {
                zipStream.closeEntry()
                continue
            }

            val newFile = File(destDir, entry!!.name)
            if (entry!!.isDirectory) {
                newFile.mkdirs()
            } else {
                newFile.parentFile.mkdirs()
                newFile.outputStream().use { outputStream ->
                    zipStream.copyTo(outputStream)
                }
            }
            zipStream.closeEntry()
        }
    }
}



