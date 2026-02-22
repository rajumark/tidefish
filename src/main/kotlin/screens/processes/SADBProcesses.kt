package screens.processes

import adb.ADBConst
import java.io.BufferedReader
import java.io.InputStreamReader

object SADBProcesses {

    fun getServiceListPair(id: String): MutableList<MutableMap<String, String?>> {
        val outputList = mutableListOf<MutableMap<String, String?>>()
        val command = arrayOf(ADBConst.path, "-s", id, "shell", "dumpsys", "activity", "services")

        try {
            val process = ProcessBuilder(*command).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                if (line!!.trim().startsWith("app=ProcessRecord")) {
                    val aa = line!!.split(" ").last().removeSuffix("}")
                    val pid_name = aa.split(":")
                    if (pid_name.size == 2) {
                        val name = if (pid_name[1].contains("/")) pid_name[1].split("/").first() else pid_name[1]
                        val map = mutableMapOf<String, String?>()
                        map["pid"] = pid_name[0]
                        map["name"] = name
                        outputList.add(map)
                    }
                }
            }
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return outputList
    }

}

