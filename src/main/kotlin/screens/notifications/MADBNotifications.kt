package screens.notifications

import adb.ADBConst
import java.io.BufferedReader
import java.io.InputStreamReader

object MADBNotifications {
    
    const val NOTIFICATION_KEY = "notification_key"
    const val PACKAGE_NAME = "pkg"
    const val TITLE = "title"
    const val TEXT = "text"
    const val POST_TIME = "postTime"
    
    var caughtNotificationList = listOf<NotificationMaster>()
    
    fun getNotificationsAll(deviceId: String): List<NotificationMaster> {
        println("DEBUG: getNotificationsAll called with deviceId: $deviceId")
        val notificationsList = mutableListOf<NotificationMaster>()
        
        val command = arrayOf(
            ADBConst.path,
            "-s",
            deviceId,
            "shell",
            "dumpsys",
            "notification"
        )
        
        println("DEBUG: Running command: ${command.joinToString(" ")}")
        
        try {
            val process = ProcessBuilder(*command).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            var currentNotificationDump = StringBuilder()
            var inNotificationRecord = false
            var lineCount = 0
            
            while (reader.readLine().also { line = it } != null) {
                lineCount++
                val trimmedLine = line!!.trim()
                
                // Log first few lines to see the structure
                if (lineCount <= 5) {
                    println("DEBUG: Line $lineCount: $trimmedLine")
                }
                
                // Look for notification record start
                if (trimmedLine.startsWith("NotificationRecord(")) {
                    println("DEBUG: Found notification record at line $lineCount")
                    // Save previous notification if exists
                    if (currentNotificationDump.isNotEmpty() && inNotificationRecord) {
                        val notification = parseSimpleNotification(currentNotificationDump.toString())
                        if (notification != null) {
                            notificationsList.add(notification)
                            println("DEBUG: Added notification: ${notification.packageName}")
                        }
                    }
                    currentNotificationDump = StringBuilder()
                    inNotificationRecord = true
                    currentNotificationDump.append(trimmedLine).append("\n")
                }
                // Look for next notification record or end of section
                else if (inNotificationRecord) {
                    if (trimmedLine.startsWith("NotificationRecord(") || 
                        trimmedLine.startsWith("AggregatedStats{") ||
                        trimmedLine.startsWith("GroupHelper:")) {
                        // End of current notification
                        val notification = parseSimpleNotification(currentNotificationDump.toString())
                        if (notification != null) {
                            notificationsList.add(notification)
                            println("DEBUG: Added notification: ${notification.packageName}")
                        }
                        
                        if (trimmedLine.startsWith("NotificationRecord(")) {
                            currentNotificationDump = StringBuilder()
                            currentNotificationDump.append(trimmedLine).append("\n")
                        } else {
                            inNotificationRecord = false
                            currentNotificationDump = StringBuilder()
                        }
                    } else {
                        currentNotificationDump.append(trimmedLine).append("\n")
                    }
                }
            }
            
            // Save the last notification
            if (currentNotificationDump.isNotEmpty() && inNotificationRecord) {
                val notification = parseSimpleNotification(currentNotificationDump.toString())
                if (notification != null) {
                    notificationsList.add(notification)
                    println("DEBUG: Added final notification: ${notification.packageName}")
                }
            }
            
            val exitCode = process.waitFor()
            println("DEBUG: Process exited with code: $exitCode")
            println("DEBUG: Total lines read: $lineCount")
            println("DEBUG: Total notifications found: ${notificationsList.size}")
            
        } catch (e: Exception) {
            println("DEBUG: Exception in getNotificationsAll: ${e.message}")
            e.printStackTrace()
        }
        
        caughtNotificationList = notificationsList
        return notificationsList
    }
    
    private fun parseSimpleNotification(dump: String): NotificationMaster? {
        val lines = dump.split("\n")
        var packageName = ""
        var title = ""
        var text = ""
        var postTime = 0L
        var key = ""
        
        for (line in lines) {
            val trimmedLine = line.trim()
            
            // Extract package from NotificationRecord line
            if (trimmedLine.startsWith("NotificationRecord(") && trimmedLine.contains("pkg=")) {
                packageName = trimmedLine.substringAfter("pkg=").substringBefore(" ").trim()
                println("DEBUG: Extracted package: $packageName")
            }
            
            // Extract key from NotificationRecord line
            if (trimmedLine.startsWith("NotificationRecord(") && trimmedLine.contains(" key=")) {
                key = trimmedLine.substringAfter(" key=").substringBefore(":").trim()
                println("DEBUG: Extracted key: $key")
            }
            
            // Extract title from android.title
            if (trimmedLine.contains("android.title=")) {
                title = trimmedLine.substringAfter("android.title=").substringBefore(",").trim()
                // Clean up title if it's in format "String [length=X]"
                if (title.startsWith("String [length=")) {
                    title = "Notification"
                }
                println("DEBUG: Extracted title: $title")
            }
            
            // Extract text from android.text
            if (trimmedLine.contains("android.text=")) {
                text = trimmedLine.substringAfter("android.text=").substringBefore(",").trim()
                // Clean up text if it's in format "String [length=X]"
                if (text.startsWith("String [length=")) {
                    text = "Content"
                }
                println("DEBUG: Extracted text: $text")
            }
            
            // Extract post time from when
            if (trimmedLine.contains("when=")) {
                val whenValue = trimmedLine.substringAfter("when=").substringBefore("\n").trim()
                try {
                    postTime = whenValue.toLongOrNull() ?: 0L
                    println("DEBUG: Extracted postTime: $postTime")
                } catch (e: Exception) {
                    println("DEBUG: Could not parse postTime: $whenValue")
                }
            }
        }
        
        // If we don't have title or text, use package name as fallback
        if (title.isEmpty() && text.isEmpty()) {
            title = getPackageDisplayName(packageName)
            text = "Notification from $packageName"
        }
        
        return if (packageName.isNotEmpty()) {
            NotificationMaster(
                notification_key = key,
                packageName = packageName,
                title = title,
                text = text,
                postTime = postTime,
                rawdata = listOf(mutableMapOf("dump" to dump))
            )
        } else {
            null
        }
    }
    
    private fun parseNotificationData(data: MutableMap<String, String?>): MutableMap<String, String?>? {
        // Filter out empty or irrelevant data
        val filteredData = data.filter { (key, value) ->
            key.isNotEmpty() && (value != null && value.isNotEmpty() || key == "raw_line")
        }.toMutableMap()
        
        return if (filteredData.isNotEmpty()) filteredData else null
    }
    
    fun getPackageDisplayName(packageName: String): String {
        return when {
            packageName.contains("phonepe") -> "PhonePe"
            packageName.contains("rapido") -> "Rapido"
            packageName.contains("gmail") -> "Gmail"
            packageName.contains("dialer") -> "Phone"
            packageName.contains("calendar") -> "Calendar"
            packageName.contains("reddit") -> "Reddit"
            packageName.contains("meesho") -> "Meesho"
            packageName.contains("icici") -> "ICICI Bank"
            packageName.contains("photo") -> "Photo App"
            else -> packageName.split(".").lastOrNull()?.replaceFirstChar { 
                if (it.isLowerCase()) it.titlecase() else it.toString() 
            } ?: packageName
        }
    }
}
