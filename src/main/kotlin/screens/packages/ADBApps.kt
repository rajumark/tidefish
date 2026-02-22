package screens.packages

import adb.ADBConst
import adb.ADBHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.ocpsoft.prettytime.PrettyTime
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


object ADBApps {

    val installTimeCache = ConcurrentHashMap<Pair<String, String>, Long>()


    fun getInstalledAppPackages(id: String): List<AppsBox> {

        try {
            val pinList = TextDatabase.getPinList()
            val process = Runtime.getRuntime().exec(arrayOf(ADBConst.path, "-s", id, "shell", "pm", "list", "packages"))
            val output = process.inputStream.bufferedReader().readLines()
            val packagelist = output.filter { it.startsWith("package:") }
                .map { it.substring(8) }.map { pkgName ->
                    pkgName
                }

            val listWithTime: List<AppsBox> = fetchAppsBoxList(packagelist, pinList, id)
            /*val modelList=packagelist.map {pkgName ->
                val installTime = getFirstInstallTime(id, pkgName)
                AppsBox(
                    packageName = pkgName,
                    installTime = installTime,
                    isPined = pinList.contains(pkgName)
                )
            }*/

            return listWithTime.sortedByDescending { it.installTime }
        } catch (e: Exception) {
            return emptyList()
        }
    }

    fun fetchAppsBoxList(
        packageList: List<String>,
        pinList: List<String>,
        id: String
    ): List<AppsBox> {
        // Create a fixed thread pool with a maximum of 40 threads
        val executor = Executors.newFixedThreadPool(40)

        // List to hold Futures for each task
        val futures = packageList.map { pkgName ->
            executor.submit<AppsBox> {
                // Check cache first
                val cacheKey = id to pkgName
                val installTime = installTimeCache.getOrPut(cacheKey) {
                    // Only fetch install time if not in cache
                    getFirstInstallTime(id, pkgName)
                }
                // Create and return AppsBox object
                AppsBox(
                    packageName = pkgName,
                    installTime = installTime,
                    isPined = pinList.contains(pkgName)
                )
            }
        }

        // Wait for all tasks to complete and collect results
        val modelList = futures.map { it.get() }

        // Shut down the executor service
        executor.shutdown()
        executor.awaitTermination(1, TimeUnit.HOURS)

        return modelList
    }

    fun getFirstInstallTime(id: String, packageName: String): Long? {
        return try {
            // Build the command to execute
            val command =
                "${ADBConst.path} -s $id shell dumpsys package $packageName | grep 'firstInstallTime' | awk -F'=' '{print \$2}'"

            // Create a process to execute the command
            val process = ProcessBuilder(*command.split(" ").toTypedArray()).redirectErrorStream(true).start()

            // Read the output of the command
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readLine() // Read the first line of output

            // Wait for the process to complete
            process.waitFor()
            if (output?.trim().isNullOrBlank()) {
               // println("anroidnullpack:" + packageName)
            }
            convertDateToMillis(output?.trim())

        } catch (e: Exception) {
            e.printStackTrace()
            null // Return null if there's an exception
        }
    }

