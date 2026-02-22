package zswing.adb

import adb.ADBConst
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

object MADBMedia {
    const val contact_id = "contact_id"
    const val display_name = "display_name"


    fun getMediaAll(
        id: String,
        sourceType: String,
        contentType: String,
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
                "content://media/$sourceType/$contentType/media",
                //  "--sort",
                //  "date_added",  tried but not worked
                //  "ASC"
            )

        try {

            val process = ProcessBuilder(*command).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val listMaps = screens.calendar.parseMediaData(line, showOriginal)
                if (listMaps.isNullOrEmpty().not()) {
                    mapDataGroup.add(listMaps!!)
                }
            }

            val exitCode = process.waitFor()
          //  println("Process exited with code: $exitCode")
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


fun parseMediaData(input: String?, showOriginal: Boolean): MutableMap<String, String?>? {
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
            screens.calendar.getExtraValue(fields, index + 1).takeIf {
                it.isBlank().not()
            }?.let { extrav ->
                value = "$value, $extrav"
                isFound = true
            }
            if (isFound) {
                isFound = false
                screens.calendar.getExtraValue(fields, index + 2).takeIf {
                    it.isBlank().not()
                }?.let { extrav ->
                    value = "$value, $extrav"
                    isFound = true
                }
                if (isFound) {
                    isFound = false
                    screens.calendar.getExtraValue(fields, index + 3).takeIf {
                        it.isBlank().not()
                    }?.let { extrav ->
                        value = "$value, $extrav"
                    }
                }
            }


            if (screens.calendar.listofValidMEdiaDBCaloums.contains(key)) {
                if (!showOriginal) {
                    if (key == "date_added" || key == "date_modified") { //Incoming Call
                        value = screens.calendar.convertTimestampToDateMedia(value)
                    } else if (key == "datetaken") { //Incoming Call
                            value = screens.calendar.convertTimestampToDateMessages(value)
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


fun convertTimestampToDateMedia(timestamp: String? = null): String {
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
fun convertTimestampToDateMessages(timestamp: String?): String {
    try {
        val date = Date(timestamp!!.toLong())

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return dateFormat.format(date)
    } catch (e: Exception) {
        return timestamp.toString()
    }

}

val listofValidMEdiaDBCaloums = listOf(
    "instance_id",
    "compilation",
    "disc_number",
    "duration",
    "album_artist",
    "description",
    "picasa_id",
    "resolution",
    "latitude",
    "orientation",
    "artist",
    "author",
    "height",
    "is_drm",
    "bucket_display_name",
    "owner_package_name",
    "f_number",
    "volume_name",
    "date_modified",
    "writer",
    "date_expires",
    "composer",
    "_display_name",
    "scene_capture_type",
    "datetaken",
    "mime_type",
    "bitrate",
    "cd_track_number",
    "_id",
    "iso",
    "xmp",
    "year",
    "_data",
    "_size",
    "album",
    "genre",
    "title",
    "width",
    "longitude",
    "is_favorite",
    "is_trashed",
    "exposure_time",
    "group_id",
    "document_id",
    "generation_added",
    "is_download",
    "generation_modified",
    "is_pending",
    "date_added",
    "mini_thumb_magic",
    "capture_framerate",
    "num_tracks",
    "isprivate",
    "original_document_id",
    "bucket_id",
    "relative_path",
    "title_key",
    "is_ringtone",
    "is_audiobook",
    "title_resource_uri",
    "is_recording",
    "is_notification",
    "is_alarm",
    "track",
    "is_music",
    "album_key",
    "artist_id",
    "artist_key",
    "genre_key",
    "is_podcast",
    "album_id",
    "genre_id",
    "bookmark",
    "language",
    "color_transfer",
    "color_standard",
    "tags",
    "category",
    "color_range"
)

