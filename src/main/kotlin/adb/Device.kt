package adb


data class Device(
    val id: String,
    val name: String,
    val osVersion: String?,
    val modelName: String?,
    val marketName: String?
) {
    fun getHumanDeviceName(): String {
        return marketName ?: modelName ?: id
    }

    fun getHumanDeviceNameSelection(): String {
        return (marketName ?: modelName ?: id) + " Android $osVersion"
    }
}