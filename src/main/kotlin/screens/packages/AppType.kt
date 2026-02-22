package screens.packages
enum class AppType(val displayName: String) {
    ALL_APPS("All Apps"),
    USER_APPS("User Apps"),
    SYSTEM_APPS("System Apps"),
    ENABLED_APPS("Enabled Apps"),
    DISABLED_APPS("Disabled Apps"),
    UNINSTALLED_APPS("Uninstalled Apps");

    companion object {

        fun options(): Array<AppType> {
            return entries.toTypedArray()
        }
    }
}