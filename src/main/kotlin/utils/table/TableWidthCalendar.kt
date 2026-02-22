package utils.table

import screens.calendar.listofValidCalenderEventsDBCaloums

object TableWidthCalendar {
    val widths: Map<String, Int> = buildMap {
        put("No.", 50)
        // Assign reasonable defaults for all valid calendar event columns
        listofValidCalenderEventsDBCaloums.forEach { key ->
            val width = when (key) {
                "title" -> 260
                "description" -> 380
                "dtstart", "dtend", "lastDate" -> 170
                "eventLocation" -> 240
                "duration" -> 100
                "eventTimezone", "eventEndTimezone", "calendar_timezone" -> 200
                "_id" -> 80
                "calendar_id" -> 100
                "calendar_displayName" -> 200
                "organizer" -> 180
                "account_name", "ownerAccount" -> 150
                "eventStatus" -> 120
                "availability" -> 120
                "allDay", "originalAllDay" -> 80
                "hasAlarm" -> 80
                "isOrganizer" -> 100
                "guestsCanInviteOthers", "guestsCanSeeGuests", "guestsCanModify" -> 120
                "canOrganizerRespond" -> 140
                "hasExtendedProperties" -> 140
                "hasAttendeeData" -> 120
                "canModifyTimeZone" -> 130
                "isPrimary" -> 80
                "visible" -> 80
                "deleted" -> 80
                "dirty" -> 80
                "calendar_access_level", "accessLevel" -> 120
                "selfAttendeeStatus" -> 130
                "eventColor_index", "calendar_color_index" -> 120
                "calendar_color" -> 120
                "eventColor" -> 100
                "displayColor" -> 100
                "customAppUri" -> 200
                "customAppPackage" -> 150
                "uid2445" -> 100
                "original_id", "original_sync_id", "_sync_id" -> 120
                "lastSynced" -> 120
                "sync_data5" -> 170
                "maxReminders" -> 100
                "allowedReminders" -> 120
                "allowedAvailability" -> 130
                "allowedAttendeeTypes" -> 130
                "rrule", "exrule" -> 200
                "rdate", "exdate" -> 200
                "originalInstanceTime" -> 150
                "mutators" -> 120
                "account_type" -> 120
                else -> 120
            }
            put(key, width)
        }
    }
}


