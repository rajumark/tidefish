package utils.table

import screens.calendar.listofValidMEdiaDBCaloums

object TableWidthMedia {
    val widths: Map<String, Int> = buildMap {
        put("No.", 50)
        // Assign reasonable defaults for all valid media columns
        listofValidMEdiaDBCaloums.forEach { key ->
            val width = when (key) {
                "_display_name", "_data", "relative_path", "owner_package_name", "title" -> 300
                "mime_type" -> 160
                "date_added", "date_modified", "datetaken" -> 170
                "resolution" -> 120
                "duration" -> 90
                "_size", "size" -> 120
                "album", "bucket_display_name" -> 180
                "_id" -> 70
                else -> 120
            }
            put(key, width)
        }
    }
}


