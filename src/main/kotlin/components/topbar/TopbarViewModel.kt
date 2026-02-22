package components.topbar

import adb.DeviceModel

class TopbarViewModel {
    var deviceModel: DeviceModel? = null
        private set

    fun setThisDeviceModel(it: DeviceModel?) {
        deviceModel = it
    }
}