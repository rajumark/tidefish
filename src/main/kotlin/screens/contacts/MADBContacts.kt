package screens.contacts

import adb.ADBConst
import screens.contacts.MADBContacts.data1
import screens.contacts.MADBContacts.display_name
import screens.contacts.MADBContacts.mimetype
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

object MADBContacts {
    const val contact_id = "contact_id"
    const val display_name = "display_name"
    const val mimetype = "mimetype"
    const val data1 = "data1"


var catchedContactList= listOf<ContactMaster>()
    fun getContactsAll(id: String): List<ContactMaster> {
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
                "content://com.android.contacts/data"
            )
        // val command = arrayOf("adb", "shell", "content", "query", "--uri", "content://com.android.contacts/data --projection phonetic_name:raw_contact_id:mimetype:data1:data2:has_phone_number:photo_file_id:hash_id:lookup:phonebook_label:_id:photo_uri:contact_last_updated_timestamp:last_time_used")

        try {

            val process = ProcessBuilder(*command).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
            //    val listMaps = parseContactData(line)
                val listMaps = parseContactDataIfficient(line)
                if (!listMaps.isNullOrEmpty()) {
                    mapDataGroup.add(listMaps)
                }
            }

             process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val listOrigroup =
            mapDataGroup.groupBy { it.getOrDefault(contact_id, "@") ?: "@" }.filter { it.key != "@" }.map {
                ContactMaster(
                    contact_id = it.key,
                    displayName = getDisplayName(it.value) ?: "",
                    number = getNumbersList(it.value),
                    rawdata = it.value,
                )
            }


        val listfinal = listOrigroup.sortedByDescending { it.contact_id.toIntOrNull() ?: 0 }
        catchedContactList =listfinal
        return listfinal
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

fun parseContactString(input: String?): Contact? {
    if (input == null) return null

    // Find the part of the string that starts with "_id=", ignoring any prefixes like "Row: ..."
    val keyValuePart = input.substringAfter("_id=", missingDelimiterValue = "")
    if (keyValuePart.isEmpty()) return null  // Return null if "_id=" is not found

    // Re-add the "_id=" part that was removed by `substringAfter`, then split by commas
    val parts = ("_id=$keyValuePart").split(", ").map { it.trim() }

    // Initialize variables to hold the extracted values
    var id: String? = null
    var displayName: String? = null
    var number: String? = null

    // Iterate through each part to find and assign values
    for (part in parts) {
        when {
            part.startsWith("_id=") -> {
                id = part.substringAfter("_id=").trim()
            }

            part.startsWith("number=") -> {
                number = part.substringAfter("number=").trim()
            }

            part.startsWith("display_name=") -> {
                displayName = part.substringAfter("display_name=").trim()
            }
        }
    }

    // Check if all required fields are present and return a Contact object
    return if (id != null && displayName != null && number != null) {
        Contact(id, displayName, number)
    } else {
        null // Return null if any field is missing
    }
}

data class ModelContactIdMap(
    val contact_id: String,
    val rawdata: MutableMap<String, String?>,

    ) {

}

fun parseContactStringDetails(input: String?): List<Pair<String, String?>> {
    if (input == null) return emptyList()
    val split1 = input.split(", ")
    val newlist1 = split1.mapNotNull { sigle ->
        if (sigle.contains("Row: ")) {
            val startext = sigle.split(" ").firstOrNull { it.contains("=") }
            getPair(startext)
        } else {
            getPair(sigle)
        }
    }
    return newlist1.toList()
}


fun getPair(startext: String?): Pair<String, String>? {
    val list = startext?.split("=").orEmpty()
    return if (list.size == 2) {
        Pair(list[0], list[1])
    } else {
        null
    }
}

fun getPhoneNumbersList(data: MutableMap<String, String?>): String? {
    //mimetype=vnd.android.cursor.item/phone_v2
    val isHaveNumber = data.getOrDefault(mimetype, "") == MimeTypes.MIMETYPE_PHONE_V2
    if (isHaveNumber) {
        return data.getOrDefault(data1, null)
    }
    return null
}

fun getDisplayName(list: List<MutableMap<String, String?>>): String {
    val value: String = list.firstOrNull { map ->
        val name = map.getOrDefault(display_name, "") ?: ""
        name.isBlank().not()
    }?.getOrDefault(display_name, "Unknown") ?: "Unknown"
    return value
}

fun getNumbersList(list: List<MutableMap<String, String?>>): List<String> {
    return list.mapNotNull { mapoc ->
        getPhoneNumbersList(mapoc)
    }.distinct()
}


object MimeTypes {
    const val MIMETYPE_GROUP_MEMBERSHIP = "vnd.android.cursor.item/group_membership"
    const val MIMETYPE_WEBSITE = "vnd.android.cursor.item/website"
    const val MIMETYPE_IM = "vnd.android.cursor.item/im"
    const val MIMETYPE_ORGANIZATION = "vnd.android.cursor.item/organization"
    const val MIMETYPE_RELATION = "vnd.android.cursor.item/relation"
    const val MIMETYPE_POSTAL_ADDRESS_V2 = "vnd.android.cursor.item/postal-address_v2"
    const val MIMETYPE_PHONE_V2 = "vnd.android.cursor.item/phone_v2"
    const val MIMETYPE_EMAIL_V2 = "vnd.android.cursor.item/email_v2"
    const val MIMETYPE_NICKNAME = "vnd.android.cursor.item/nickname"
    const val MIMETYPE_CONTACT_EVENT = "vnd.android.cursor.item/contact_event"
    const val MIMETYPE_NOTE = "vnd.android.cursor.item/note"
    const val MIMETYPE_NAME = "vnd.android.cursor.item/name"
    const val MIMETYPE_SIP_ADDRESS = "vnd.android.cursor.item/sip_address"
    const val MIMETYPE_IDENTITY = "vnd.android.cursor.item/identity"
}

val listofSortMime = mapOf(
    MimeTypes.MIMETYPE_NAME to 99,
    MimeTypes.MIMETYPE_NICKNAME to 98,
    MimeTypes.MIMETYPE_PHONE_V2 to 97,
    MimeTypes.MIMETYPE_EMAIL_V2 to 96,
    MimeTypes.MIMETYPE_WEBSITE to 95,
    MimeTypes.MIMETYPE_IM to 94,
    MimeTypes.MIMETYPE_ORGANIZATION to 93,
    MimeTypes.MIMETYPE_POSTAL_ADDRESS_V2 to 92,
    MimeTypes.MIMETYPE_RELATION to 91,
    MimeTypes.MIMETYPE_CONTACT_EVENT to 90,
    MimeTypes.MIMETYPE_NOTE to 89,
    MimeTypes.MIMETYPE_SIP_ADDRESS to 88,
    MimeTypes.MIMETYPE_GROUP_MEMBERSHIP to 87,
    MimeTypes.MIMETYPE_IDENTITY to 86,
)

fun getMeaningfulTitle(mimeType: String): String {
    return when (mimeType) {
        MimeTypes.MIMETYPE_GROUP_MEMBERSHIP -> "Group Membership"
        MimeTypes.MIMETYPE_WEBSITE -> "Website"
        MimeTypes.MIMETYPE_IM -> "Instant Messenger"
        MimeTypes.MIMETYPE_ORGANIZATION -> "Organization"
        MimeTypes.MIMETYPE_RELATION -> "Relation"
        MimeTypes.MIMETYPE_POSTAL_ADDRESS_V2 -> "Postal Address"
        MimeTypes.MIMETYPE_PHONE_V2 -> "Phone Number"
        MimeTypes.MIMETYPE_EMAIL_V2 -> "Email Address"
        MimeTypes.MIMETYPE_NICKNAME -> "Nickname"
        MimeTypes.MIMETYPE_CONTACT_EVENT -> "Contact Event"
        MimeTypes.MIMETYPE_NOTE -> "Note"
        MimeTypes.MIMETYPE_NAME -> "Name"
        MimeTypes.MIMETYPE_SIP_ADDRESS -> "SIP Address"
        MimeTypes.MIMETYPE_IDENTITY -> "Identity"
        else -> {
            mimeType.substringAfterLast("/")
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                .ifEmpty { "Unknown" }
        }
    }
}


fun parseContactDataIfficient(input: String? ): MutableMap<String, String?>? {
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
            getExtraValueContact(fields, index + 1).takeIf {
                it.isBlank().not()
            }?.let { extrav ->
                value = "$value, $extrav"
                isFound = true
            }
            if (isFound) {
                isFound = false
                getExtraValueContact(fields, index + 2).takeIf {
                    it.isBlank().not()
                }?.let { extrav ->
                    value = "$value, $extrav"
                    isFound = true
                }
                if (isFound) {
                    isFound = false
                    getExtraValueContact(fields, index + 3).takeIf {
                        it.isBlank().not()
                    }?.let { extrav ->
                        value = "$value, $extrav"
                    }
                }
            }


            if (listofValidContactDBCaloums.contains(key)) {
                dataMap[key] = value
            }
        } else {
            // If the field doesn't have an equal sign, it's a malformed entry (shouldn't happen with this data)
            continue
        }
    }



    return dataMap
}

