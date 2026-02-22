// Simple test to verify notification parsing
fun main() {
    val deviceId = "ZD222XW5RL"
    val notifications = MADBNotifications.getNotificationsAll(deviceId)
    println("Found ${notifications.size} notifications")
    notifications.take(3).forEach { notification ->
        println("Package: ${notification.packageName}")
        println("Title: ${notification.title}")
        println("Text: ${notification.text}")
        println("PostTime: ${notification.postTime}")
        println("Raw data keys: ${notification.rawdata.flatMap { it.keys }.distinct()}")
        println("---")
    }
}
