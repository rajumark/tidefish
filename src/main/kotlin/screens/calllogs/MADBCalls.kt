package screens.calllogs

import adb.ADBConst


import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

object MADBCalls {

    fun getCallssAll(id: String): MutableList<MutableMap<String, String?>> {
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
                "content://call_log/calls"
            )

        try {

            val process = ProcessBuilder(*command).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val listMaps = parseCallData(line)
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


fun parseCallData(input: String?): MutableMap<String, String?>? {
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
            getExtraValue(fields, index + 1).takeIf {
                it.isBlank().not()
            }?.let { extrav ->
                value = "$value, $extrav"
                isFound = true
            }
            if (isFound) {
                isFound = false
                getExtraValue(fields, index + 2).takeIf {
                    it.isBlank().not()
                }?.let { extrav ->
                    value = "$value, $extrav"
                    isFound = true
                }
                if (isFound) {
                    isFound = false
                    getExtraValue(fields, index + 3).takeIf {
                        it.isBlank().not()
                    }?.let { extrav ->
                        value = "$value, $extrav"
                    }
                }
            }


            if (listofValidCallsDBCaloums.contains(key)) {
                if (key == "type") { //Incoming Call
                    value = getCallTypeDescription(value.toIntOrNull() ?: 99)
                }else
                if (key == "date") { //Incoming Call
                    value = convertTimestampToDateCall(value)
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
fun convertTimestampToDateCall(timestamp: String?): String {
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
fun getCallTypeDescription(callType: Int): String {
    return when (callType) {
        1 -> "Incoming Call" // The call was received (incoming call)
        2 -> "Outgoing Call" // The call was dialed (outgoing call)
        3 -> "Missed Call" // The call was missed (not answered)
        4 -> "Rejected Call" // The call was rejected (declined by the user)
        5 -> "Blocked Call" // The call was blocked
        6 -> "Voicemail Call" // The call went to voicemail
        else -> "Unknown Call Type(type=$callType)" // For any other value or unexpected types
    }
}

fun getExtraValue(
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



val listofValidCallsDBCaloums = listOf(
    "name",
    "duration",
    "subject",
    "is_call_log_phone_account_migration_pending",
    "source_id",
    "my_number",
    "cloud_antispam_type",
    "subscription_id",
    "cloud_antispam_type_tag",
    "photo_id",
    "post_dial_digits",
    "call_screening_app_name",
    "priority",
    "number",
    "countryiso",
    "forwarded_call",
    "sync_1",
    "sync_2",
    "sync_3",
    "photo_uri",
    "geocoded_location",
    "call_id_description",
    "missed_reason",
    "call_id_app_name",
    "block_reason",
    "subscription_component_name",
    "add_for_all_users",
    "numbertype",
    "features",
    "call_id_name",
    "transcription",
    "phone_call_type",
    "call_id_nuisance_confidence",
    "missed_count",
    "last_modified",
    "ai",
    "_id",
    "new",
    "date",
    "type",
    "simid",
    "contact_id",
    "presentation",
    "via_number",
    "number_type",
    "numberlabel",
    "normalized_number",
    "composer_photo_uri",
    "phone_account_address",
    "phone_account_hidden",
    "lookup_uri",
    "voicemail_uri",
    "matched_number",
    "call_id_package_name",
    "mark_deleted",
    "transcription_state",
    "data_usage",
    "location",
    "call_screening_component_name",
    "call_id_details",
    "is_read",
    "firewalltype",
)

