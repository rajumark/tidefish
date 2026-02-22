package screens.packages

data class PackageInfo(
    val activities: MutableList<ActivityInfo>,
    val receivers: MutableList<ReceiverInfo>,
    val services: MutableList<ServiceInfo>,
    val permissions: MutableList<PermissionInfo>,
    val contentProviders: MutableList<ContentProviderInfo>
)