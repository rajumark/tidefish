package adb

import adb.ADBDevice.getOsVersion
import java.io.BufferedReader
import java.io.InputStreamReader

object SADBDevices {
    fun getAvailableDevices(): List<DeviceModel> {
        val devices = mutableListOf<DeviceModel>()

        val process = Runtime.getRuntime().exec(arrayOf(ADBConst.path, "devices"))
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        reader.readLine() // Skip the first line (header)

        var line: String? = reader.readLine()
        while (line != null) {
            val parts = line.split("\t".toRegex())
            if (parts.size >= 2) {
                val deviceId = parts[0]
                val osVersion = getOsVersion(deviceId) // Check OS version (optional)
                devices.add(DeviceModel(deviceId, osVersion))
            }
            line = reader.readLine()
        }

        reader.close()
        process.waitFor()

        return devices
    }
}