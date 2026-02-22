package components.topbar

import adb.DeviceModel


class TopbarViewController(model: TopbarViewModel, view: TopbarView) {
    var onItemChange:( (DeviceModel?) -> Unit)?=null

    init {
        view.deviceSelectorView.onItemChange = {
            model.setThisDeviceModel(it)
            onItemChange?.invoke(it)
        }
    }
}