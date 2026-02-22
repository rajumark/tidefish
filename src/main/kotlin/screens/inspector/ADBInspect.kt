package screens.inspector

import adb.*

import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

object ADBInspect {
    fun getCurrentActivityName(id: String): String? {
        val output =
            ADBHelper.executeCommand("${ADBConst.path} -s $id shell dumpsys activity activities | grep -E 'mCurrentFocus|mFocusedApp'")
        val bb = output?.split(" ")?.firstOrNull { it.contains("/") }?.replace("}", "")
        return bb
    }

    fun getUITree(id: String): Hierarchy? {
        try {
            val file = dumpAndGetFileForUI(id)
            val list = file?.let { parseXmlFile(it) }
            return list
        } catch (e: Exception) {
           // println("AdbStudioLog:error" + e)
            return null
        }
    }

    fun dumpAndGetFileForUI(id: String): File? {
        val appFolder = ZipHelper.getUserFolderForCurrentOS()
        val dumpFile1 = File(appFolder, "window_dump.xml")
        if (dumpFile1.exists()) {
            dumpFile1.delete()
        }
        val commands = listOf(
            "${ADBConst.path} -s $id shell uiautomator dump", // Dump the UI hierarchy
            "${ADBConst.path} -s $id pull /sdcard/window_dump.xml $appFolder", // Pull the dump file
            "${ADBConst.path} -s $id shell rm /sdcard/window_dump.xml" // Cleanup (optional)
        )

        var fullOutput = ""
        for (command in commands) {
            val process = Runtime.getRuntime().exec(command)
            val reader = InputStreamReader(process.inputStream, StandardCharsets.UTF_8)
            val buffer = CharArray(1024)
            var bytesRead: Int
            val output = StringBuilder()
            while (reader.read(buffer).also { bytesRead = it } != -1) {
                output.append(String(buffer, 0, bytesRead))
            }
            fullOutput += output.toString()
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                break
            }
        }

