package screens.deviceproperties

import adb.ADBConst
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader


object MADBDeviceProperties {
    fun fetchAllSettings(deviceId: String): List<SettingItem> = runBlocking {
        val systemDeferred = async {  getSystemSettings(deviceId) }
        val secureDeferred = async {  getSecureSettings(deviceId) }
        val globalDeferred = async {  getGlobalSettings(deviceId) }

        val systemSettings = systemDeferred.await()
        val secureSettings = secureDeferred.await()
        val globalSettings = globalDeferred.await()

        val combinedSettings = systemSettings + secureSettings + globalSettings
        combinedSettings
    }
        fun getSystemSettings(id: String): MutableList<SettingItem> {
            return getSettingsList(id, "system", SettingType.SYSTEM)
        }

        fun getSecureSettings(id: String): MutableList<SettingItem> {
            return getSettingsList(id, "secure", SettingType.SECURE)
        }

        fun getGlobalSettings(id: String): MutableList<SettingItem> {
            return getSettingsList(id, "global", SettingType.GLOBAL)
        }

        private fun getSettingsList(id: String, listType: String, type: SettingType): MutableList<SettingItem> {
            val settingsList = mutableListOf<SettingItem>()
            val command = arrayOf(
                ADBConst.path,
                "-s",
                id,
                "shell",
                "settings",
                "list",
                listType
            )

            try {
                val process = ProcessBuilder(*command).start()
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    if (line.isNullOrBlank()) continue
                    val parts = line!!.split("=")
                    if (parts.isNotEmpty()) {
                        val key = parts[0].trim()
                        val value = line!!.drop(key.length+1)
                        // val value = if (parts.size > 1) parts[1].trim() else null
                        if (key.isNotEmpty()) {
                            settingsList.add(
                                SettingItem(
                                    type = type,
                                    key = key,
                                    value = value,
                                    description = settingsMeaningMap.getOrDefault(key,"")
                                )
                            )
                        }
                    }
                }

                val exitCode = process.waitFor()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return settingsList
        }
    }

