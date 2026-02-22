package screens.calendar

import adb.ADBConst
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

object MADBCalendar {
    const val contact_id = "contact_id"
    const val display_name = "display_name"


    fun getCalendarsAll(
        id: String,
        showOriginal: Boolean
    ): MutableList<MutableMap<String, String?>> {
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
                "content://com.android.calendar/calendars",
                //  "--sort",
                //  "date_added",  tried but not worked
                //  "ASC"
            )

        try {

            val process = ProcessBuilder(*command).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val listMaps = parseCalendarData(line, showOriginal)
                if (listMaps.isNullOrEmpty().not()) {
                    mapDataGroup.add(listMaps!!)
                }
            }

            val exitCode = process.waitFor()
            //println("Process exited with code: $exitCode")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return mapDataGroup.sortedByDescending {
            it.getOrDefault("_id", "")?.toIntOrNull()
        }.toMutableList()
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


fun parseCalendarData(input: String?, showOriginal: Boolean): MutableMap<String, String?>? {
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


            if (listofValidCalendarDBCaloums.contains(key)) {
                if (!showOriginal) {
                    if (key == "date_added" || key == "date_modified") { //Incoming Call
                        value = convertTimestampToDateCalendar(value)
                    } else if (key == "datetaken") { //Incoming Call
                            value = convertTimestampToDateCalender(value)
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

fun convertTimestampToDateCalender(timestamp: String?): String {
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
fun convertTimestampToDateCalendar(timestamp: String? = null): String {
    // Check if the timestamp is null or empty
    if (timestamp.isNullOrEmpty()) {
        return "Invalid timestamp"
    }

    return try {
        // Create a Date object from the timestamp
        val date = Date(timestamp.toLong() * 1000) // Convert seconds to milliseconds

        // Format the Date object into a readable string
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getDefault() // Set the time zone if necessary
        dateFormat.format(date)
    } catch (e: NumberFormatException) {
        "Invalid timestamp format"
    } catch (e: Exception) {
        "Error formatting date: ${e.message}"
    }
}

 

val listofValidCalendarDBCaloums = listOf(
    "account_type",
    "mutators",
    "ownerAccount",
    "allowedReminders",
    "cal_sync3",
    "cal_sync2",
    "isPrimary",
    "maxReminders",
    "cal_sync1",
    "cal_sync10",
    "account_name",
    "cal_sync7",
    "cal_sync6",
    "canPartiallyUpdate",
    "cal_sync5",
    "sync_events",
    "cal_sync4",
    "canOrganizerRespond",
    "calendar_color",
    "cal_sync9",
    "calendar_location",
    "cal_sync8",
    "dirty",
    "visible",
    "calendar_timezone",
    "calendar_access_level",
    "allowedAvailability",
    "_sync_id",
    "deleted",
    "name",
    "canModifyTimeZone",
    "_id",
    "calendar_color_index",
    "allowedAttendeeTypes",
    "calendar_displayName",


)

