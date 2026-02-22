package adb_terminal

import adb.ADBConst
import java.io.BufferedReader
import java.io.InputStreamReader

object ADBTerminalHelper {

    data class ExecResult(val output: String, val isError: Boolean)

    fun normalizeAndBuildCommand(deviceId: String, rawInput: String): Array<String> {
        val trimmed = rawInput.trim()
        if (trimmed.isEmpty()) return arrayOf(ADBConst.path, "-s", deviceId, "shell")

        val tokens = trimmed.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (tokens.isEmpty()) return arrayOf(ADBConst.path, "-s", deviceId, "shell")

        val first = tokens.first().lowercase()

        // If user typed a full adb command, strip leading "adb"
        val noAdb = if (first == "adb") tokens.drop(1) else tokens

        // Handle common shorthand variations
        return when {
            // e.g., "devices"
            noAdb.size == 1 && noAdb[0].equals("devices", ignoreCase = true) ->
                arrayOf(ADBConst.path, "devices")

            // e.g., "shell devices"
            noAdb.size >= 1 && noAdb[0].equals("shell", ignoreCase = true) && noAdb.getOrNull(1)?.equals("devices", true) == true ->
                arrayOf(ADBConst.path, "devices")

            // e.g., "adb shell device" or "shell device" (user likely meant "devices")
            noAdb.size >= 1 && noAdb[0].equals("shell", ignoreCase = true) && noAdb.getOrNull(1)?.equals("device", true) == true ->
                arrayOf(ADBConst.path, "devices")

            // Any other shell command: prefix with -s <id> shell ...
            noAdb.first().equals("shell", ignoreCase = true) ->
                arrayOf(ADBConst.path, "-s", deviceId) + noAdb.toTypedArray()

            // If user omitted "shell", assume they intend a shell command
            else -> arrayOf(ADBConst.path, "-s", deviceId, "shell") + noAdb.toTypedArray()
        }
    }

    fun execAndCapture(command: Array<String>): ExecResult {
        return try {
            val process = Runtime.getRuntime().exec(command)
            val stdout = BufferedReader(InputStreamReader(process.inputStream)).use { it.readText() }
            val stderr = BufferedReader(InputStreamReader(process.errorStream)).use { it.readText() }
            val code = process.waitFor()
            if (code == 0) ExecResult(stdout, false) else ExecResult((stderr.ifBlank { stdout }).ifBlank { "Command failed with exit code $code" }, true)
        } catch (e: Exception) {
            ExecResult("ERROR: ${e.message}", true)
        }
    }

    fun runUserCommand(deviceId: String, rawInput: String): ExecResult {
        val cmd = normalizeAndBuildCommand(deviceId, rawInput)
        return execAndCapture(cmd)
    }
}


