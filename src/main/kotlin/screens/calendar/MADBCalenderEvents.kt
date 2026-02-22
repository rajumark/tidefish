package screens.calendar

import adb.ADBConst

import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

object MADBCalenderEvents {
    const val contact_id = "contact_id"
    const val display_name = "display_name"
    const val mimetype = "mimetype"
    const val data1 = "data1"


    fun getCalendersEventsAll(id: String, showOriginal: Boolean): MutableList<MutableMap<String, String?>> {
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
                "content://com.android.calendar/events"
            )

        try {

            val process = ProcessBuilder(*command).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val listMaps = parseEventsData(line,showOriginal)
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


    fun parseContactData(input: String?): MutableMap<String, String?>? {
        val dataMap = mutableMapOf<String, String?>()

        if (input == null) return null
        // Split the input string by commas to separate the fields
        val fields = input.split(",").map { it.trim() }

        // Create a map to hold parsed key-value pairs

        // Iterate through each field and split by '=' to get key-value pairs
        for (field in fields) {
            val keyValue = field.split("=")
            if (keyValue.size == 2) {
                dataMap[keyValue[0]] = keyValue[1].takeIf { it != "NULL" } // Convert "NULL" to null
            } else {
                // Handle cases where there might not be an '=' or malformed input
                dataMap[keyValue[0]] = null
            }
        }
        if (isHaveTrueContactID(dataMap)) {
            return dataMap
        }
        return null
    }

    private fun isHaveTrueContactID(dataMap: MutableMap<String, String?>): Boolean {
        var have = false;
        val value = dataMap.getOrDefault(contact_id, null)
        if (value.isNullOrBlank().not()) {
            have = true
        }
        return have
    }
}


fun parseEventsData(input: String?, showOriginal: Boolean): MutableMap<String, String?>? {
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


            if (listofValidCalenderEventsDBCaloums.contains(key)) {
                 if (!showOriginal){
                    if (key == "dtstart") { //Incoming Call
                        value = convertTimestampToDateMessages(value )
                    }else if (key == "dtend") { //Incoming Call
                        value = convertTimestampToDateMessages(value )
                    }else if (key == "lastDate") { //Incoming Call
                        value = convertTimestampToDateMessages(value )
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

val listofValidCalenderEventsDBCaloums = listOf(
    "originalAllDay",
    "account_type",
    "exrule",
    "mutators",
    "originalInstanceTime",
    "allDay",
    "allowedReminders",
    "rrule",
    "canOrganizerRespond",
    "lastDate",
    "visible",
    "calendar_id",
    "hasExtendedProperties",
    "calendar_access_level",
    "selfAttendeeStatus",
    "allowedAvailability",
    "eventColor_index",
    "isOrganizer",
    "_sync_id",
    "calendar_color_index",
    "_id",
    "guestsCanInviteOthers",
    "allowedAttendeeTypes",
    "dtstart",
    "guestsCanSeeGuests",
    "sync_data9",
    "sync_data8",
    "exdate",
    "sync_data7",
    "sync_data6",
    "sync_data1",
    "description",
    "eventTimezone",
    "availability",
    "title",
    "ownerAccount",
    "sync_data5",
    "sync_data4",
    "sync_data3",
    "sync_data2",
    "duration",
    "lastSynced",
    "guestsCanModify",
    "cal_sync3",
    "rdate",
    "cal_sync2",
    "maxReminders",
    "isPrimary",
    "cal_sync1",
    "cal_sync10",
    "account_name",
    "cal_sync7",
    "cal_sync6",
    "cal_sync5",
    "cal_sync4",
    "calendar_color",
    "cal_sync9",
    "cal_sync8",
    "dirty",
    "calendar_timezone",
    "accessLevel",
    "eventLocation",
    "hasAlarm",
    "uid2445",
    "deleted",
    "eventColor",
    "organizer",
    "eventStatus",
    "customAppUri",
    "canModifyTimeZone",
    "eventEndTimezone",
    "customAppPackage",
    "original_sync_id",
    "hasAttendeeData",
    "displayColor",
    "dtend",
    "original_id",
    "sync_data10",
    "calendar_displayName",




)



