package adb

import java.io.BufferedReader
import java.io.InputStreamReader

object ADBDevice {


    fun getAvailableDevices(): List<Device> {
        val devices = mutableListOf<Device>()

        val process = Runtime.getRuntime().exec(arrayOf(ADBConst.path, "devices"))
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        reader.readLine() // Skip the first line (header)

        var line: String? = reader.readLine()
        while (line != null) {
            val parts = line.split("\t".toRegex())
            if (parts.size >= 2) {
                val deviceId = parts[0]
                val deviceName = parts[1]
                val osVersion =  getOsVersion(deviceId) // Check OS version (optional)
                val modelName =  getModelName(deviceId) // Check OS version (optional)
                val marketName =  getMarketName(deviceId) // Check OS version (optional)
                devices.add(Device(deviceId, deviceName, osVersion, modelName, marketName))
            }
            line = reader.readLine()
        }

        reader.close()
        process.waitFor()


        return devices
    }

    fun getModelName(deviceId: String): String? {
        val process =
            Runtime.getRuntime().exec(arrayOf(ADBConst.path, "-s", deviceId, "shell", "getprop", "ro.product.brand"))
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val modelName = reader.readLine()
        reader.close()
        process.waitFor()
        return modelName?.trim() // Remove leading/trailing whitespace
    }

    fun getOsVersion(deviceId: String): String? {
        val process = Runtime.getRuntime()
            .exec(arrayOf(ADBConst.path, "-s", deviceId, "shell", "getprop", "ro.build.version.release"))
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val version = reader.readLine()
        reader.close()
        process.waitFor()
        return version
    }

    fun getMarketName(deviceId: String): String? {
        val process =
            Runtime.getRuntime().exec(arrayOf(ADBConst.path, "-s", deviceId, "shell", "getprop", "ro.product.marketname"))
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val modelName = reader.readLine()
        reader.close()
        process.waitFor()
        return modelName?.trim() // Remove leading/trailing whitespace
    }

}