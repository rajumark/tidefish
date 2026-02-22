package adb

data class DeviceModel(
    val id: String,
    val osVersion: String?,
) {
    fun getComboID(): String {
        return "$id-$osVersion"

    }
}