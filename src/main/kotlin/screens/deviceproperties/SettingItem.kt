package screens.deviceproperties


data class SettingItem(
    val type: SettingType,
    val key: String,
    val value: String?,
    val description: String? = null
)

enum class SettingType {
    SYSTEM, SECURE, GLOBAL
}
