package screens.packages

import adb.ADBConst
import java.io.BufferedReader
import java.io.InputStreamReader
fun getTopLevelSections(deviceId: String, packageName: String): Map<String, String> {
    val sectionMap = mutableMapOf<String, String>()
    try {
        val process = Runtime.getRuntime().exec(
            arrayOf(ADBConst.path, "-s", deviceId, "shell", "dumpsys", "package", packageName)
        )
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val lines = reader.readLines()
        reader.close()
        process.waitFor()

        // Find indexes of top-level headers (`...:` and no leading spaces)
        val headerIndexes = mutableListOf<Int>()
        for ((i, line) in lines.withIndex()) {
            if (line.trim().endsWith(":") && !line.startsWith(" ")) {
                headerIndexes.add(i)
            }
        }

        // Build sections using index ranges
        for ((idx, start) in headerIndexes.withIndex()) {
            val endExclusive = if (idx + 1 < headerIndexes.size) headerIndexes[idx + 1] else lines.size
            val rawHeader = lines[start].trim()
            val cleanHeader = rawHeader.removeSuffix(":") // ✅ remove trailing colon
            val section = lines.subList(start, endExclusive).joinToString("\n")
            sectionMap[cleanHeader] = section
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

//    // Debug print
//    sectionMap.forEach { (key, value) ->
//        println("Key: $key")   // now without ":"
//        println("Section:\n$value")
//        println("---------------")
//    }

    return sectionMap
}




