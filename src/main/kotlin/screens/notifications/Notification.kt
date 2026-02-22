package screens.notifications

data class Notification(
    val id: String,
    val packageName: String,
    val title: String,
    val text: String,
    val postTime: Long = 0L
)

data class NotificationMaster(
    val notification_key: String,
    val packageName: String,
    val title: String,
    val text: String,
    val postTime: Long = 0L,
    val rawdata: List<MutableMap<String, String?>>
)
