package screens.apps.right.paths

import java.awt.Desktop
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Comparator

object PlatformPaths {
    fun getDownloadsDir(): String {
        val os = System.getProperty("os.name").lowercase()
        val home = System.getProperty("user.home")
        val downloads = when {
            os.contains("win") -> Paths.get(System.getenv("USERPROFILE") ?: home, "Downloads")
            else -> Paths.get(home, "Downloads")
        }
        try {
            Files.createDirectories(downloads)
        } catch (_: Exception) {}
        return downloads.toAbsolutePath().toString()
    }

    fun openInFileManager(dir: String) {
        try {
            val file = File(dir)
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file)
                return
            }
        } catch (_: Exception) {}

        val os = System.getProperty("os.name").lowercase()
        try {
            when {
                os.contains("mac") -> Runtime.getRuntime().exec(arrayOf("open", dir))
                os.contains("nux") || os.contains("nix") -> Runtime.getRuntime().exec(arrayOf("xdg-open", dir))
                os.contains("win") -> Runtime.getRuntime().exec(arrayOf("explorer", dir))
            }
        } catch (_: Exception) {}
    }

    fun prepareCleanDir(parentDir: String, folderName: String): String {
        val target: Path = Paths.get(parentDir, folderName)
        try {
            if (Files.exists(target)) {
                Files.walk(target)
                    .sorted(Comparator.reverseOrder())
                    .forEach { p ->
                        try { Files.deleteIfExists(p) } catch (_: Exception) {}
                    }
            }
            Files.createDirectories(target)
        } catch (_: Exception) {}
        return target.toAbsolutePath().toString()
    }
}