    fun convertDateToMillis(dateStr: String?): Long? {
        // Define the date format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            // Parse the date string into a Date object
            val date: Date = dateFormat.parse(dateStr)
            // Return the time in milliseconds
            return date.getTime()
        } catch (e: Exception) {

            return null
        }
    }

    fun startApp(packageName: String, id: String) {
        try {
            //adb shell am start -n
            //  working  //adb shell monkey -p com.willme.topactivity -c android.intent.category.LAUNCHER 1
            val process = Runtime.getRuntime().exec(
                arrayOf(
                    ADBConst.path,
                    "-s",
                    id,
                    "shell",
                    "monkey",
                    "-p",
                    "$packageName",
                    "-c",
                    "android.intent.category.LAUNCHER",
                    "1"
                )
            )
            // val process = Runtime.getRuntime().exec(arrayOf(ADBpathMac, "shell", "am", "start","-n",  packageName))
            process.waitFor() // Wait for the command to complete
          //  println("$packageName opened successfully.")
        } catch (e: Exception) {
            e.printStackTrace()
           // println("Failed to open $packageName.")
        }
    }

    fun forceStopApp(packageName: String, id: String) {
        try {
            val process =
                Runtime.getRuntime().exec(arrayOf(ADBConst.path, "-s", id, "shell", "am", "force-stop", packageName))
            process.waitFor() // Wait for the command to complete
          //  println("$packageName force stopped successfully.")
        } catch (e: Exception) {
            e.printStackTrace()
           // println("Failed to force stop $packageName.")
        }
    }

    fun clearAppData(packageName: String, id: String) {
        try {
            val process =
                Runtime.getRuntime().exec(arrayOf(ADBConst.path, "-s", id, "shell", "pm", "clear", packageName))
            process.waitFor() // Wait for the command to complete
         //   println("$packageName clear data successfully.")
        } catch (e: Exception) {
            e.printStackTrace()
         //   println("Failed to clear data $packageName.")
        }
    }

    fun openAppSettings(packageName: String, id: String) {
        try {
            // Construct the command to open app settings
            val command = arrayOf(
                ADBConst.path,
                "-s",
                id,
                "shell",
                "am",
                "start",
                "-a",
                "android.settings.APPLICATION_DETAILS_SETTINGS",
                "-d",
                "package:$packageName" // No space between 'package:' and packageName
            )

            // Execute the command
            val process = Runtime.getRuntime().exec(command)

            // Wait for the command to complete
            val exitCode = process.waitFor()

            // Check if the command was successful
            if (exitCode == 0) {
              //  println("$packageName opened app settings successfully.")
            } else {
               // println("Failed to open app settings for $packageName. Exit code: $exitCode")
            }
        } catch (e: Exception) {
            e.printStackTrace()
           // println("Error occurred while trying to open app settings for $packageName.")
        }
    }

    fun home(packageName: String, id: String) {
        try {
            //adb shell input keyevent KEYCODE_HOME
            // Construct the command to open app settings
            val command = arrayOf(
                ADBConst.path,
                "-s",
                id,
                "shell",
                "input",
                "keyevent",
                "KEYCODE_HOME",
            )

            // Execute the command
            val process = Runtime.getRuntime().exec(command)

            // Wait for the command to complete
            val exitCode = process.waitFor()

            // Check if the command was successful
            if (exitCode == 0) {
              //  println("$packageName opened app settings successfully.")
            } else {
               // println("Failed to open app settings for $packageName. Exit code: $exitCode")
            }
        } catch (e: Exception) {
            e.printStackTrace()
           // println("Error occurred while trying to open app settings for $packageName.")
        }
    }

    fun restartApp(s: String, id: String) {
        forceStopApp(s, id)
        startApp(s, id)
    }
    fun enableDisableApp(packageName: String, id: String, makeEnable: Boolean) {
        try {
            val command = if (makeEnable) {
                // Command to enable the app
                arrayOf(ADBConst.path, "-s", id, "shell", "pm", "enable", packageName)
            } else {
                // Command to disable the app
                arrayOf(ADBConst.path, "-s", id, "shell", "pm", "disable-user", packageName)
            }

            val process = Runtime.getRuntime().exec(command)
            process.waitFor() // Wait for the command to complete

            if (makeEnable) {
             //   println("$packageName enabled successfully.")
            } else {
              //  println("$packageName disabled successfully.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
           // println("Failed to ${if (makeEnable) "enable" else "disable"} $packageName.")
        }
    }
    fun uninstallApp(packageName: String, id: String) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf(ADBConst.path, "-s", id, "uninstall", packageName))
            process.waitFor() // Wait for the command to complete
          //  println("$packageName uninstalled successfully.")
        } catch (e: Exception) {
            e.printStackTrace()
            //println("Failed to uninstall $packageName.")
        }
    }

    fun downloadAPK( packageName: String, id: String) {
        try {
            val outputDir = getDownloadPath() ?: return
            val fileOutFolder=File(outputDir,packageName)
            if (fileOutFolder.exists().not()) {
                fileOutFolder.mkdirs()
            }
             val success = pullApkByPackageName( packageName, fileOutFolder.absolutePath, id)
            if (success) {
               // println("APK file pulled successfully to $outputDir")
            } else {
              //  println("Failed to pull APK file")
            }
        } catch (e: Exception) {
           // println("AdbStudioLog:error" + e)
        }
    }

    fun getDownloadPath(): String? {
        val os = System.getProperty("os.name")?.toLowerCase()
       // println("AdbStudioLog:os" + os)
       // println("AdbStudioLog:os" + System.getenv("HOME")?.plus("/Downloads"))

        if (os?.contains("mac") == true) {
            return System.getenv("HOME")?.plus("/Downloads/")
        } else {
            return System.getenv("USERPROFILE")?.plus("\\Downloads")
        }
        /*return when (os) {
            "windows" -> System.getenv("USERPROFILE")?.plus("\\Downloads")
            "linux" -> System.getenv("HOME")?.plus("/Downloads")
            "mac" -> System.getenv("HOME")?.plus("/Downloads")
            else -> null
        }*/
    }

    fun getAllRuntimePermissions(packageName: String, id: String): Map<String, List<PermissionInfo>> {
        val permissionsMap = mutableMapOf<String, MutableList<PermissionInfo>>()
        try {
            val process = Runtime.getRuntime().exec(arrayOf(ADBConst.path, "-s", id, "shell", "dumpsys", "package", packageName))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            var currentGroup: String? = null

            while (reader.readLine().also { line = it } != null) {
                line?.let {
                    when {
                        it.contains("requested permissions:") -> currentGroup = "requested_permissions"
                        it.contains("install permissions:") -> currentGroup = "install_permissions"
                        it.contains("runtime permissions:") -> currentGroup = "runtime_permissions"
                        it.isBlank() -> currentGroup = null
                        currentGroup != null -> {
                            val permission = it.extractPermission() ?: return@let

                            val granted = when (currentGroup) {
                                "runtime_permissions", "install_permissions" -> it.contains("granted=true")
                                "requested_permissions" -> false // requested permissions don't have granted info
                                else -> false
                            }

                            val permissionInfo = PermissionInfo(permission, granted)
                            permissionsMap.getOrPut(currentGroup) { mutableListOf() }.add(permissionInfo)
                        }
                    }
                }
            }

            reader.close()
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }


        return permissionsMap
    }


    fun grantAllPermissions(packageName: String, permissions: List<String>, id: String) {
        permissions.forEach { permission ->
            try {
                val process = Runtime.getRuntime()
                    .exec(arrayOf(ADBConst.path, "-s", id, "shell", "pm", "grant", packageName, permission))
                process.waitFor() // Wait for the command to complete
             //   println("$permission granted to $packageName successfully.")
            } catch (e: Exception) {
                e.printStackTrace()
               // println("Failed to grant $permission to $packageName.")
            }
        }
    }

    fun revokeAllPermissions(packageName: String, permissions: List<String>, id: String) {
        permissions.forEach { permission ->
            try {
                val process = Runtime.getRuntime()
                    .exec(arrayOf(ADBConst.path, "-s", id, "shell", "pm", "revoke", packageName, permission))
                process.waitFor() // Wait for the command to complete
              //  println("$permission revoked from $packageName successfully.")
            } catch (e: Exception) {
                e.printStackTrace()
               // println("Failed to revoke $permission from $packageName.")
            }
        }
    }


    fun pullApkByPackageName(  packageName: String, outputDir: String, id: String): Boolean {
        return try {
            // Get the APK path using adb shell command
            val getApkPathCommand = "${ADBConst.path} -s $id shell pm path $packageName"
            val apkPathout = ADBHelper.executeCommand(getApkPathCommand)
           // println("apkPathout="+apkPathout)
            val apksList=apkPathout.orEmpty().lines().filter {
                it.trim().contains(packageName)
            }.mapNotNull { singlePackage->
                singlePackage.trim().split(":").getOrNull(1)?.trim()
            }

            if (apksList.isEmpty()) {
               return false
            }

            // Pull the APK file from the device
            runBlocking {
                pullApksInParallel(apksList, id, outputDir)
            }
            try {
                //val newFile = renameFile(apkFile, packageName + ".apk")
                openFileInExplorer(File(outputDir))
            } catch (e: Exception) {
              // println("ADBCard:D2==" + e.message)
            }

            true
            /* if (apkFile.exists()) {
                 println("ADBCard:D2==" + apkFileName)
                 val newFile = renameFile(apkFile, packageName + ".apk")
                 openFileInExplorer(newFile)
                 println("ADBCard:D3==" + apkFileName)
                 true
             } else {
                 println("ADBCard:D4==" + apkFileName)
                 false
             }*/

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun pullApksInParallel(apksList: List<String>, id: String, outputDir: String) {
        coroutineScope {
            apksList.forEach { apkPath ->

                launch(Dispatchers.IO) {
                    val pullApkCommand = "${ADBConst.path} -s $id pull $apkPath $outputDir"
                  //  println("ADBCard:pullApkCommand= $pullApkCommand")
                    val pullResult = ADBHelper.executeCommand(pullApkCommand)
                  //  println("ADBCard: path=$pullResult")
                }
            }
        }
    }

    fun openFileInExplorer(file: File) {


        try {
            val osName = System.getProperty("os.name").toLowerCase()
            when {
                osName.contains("win") -> {
                    // Windows
                    Runtime.getRuntime().exec("explorer.exe /select,${file.absolutePath}")
                }

                osName.contains("mac") -> {
                    // macOS
                    Runtime.getRuntime().exec("open -R ${file.absolutePath}")
                }

                osName.contains("nix") || osName.contains("nux") -> {
                    // Linux (assuming GNOME)
                    Runtime.getRuntime().exec(arrayOf("xdg-open", file.parentFile.absolutePath))
                }

                else -> {
                   // println("Unsupported operating system: $osName")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun renameFile(oldFile: File, newFilename: String): File {

        val newPath = File(oldFile.parent, newFilename)
        try {
            if (oldFile.renameTo(newPath)) {
              //  println("File renamed successfully: ${oldFile.absolutePath} -> $newPath")
            } else {
              //  println("Failed to rename file!")
            }
        } catch (e: Exception) {
            //println("ADBCard:renameFile==" + e.message)

        }


        return newPath
    }


    fun toRelativeTime(timestamp: Long?): String {
        if (timestamp == null) return "Other"
        return try {
            // Create a calendar instance and set time to the given timestamp
            val calendar = Calendar.getInstance().apply {
                timeInMillis = timestamp
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // Use PrettyTime with the adjusted date
            val prettyTime = PrettyTime()
            prettyTime.format(calendar.time)

        } catch (e: Exception) {
            "Other"
        }
    }

    fun buildPlayStoreUrl(packageName: String): String {
        return "https://play.google.com/store/apps/details?id=$packageName"
    }

    fun buildFindInMarketUrl(packageName: String): String {
        return "https://www.google.co.in/search?q=download+$packageName+APK"
    }

    fun openUrlInAndroidBrowser(url: String, deviceId: String? = null) {
        try {
            // Construct the ADB command
            val command = if (deviceId != null) {
                arrayOf(ADBConst.path, "-s", deviceId, "shell", "am", "start", "-a", "android.intent.action.VIEW", "-d", url)
            } else {
                arrayOf(ADBConst.path, "shell", "am", "start", "-a", "android.intent.action.VIEW", "-d", url)
            }

            // Execute the command
            val process = Runtime.getRuntime().exec(command)

            // Wait for the command to complete
            val exitCode = process.waitFor()

            // Check if the command was successful
            if (exitCode == 0) {
              //  println("Successfully opened URL: $url")
            } else {
              //  println("Failed to open URL: $url. Exit code: $exitCode")
            }
        } catch (e: Exception) {
            e.printStackTrace()
           // println("Error occurred while trying to open URL: $url")
        }
    }


}