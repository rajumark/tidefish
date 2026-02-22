package adb

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.BufferedReader
import java.io.InputStreamReader

object ADBHelper {
    fun checkAdbCommand(): Boolean {
        return try {
            val process = ProcessBuilder(ADBConst.path, "version")
                .redirectErrorStream(true)
                .start()
            val exitCode = process.waitFor()
            exitCode == 0
        } catch (e: Exception) {
            false
        }
    }
    fun copyTextToClipboard(text: String) {
        val stringSelection = StringSelection(text)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(stringSelection, null)
       // println("Text copied to clipboard successfully.")
    }

    fun executeCommand(command: String): String? {
        return try {
            val process = Runtime.getRuntime().exec(command)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String? = reader.readLine()
            while (line != null) {
                output.append(line).append("\n")
                line = reader.readLine()
            }
            reader.close()
            process.waitFor()
            output.toString().trim()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun getCurrentVersion(): String {

        try {
           return System.getProperty("jpackage.app-version")?:""
        }catch (e:Exception){

        }
        return ""
    }
}