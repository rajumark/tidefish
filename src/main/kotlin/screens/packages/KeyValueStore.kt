package screens.packages

import adb.ZipHelper
import java.io.File

object KeyValueStore {
    private const val DB_FILE = "key_data_store.txt"

    private fun file(): File = File(ZipHelper.getUserFolderForCurrentOS(), DB_FILE)

    @Synchronized
    fun put(key: String, value: String) {
        val map = readAll().toMutableMap()
        map[key] = value
        writeAll(map)
    }

    @Synchronized
    fun get(key: String): String? = readAll()[key]

    @Synchronized
    fun remove(key: String) {
        val map = readAll().toMutableMap()
        if (map.remove(key) != null) writeAll(map)
    }

    private fun readAll(): Map<String, String> {
        val f = file()
        if (!f.exists()) return emptyMap()
        return f.readLines()
            .mapNotNull { line ->
                val idx = line.indexOf('=')
                if (idx <= 0) null else line.substring(0, idx) to line.substring(idx + 1)
            }
            .toMap()
    }

    private fun writeAll(map: Map<String, String>) {
        val f = file()
        if (!f.parentFile.exists()) f.parentFile.mkdirs()
        val content = buildString {
            map.forEach { (k, v) ->
                append(k)
                append('=')
                append(v)
                append('\n')
            }
        }
        f.writeText(content)
    }
}


