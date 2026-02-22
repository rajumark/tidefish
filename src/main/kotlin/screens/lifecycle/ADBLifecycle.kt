package screens.lifecycle

import adb.ADBConst
import java.io.BufferedReader
import java.io.InputStreamReader

object ADBLifecycle {

    fun parseLogEntry(logString: String?): LogEntry? {
        if (logString == null) return null
        // Split the input string by spaces while preserving key-value pairs
        val entries = logString.split(" ").map { it.trim() }

        // Initialize a mutable map to hold parsed values
        val parsedValues = mutableMapOf<String, String?>()

        // Iterate over each entry and split by "=" to extract key-value pairs
        for (entry in entries) {
            val parts = entry.split("=")
            if (parts.size == 2) {
                parsedValues[parts[0]] = parts[1].removeSurrounding("\"") // Remove quotes if present
            }
        }

        // Return an instance of LogEntry populated with parsed values
        return LogEntry(
            time = getTimeString(logString),
            type = parsedValues["type"],
            packageName = parsedValues["package"],
            className = parsedValues["class"],
            instanceId = parsedValues["instanceId"],
            taskRootPackage = parsedValues["taskRootPackage"],
            taskRootClass = parsedValues["taskRootClass"],
            flags = parsedValues["flags"]
        )
    }



    fun getUsageState(id:String): List<LogEntry> {
        val contacts = mutableListOf<LogEntry>()

        try {
            val command = "${ADBConst.path} -s $id shell dumpsys usagestats"

            val process = Runtime.getRuntime().exec(command)

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val model = parseLogEntry(line)
                if (model != null) {
                    if (model.time != null && model.type != null) {
                        contacts.add(model)
                    }
                }
            }

            // Wait for the process to complete
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return contacts.reversed()
    }

    fun getTimeString(input: String): String? {
        // Find the starting index of the time substring
        val startIndex = input.indexOf("time=\"") + 6 // Length of 'time="'

        // Check if the start index is valid
        if (startIndex < 6) return null

        // Find the ending index of the time substring
        val endIndex = input.indexOf("\"", startIndex)

        // Check if the end index is valid
        if (endIndex == -1) return null

        // Extract and return the time string
        return input.substring(startIndex, endIndex)
    }

}