package screens.packages.packagedetails

import adb.ADBConst
import screens.packages.PackageMetadata
import java.io.BufferedReader
import java.io.InputStreamReader

fun getPackageMetadataInfoByPackageName(id: String, packageName: String): PackageMetadata? {
    try {
        val command = "${ADBConst.path} -s $id shell dumpsys package $packageName"
        val process = Runtime.getRuntime().exec(command)
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        val model = PackageMetadata()

        var line: String?

        while (reader.readLine().also { line = it?.trim() } != null) {
            when {
                line!!.startsWith("appId=") -> model.appId = line!!.substringAfter("appId=")
                line!!.startsWith("pkg=") -> model.pkg = packageName
                line!!.startsWith("codePath=") -> model.codePath = line!!.substringAfter("codePath=")
                line!!.startsWith("resourcePath=") -> model.resourcePath = line!!.substringAfter("resourcePath=")
                line!!.startsWith("legacyNativeLibraryDir=") -> model.legacyNativeLibraryDir =
                    line!!.substringAfter("legacyNativeLibraryDir=")

                line!!.startsWith("extractNativeLibs=") -> model.extractNativeLibs =
                    line!!.substringAfter("extractNativeLibs=")

                line!!.startsWith("primaryCpuAbi=") -> model.primaryCpuAbi = line!!.substringAfter("primaryCpuAbi=")
                line!!.contains("versionCode=") && line!!.contains("minSdk=") -> {
                    //versionCode=171 minSdk=24 targetSdk=34
                    splitDataVersionCode(line, model)
                }

                line!!.startsWith("versionName=") -> model.versionName = line!!.substringAfter("versionName=")
                line!!.startsWith("usesNonSdkApi=") -> model.usesNonSdkApi = line!!.substringAfter("usesNonSdkApi=")
                line!!.startsWith("isMiuiPreinstall=") -> model.isMiuiPreinstall =
                    line!!.substringAfter("isMiuiPreinstall=")

                line!!.startsWith("splits=") -> model.splits = line!!.substringAfter("splits=")
                line!!.startsWith("apkSigningVersion=") -> model.apkSigningVersion =
                    line!!.substringAfter("apkSigningVersion=")

                line!!.startsWith("flags=") -> model.flags = line!!.substringAfter("flags=")
                line!!.startsWith("privateFlags=") -> model.privateFlags = line!!.substringAfter("privateFlags=")
                line!!.startsWith("forceQueryable=") -> model.forceQueryable = line!!.substringAfter("forceQueryable=")
                line!!.startsWith("queriesPackages=") -> model.queriesPackages =
                    line!!.substringAfter("queriesPackages=")

                line!!.startsWith("queriesIntents=") -> model.queriesIntents = line!!.substringAfter("queriesIntents=")
                line!!.startsWith("dataDir=") -> model.dataDir = line!!.substringAfter("dataDir=")
                line!!.startsWith("supportsScreens=") -> model.supportsScreens =
                    line!!.substringAfter("supportsScreens=")

                line!!.startsWith("timeStamp=") -> model.timeStamp = line!!.substringAfter("timeStamp=")
                line!!.startsWith("lastUpdateTime=") -> model.lastUpdateTime = line!!.substringAfter("lastUpdateTime=")
                line!!.startsWith("installerPackageName=") -> model.installerPackageName =
                    line!!.substringAfter("installerPackageName=")

                line!!.startsWith("installerPackageUid=") -> model.installerPackageUid =
                    line!!.substringAfter("installerPackageUid=")

                line!!.startsWith("initiatingPackageName=") -> model.initiatingPackageName =
                    line!!.substringAfter("initiatingPackageName=")

                line!!.startsWith("originatingPackageName=") -> model.originatingPackageName =
                    line!!.substringAfter("originatingPackageName=")

                line!!.startsWith("updateOwnerPackageName=") -> model.updateOwnerPackageName =
                    line!!.substringAfter("updateOwnerPackageName=")

                line!!.startsWith("packageSource=") -> model.packageSource = line!!.substringAfter("packageSource=")
                line!!.startsWith("appMetadataFilePath=") -> model.appMetadataFilePath =
                    line!!.substringAfter("appMetadataFilePath=")

                line!!.startsWith("installPermissionsFixed=") -> model.installPermissionsFixed =
                    line!!.substringAfter("installPermissionsFixed=")

            }
        }
        return model
    } catch (e: Exception) {
        return null
    }
}


private fun splitDataVersionCode(line: String?, model: PackageMetadata) {
    line!!.split(" ").forEach { subline ->
        if (subline.startsWith("versionCode")) {
            model.versionCode = subline.substringAfter("versionCode=")
        }
        if (subline.startsWith("minSdk")) {
            model.minSdk = subline.substringAfter("minSdk=")
        }
        if (subline.startsWith("targetSdk")) {
            model.targetSdk = subline.substringAfter("targetSdk=")
        }
    }
}


fun getAppSize(id: String, packageName: String): String {
    val apkPaths = mutableListOf<String>()

    // Get the APK paths
    val pathCommand = "${ADBConst.path} -s $id shell pm path $packageName"
    val pathProcess = Runtime.getRuntime().exec(pathCommand)
    val pathReader = BufferedReader(InputStreamReader(pathProcess.inputStream))

    pathReader.use { reader ->
        reader.lines().forEach { line ->
            // Extract the path from the output
            val path = line.substringAfter("package:").trim()
            apkPaths.add(path)
        }
    }

    // Get the size of each APK
    var totalSize = 0L
    apkPaths.forEach { apkPath ->
        val sizeCommand = "${ADBConst.path} -s $id shell stat $apkPath"
        val sizeProcess = Runtime.getRuntime().exec(sizeCommand)
        val sizeReader = BufferedReader(InputStreamReader(sizeProcess.inputStream))

        sizeReader.use { reader ->
            reader.lines().forEach { line ->
                // Extract size from the output
                val size = line.substringAfter("Size:").trim().split("\\s+".toRegex()).firstOrNull()?.toLongOrNull()
                if (size != null) {
                    totalSize += size
                }
            }
        }
    }

    // Convert total size to a human-readable format
    return formatSize(totalSize)
}


fun formatSize(sizeInBytes: Long): String {
    return when {
        sizeInBytes >= 1_048_576 -> String.format("%.2f MB", sizeInBytes / 1_048_576.0)
        sizeInBytes >= 1_024 -> String.format("%.2f KB", sizeInBytes / 1_024.0)
        else -> "$sizeInBytes Bytes"
    }
}