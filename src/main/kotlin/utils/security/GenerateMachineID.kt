package utils.security

import java.io.File

fun getDeviceFingerprint(): String {
    val osName = System.getProperty("os.name") ?: ""
    val osArch = System.getProperty("os.arch") ?: ""
    val userHome = System.getProperty("user.home") ?: ""
    
    // CPU Info (UUID / ID)
    val cpuInfo = try {
        when {
            osName.contains("Windows", ignoreCase = true) -> {
                val proc = Runtime.getRuntime().exec(arrayOf("wmic", "cpu", "get", "ProcessorId"))
                proc.inputStream.bufferedReader().readLines().getOrElse(1) { "" }.trim()
            }
            osName.contains("Linux", ignoreCase = true) -> {
                File("/proc/cpuinfo").readLines()
                    .firstOrNull { it.startsWith("Serial") || it.startsWith("processor") }
                    ?.split(":")?.getOrNull(1)?.trim() ?: ""
            }
            osName.contains("Mac", ignoreCase = true) -> {
                val proc = Runtime.getRuntime().exec(arrayOf("ioreg", "-rd1", "-c", "IOPlatformExpertDevice"))
                proc.inputStream.bufferedReader().readText()
                    .lineSequence()
                    .firstOrNull { it.contains("IOPlatformUUID") }
                    ?.split("=")?.getOrNull(1)?.replace("\"", "")?.trim() ?: ""
            }
            else -> ""
        }
    } catch (e: Exception) { "" }
    
    // Disk Serial
    val diskSerial = try {
        when {
            osName.contains("Windows", ignoreCase = true) -> {
                val proc = Runtime.getRuntime().exec(arrayOf("wmic", "diskdrive", "get", "SerialNumber"))
                proc.inputStream.bufferedReader().readLines().getOrElse(1) { "" }.trim()
            }
            osName.contains("Linux", ignoreCase = true) -> {
                File("/sys/block/sda/device/serial").takeIf { it.exists() }?.readText()?.trim() ?: ""
            }
            osName.contains("Mac", ignoreCase = true) -> {
                val proc = Runtime.getRuntime().exec(arrayOf("ioreg", "-rd1", "-c", "AppleAHCIDiskDriver"))
                proc.inputStream.bufferedReader().readText()
                    .lineSequence()
                    .firstOrNull { it.contains("Serial Number") }
                    ?.split("=")?.getOrNull(1)?.replace("\"", "")?.trim() ?: ""
            }
            else -> ""
        }
    } catch (e: Exception) { "" }
    
    // Motherboard UUID
    val boardUUID = try {
        when {
            osName.contains("Windows", ignoreCase = true) -> {
                val proc = Runtime.getRuntime().exec(arrayOf("wmic", "csproduct", "get", "UUID"))
                proc.inputStream.bufferedReader().readLines().getOrElse(1) { "" }.trim()
            }
            osName.contains("Linux", ignoreCase = true) -> {
                File("/sys/class/dmi/id/product_uuid").takeIf { it.exists() }?.readText()?.trim() ?: ""
            }
            osName.contains("Mac", ignoreCase = true) -> {
                val proc = Runtime.getRuntime().exec(arrayOf("ioreg", "-rd1", "-c", "IOPlatformExpertDevice"))
                proc.inputStream.bufferedReader().readText()
                    .lineSequence()
                    .firstOrNull { it.contains("IOPlatformUUID") }
                    ?.split("=")?.getOrNull(1)?.replace("\"", "")?.trim() ?: ""
            }
            else -> ""
        }
    } catch (e: Exception) { "" }
    
    // Return comma-separated machine ID
    return "$osName#$osArch#$userHome#$cpuInfo#$diskSerial#$boardUUID"
}
