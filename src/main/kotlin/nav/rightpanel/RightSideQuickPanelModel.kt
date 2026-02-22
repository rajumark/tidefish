package nav

import adb.DeviceModel

class RightSideQuickPanelModel {
    var deviceModel: DeviceModel? = null
        private set

    fun setThisDeviceModel(it: DeviceModel?) {
        deviceModel = it
    }
}


