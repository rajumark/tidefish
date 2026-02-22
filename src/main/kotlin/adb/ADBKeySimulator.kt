package adb

object ADBKeySimulator {

    fun pressHome(id: String) {
        runKeyEvent(id, "3")
    }

    fun pressBack(id: String) {
        runKeyEvent(id, "4")
    }

    fun pressRecent(id: String) {
        runKeyEvent(id, "187")
    }

    fun pressVolumeUp(id: String) {
        runKeyEvent(id, "24")
    }

    fun pressVolumeDown(id: String) {
        runKeyEvent(id, "25")
    }

    fun openSettings(id: String) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf(ADBConst.path, "-s", id, "shell", "am", "start", "-a", "android.settings.SETTINGS"))
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pressPower(id: String) {
        runKeyEvent(id, "26")
    }

    fun longPressPower(id: String) {
        try {
            val down = Runtime.getRuntime().exec(arrayOf(ADBConst.path, "-s", id, "shell", "input", "keyevent", "--longpress", "26"))
            down.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun captureScreenshotToDesktop(deviceId: String): String {
        try {
            val desktop = java.io.File(System.getProperty("user.home"), "Desktop")
            if (!desktop.exists()) desktop.mkdirs()

            val date = java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS", java.util.Locale.getDefault()).format(java.util.Date())
            val fileName = "screenshot_${date}.png"
            val outputFile = java.io.File(desktop, fileName)

            val remote = "/sdcard/${fileName}"
            Runtime.getRuntime().exec(arrayOf(ADBConst.path, "-s", deviceId, "shell", "screencap", "-p", remote)).waitFor()
            Runtime.getRuntime().exec(arrayOf(ADBConst.path, "-s", deviceId, "pull", remote, outputFile.absolutePath)).waitFor()
            Runtime.getRuntime().exec(arrayOf(ADBConst.path, "-s", deviceId, "shell", "rm", remote)).waitFor()

            return outputFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    fun mediaPlay(id: String) {
        try {
            val process = Runtime.getRuntime().exec(
                arrayOf(ADBConst.path, "-s", id, "shell", "input", "keyevent", "126") // KEYCODE_MEDIA_PLAY
            )
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun mediaPause(id: String) {
        try {
            val process = Runtime.getRuntime().exec(
                arrayOf(ADBConst.path, "-s", id, "shell", "input", "keyevent", "127") // KEYCODE_MEDIA_PAUSE
            )
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun volumeMute(id: String) {
        runKeyEvent(id, "164")
    }

    fun expandQuickSettings(id: String) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf(ADBConst.path, "-s", id, "shell", "cmd", "statusbar", "expand-settings"))
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun expandNotifications(id: String) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf(ADBConst.path, "-s", id, "shell", "cmd", "statusbar", "expand-notifications"))
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun collapseAll(id: String) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf(ADBConst.path, "-s", id, "shell", "cmd", "statusbar", "collapse"))
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun unlockMenu(id: String) {
        runKeyEvent(id, "82")
    }

    fun openDeveloperSettings(id: String) {
        try {
            val process = Runtime.getRuntime().exec(
                arrayOf(
                    ADBConst.path, "-s", id, "shell", "am", "start",
                    "-a", "android.settings.APPLICATION_DEVELOPMENT_SETTINGS"
                )
            )
            process.waitFor()
           // println("Opened Developer Options via intent")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun showTaps(id: String) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf(ADBConst.path, "-s", id, "shell", "settings", "put", "system", "show_touches", "1"))
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideTaps(id: String) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf(ADBConst.path, "-s", id, "shell", "settings", "put", "system", "show_touches", "0"))
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun runKeyEvent(id: String, keyCode: String) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf(ADBConst.path, "-s", id, "shell", "input", "keyevent", keyCode))
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


