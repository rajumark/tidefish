package screens.messages

import adb.ADBConst

import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

object MADBMessages {


    fun getMessagesAll(id: String, showOriginal: Boolean): MutableList<MutableMap<String, String?>> {
        val mapDataGroup = mutableListOf<MutableMap<String, String?>>()
        val command =
            arrayOf(
                ADBConst.path,
                "-s",
                id,
                "shell",
                "content",
                "query",
                "--uri",
                "content://sms"
            )

        try {

            val process = ProcessBuilder(*command).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val listMaps = parseMessagesData(line,showOriginal)
                if (listMaps.isNullOrEmpty().not()) {
                    mapDataGroup.add(listMaps!!)
                }
            }

            val exitCode = process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return mapDataGroup.sortedByDescending {
            it.getOrDefault("_id", "")?.toIntOrNull()
        }.toMutableList()
    }





}


fun parseMessagesData(input: String?, showOriginal: Boolean): MutableMap<String, String?>? {
    val dataMap = mutableMapOf<String, String?>()

    // List of valid keys to look for in the input string


    if (input == null) return null

    // Remove "Row: ..." if it's part of the input
    val cleanInput = input.substringAfter("Row: ").trim()

    val fields = cleanInput.split(", ").map { it.trim() }

    // Iterate through each field and check if it contains a valid key
    for ((index, field) in fields.withIndex()) {
        val keyValue = field.split("=", limit = 2) // Split only into two parts: key and value

        if (keyValue.size == 2) {
            val key = keyValue[0].trim()
            var value = keyValue[1].trim()

            var isFound = false
            getExtraValueMessages(fields, index + 1).takeIf {
                it.isBlank().not()
            }?.let { extrav ->
                value = "$value, $extrav"
                isFound = true
            }
            if (isFound) {
                isFound = false
                getExtraValueMessages(fields, index + 2).takeIf {
                    it.isBlank().not()
                }?.let { extrav ->
                    value = "$value, $extrav"
                    isFound = true
                }
                if (isFound) {
                    isFound = false
                    getExtraValueMessages(fields, index + 3).takeIf {
                        it.isBlank().not()
                    }?.let { extrav ->
                        value = "$value, $extrav"
                    }
                }
            }


            if (listofValidMessagesDBCaloums.contains(key)) {
                if (!showOriginal){
                    if (key == "type") {
                        value = getMessagesTypeDescription(value.toIntOrNull() ?: 99)
                    } else if (key == "date") {
                        value = convertTimestampToDateMessages(value )
                    }else if (key == "date_sent") {
                        value = convertTimestampToDateMessages(value )
                    }else if (key == "read") {
                        value = getMessagesReadStatusDescription(value.toIntOrNull() ?: 99)
                    }
                }
                dataMap[key] = value
            }
        } else {
            // If the field doesn't have an equal sign, it's a malformed entry (shouldn't happen with this data)
            continue
        }
    }



    return dataMap
}

fun getMessagesTypeDescription(messageType: Int): String {
    return when (messageType) {
        1 -> "Incoming SMS" // The message was received
        2 -> "Sent SMS" // The message was sent
        3 -> "Draft SMS" // The message is saved as a draft
        4 -> "Failed SMS" // The message failed to send
        else -> "Unknown Message Type(type=$messageType)" // For any other value or unexpected types
    }
}
fun getMessagesReadStatusDescription(readStatus: Int): String {
    return when (readStatus) {
        1 -> "Read" // The message has been read
        0 -> "Unread" // The message has not been read
        else -> "Unknown Read Status(readStatus=$readStatus)" // For any other value or unexpected statuses
    }
}
fun getExtraValueMessages(
    fields: List<String>,
    extraIndex: Int,

    ): String {
    val nextnull = fields.getOrNull(extraIndex)
    if (nextnull != null) {
        if (!nextnull.contains("=")) {
            return nextnull
        }
    }
    return ""
}
fun convertTimestampToDateMessages(timestamp: String?): String {
    // Create a Date object from the timestamp
    try {
        val date = Date(timestamp!!.toLong())

        // Format the Date object into a readable string
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return dateFormat.format(date)
    } catch (e: Exception) {
        return timestamp.toString()
    }

}

val listofValidMessagesDBCaloums = listOf(
    "_id",
    "thread_id",
    "address",
    "person",
    "date",
    "date_sent",
    "protocol",
    "read",
    "status",
    "type",
    "reply_path_present",
    "subject",
    "body",
    "service_center",
    "locked",
    "error_code",
    "seen",
    "timed",
    "deleted",
    "sync_state",
    "marker",
    "source",
    "bind_id",
    "mx_status",
    "mx_id",
    "out_time",
    "account",
    "sim_id",
    "block_type",
    "advanced_seen",
    "b2c_ttl",
    "b2c_numbers",
    "fake_cell_type",
    "url_risky_type",
    "creator",
    "favorite_date",
    "mx_id_v2",
    "sub_id",

)