fun getExtraValueContact(
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
val listofValidContactDBCaloums = listOf(
    "phonetic_name",
    "status_res_package",
    "custom_ringtone",
    "contact_status_ts",
    "account_type",
    "data_version",
    "photo_file_id",
    "contact_status_res_package",
    "name_verified",
    "group_sourceid",
    "display_name_alt",
    "sort_key_alt",
    "mode",
    "last_time_used",
    "starred",
    "contact_status_label",
    "has_phone_number",
    "chat_capability",
    "raw_contact_id",
    "contact_account_type",
    "carrier_presence",
    "contact_last_updated_timestamp",
    "res_package",
    "photo_uri",
    "data_sync4",
    "phonebook_bucket",
    "times_used",
    "display_name",
    "sort_key",
    "data_sync1",
    "version",
    "data_sync2",
    "data_sync3",
    "photo_thumb_uri",
    "status_label",
    "contact_presence",
    "in_default_directory",
    "times_contacted",
    "_id",
    "account_type_and_data_set",
    "name_raw_contact_id",
    "status",
    "phonebook_bucket_alt",
    "last_time_contacted",
    "pinned",
    "is_primary",
    "photo_id",
    "video",
    "contact_id",
    "contact_chat_capability",
    "contact_status_icon",
    "in_visible_group",
    "phonebook_label",
    "account_name",
    "nickname",
    "display_name_source",
    "company",
    "data9",
    "dirty",
    "sourceid",
    "phonetic_name_style",
    "send_to_voicemail",
    "data8",
    "lookup",
    "data7",
    "data6",
    "phonebook_label_alt",
    "data5",
    "is_super_primary",
    "data4",
    "data3",
    "data2",
    "data1",
    "data_set",
    "contact_status",
    "backup_id",
    "preferred_phone_account_component_name",
    "raw_contact_is_user_profile",
    "status_ts",
    "data10",
    "preferred_phone_account_id",
    "data12",
    "mimetype",
    "status_icon",
    "data11",
    "data14",
    "data13",
    "hash_id",
    "data15",
    )

