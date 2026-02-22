package adb
object ADBOS {


fun getOperatingSystem(): OperatingSystem {
    val osName = System.getProperty("os.name").lowercase()
    return when {
        osName.contains("win") -> OperatingSystem.WINDOWS
        osName.contains("mac") || osName.contains("darwin") -> OperatingSystem.MAC
        osName.contains("nix") || osName.contains("nux") || osName.contains("aix") -> OperatingSystem.LINUX
        else -> OperatingSystem.UNKNOWN
    }
}
}

enum class OperatingSystem {
    WINDOWS,
    MAC,
    LINUX,
    UNKNOWN
}