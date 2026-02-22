package screens.packages

data class PackageMetadata(
    // Core app information
    var appId: String? = null,
    var pkg: String? = null,
    var versionName: String? = null,
    var versionCode: String? = null,
    var minSdk: String? = null,
    var targetSdk: String? = null,

    // Installation and update details
    var installerPackageName: String? = null,
    var installerPackageUid: String? = null,
    var initiatingPackageName: String? = null,
    var originatingPackageName: String? = null,
    var updateOwnerPackageName: String? = null,
    var packageSource: String? = null,
    var timeStamp: String? = null,
    var lastUpdateTime: String? = null,

    // Technical details
    var codePath: String? = null,
    var resourcePath: String? = null,
    var legacyNativeLibraryDir: String? = null,
    var extractNativeLibs: String? = null,
    var primaryCpuAbi: String? = null,
    var usesNonSdkApi: String? = null,
    var isMiuiPreinstall: String? = null,
    var splits: String? = null,
    var apkSigningVersion: String? = null,
    var flags: String? = null,
    var privateFlags: String? = null,
    var forceQueryable: String? = null,
    var queriesPackages: String? = null,
    var queriesIntents: String? = null,
    var dataDir: String? = null,
    var supportsScreens: String? = null,
    var appMetadataFilePath: String? = null,
    var installPermissionsFixed: String? = null
)