        val dumpFile = File(appFolder, "window_dump.xml")
        if (dumpFile.exists()) {
            return (dumpFile)
        } else {
            return null
        }

    }

    fun replaceClassWithViewN(file: File) {
        val text = file.readText()
        val replacedText = text.replace("""class="""", """CV="""")
        file.writeText(replacedText)
    }

    fun parseXmlFile(file: File): Hierarchy {
        replaceClassWithViewN(file)
        val serializer: Serializer = Persister()
        return serializer.read(Hierarchy::class.java, file)
    }


    fun captureScreenshot(deviceId: String): String {
        val outputDir = ZipHelper.getUserFolderForCurrentOS()
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
        val fileName = "screenshot_inspect.png"
        val outputFile = File(outputDir, fileName)

        try {
            val process =
                Runtime.getRuntime().exec("${ADBConst.path} -s $deviceId shell screencap -p /sdcard/$fileName")
            process.waitFor()

            Runtime.getRuntime().exec("${ADBConst.path} -s $deviceId pull /sdcard/$fileName ${outputFile.absolutePath}")
                .waitFor()
            Runtime.getRuntime().exec("${ADBConst.path} -s $deviceId shell rm /sdcard/$fileName").waitFor()

            return outputFile.absolutePath
        } catch (e: IOException) {
           // println("Error capturing screenshot: ${e.message}")
            return ""
        }
    }


//    val deviceId = "emulator-5554"

//    val activity =
//        "com.android.dialer/com.android.incallui.InCallActivity"
//    val listOut = ADBFragment.runAdbCommand("adb shell dumpsys activity $activity").lines()
//
//


    fun extractFragmentName(fragmentInfo: String): String? {
        // Define a regular expression to match the fragment name
        val regex = Regex(":(\\s*\\w+)\\{")

        // Find the match in the input string
        val matchResult = regex.find(fragmentInfo)

        // Return the fragment name if found, otherwise return null
        return matchResult?.groups?.get(1)?.value?.trim()
    }

    fun String.removeBetween(start: String, end: String): String {
        val startIndex = this.indexOf(start)
        val endIndex = this.indexOf(end, startIndex + start.length)

        return if (startIndex != -1 && endIndex != -1) {
            // Remove the substring including the start and end delimiters
            this.removeRange(startIndex, endIndex + end.length)
        } else {
            // Return the original string if delimiters are not found
            this
        }
    }

    fun getFragmentsList(activity: String): List<String> {
        val listOut = runAdbCommandFragment("${ADBConst.path} shell dumpsys activity $activity").lines()

        val os = ADBOS.getOperatingSystem()
        return when (os) {
            OperatingSystem.MAC -> {
                val listFragment = mutableListOf<String>()
                listOut.forEach {
                    if (it.trim().startsWith("#") && it.trim().contains(":") &&
                        it.trim().contains("{") && it.trim().contains("(")
                    ) {
                        val line = it
                            .removeBetween("{", "}").trim()
                            .removeBetween("{", "}").trim()
                            .removeBetween("{", "}").trim()
                            .removeBetween("{", "}").trim()
                            .removeBetween("{", "}").trim()
                            .removeBetween("(", ")").trim()
                            .removeBetween("(", ")").trim()
                            .removeBetween("(", ")").trim()
                            .removeBetween("(", ")").trim()
                            .removeBetween("(", ")").trim()
                            .removeBetween("#", ":").trim()
                            .removeBetween("#", ":").trim()
                        listFragment.add(line)
                    }
                }
                listFragment.filter { it.isBlank().not() && it.length > 3 }.distinct()
                /*listOut.filter {
                    it.trim().startsWith("#") && it.trim().contains(":") &&
                    it.trim().startsWith("{") && it.trim().contains("(")
                }.map {
                    getFragmentNameMacFromLine(it.trim())
                }.filter { it.isBlank().not() }.distinct()*/
            }

            else -> {
                //working proper for windows
                listOut.filter {
                    it.trim().startsWith("#") && it.trim().contains("id") && !it.trim()
                        .contains("report_fragment_tag")
                }.mapNotNull { extractFragmentName(it) }.filter { it.isBlank().not() }.distinct()
            }
        }.filter {
            !it.contains("SupportRequestManagerFragment")
                    && !it.contains("{")
                    && !it.contains("}")
                    && !it.contains("(")
                    && !it.contains(")")
        }


    }


    fun runAdbCommandFragment(command: String): String {
        val process = Runtime.getRuntime().exec(command)
        val output = process.inputStream.bufferedReader().use { it.readText() }
        process.waitFor()
        return output
    }


    fun getScreenSize(id: String): Pair<Int, Int> {
        val process = Runtime.getRuntime().exec("${ADBConst.path} -s $id shell wm size")
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        var line: String?
        try {
            while (reader.readLine().also { line = it } != null) {
                if (line!!.startsWith("Physical size:")) {
                    val parts = line!!.split(":")[1].trim().split("x")
                    val width = parts[0].toInt()
                    val height = parts[1].toInt()
                    return Pair(width, height)
                }
            }
        } catch (e: Exception) {
            //println("ErrorCodeforscreensize:" + e.message)
        }

        return Pair(0, 0)
    }

    fun getRecentActivities(id: String): MutableList<List<String>> {
        val listfinal = mutableListOf<List<String>>()
        try {
            // Execute the ADB command to get recent activities
            val process = Runtime.getRuntime().exec("${ADBConst.path} -s $id shell dumpsys activity recents")
            val reader = BufferedReader(InputStreamReader(process.inputStream))

            // Read the output line by line
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                // Print lines that contain "Activities"
                if (line!!.contains("Activities=[") && !line!!.contains("Activities=[]")) {
                    val listac: List<String> =
                        line!!.split(" ").filter { it.contains(".") && it.contains("/") }.reversed()
                    if (listac.isNotEmpty()) {
                        listfinal.add(listac)
                    }
                }
            }

            // Wait for the process to complete
            process.waitFor()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listfinal
    }

}

data class ResultData(
    val activityName: String?,
    val appsList: List<String>
)
