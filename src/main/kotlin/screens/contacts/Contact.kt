package screens.contacts

data class Contact(
    val id: String,
    val displayName: String,
    val number: String,

    )

data class ContactMaster(
    val contact_id: String,
    val displayName: String,
    val number: List<String>,
    val rawdata:List<MutableMap<String, String?>>,

    )