package screens.apps.right.basic_info

object KeyDescriptions {
    private val map: Map<String, String> = mapOf(
        // Core app information
        "appId" to "Application ID (same as package name for Android app)",
        "pkg" to "Package name of the app",
        "versionName" to "Human-readable version name",
        "versionCode" to "Internal version code used by the system",
        "minSdk" to "Minimum Android SDK level required to run",
        "targetSdk" to "Target Android SDK level the app is optimized for",

        // Installation and update details
        "installerPackageName" to "Package responsible for installing this app",
        "installerPackageUid" to "UID of the installer package",
        "initiatingPackageName" to "Package that initiated installation",
        "originatingPackageName" to "Original source package for install",
        "updateOwnerPackageName" to "Package that owns updates for this app",
        "packageSource" to "Source of the package (e.g., system, user)",
        "timeStamp" to "Initial install timestamp",
        "lastUpdateTime" to "Last time the app was updated",

        // Technical details
        "codePath" to "Path to base APK code on device",
        "resourcePath" to "Path to resources for the app",
        "legacyNativeLibraryDir" to "Directory for native .so libraries (legacy)",
        "extractNativeLibs" to "Whether native libs are extracted from APK",
        "primaryCpuAbi" to "Primary supported CPU ABI",
        "usesNonSdkApi" to "Whether app uses non-SDK APIs",
        "isMiuiPreinstall" to "MIUI preinstalled application flag",
        "splits" to "APK splits present for this install",
        "apkSigningVersion" to "APK signing scheme version",
        "flags" to "Package flags (bitmask)",
        "privateFlags" to "Private package flags (bitmask)",
        "forceQueryable" to "Whether app is force-queryable for queries",
        "queriesPackages" to "Packages this app can query",
        "queriesIntents" to "Intents this app can query",
        "dataDir" to "App data directory on device",
        "supportsScreens" to "Screen sizes/densities supported",
        "appMetadataFilePath" to "Path to app metadata file",
        "installPermissionsFixed" to "Whether install-time permissions are fixed"
    )

    fun getDescription(key: String): String = map[key] ?: ""
}


