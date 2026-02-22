package screens.apps

import adb.DeviceModel
import first.FirstPageModel
import first.FirstPageView

class AppsListController(private val model: AppsListModel, private val view: AppsListView) {
    private var selectedDevice: DeviceModel? = null

    fun setDeviceModel(selectedDeviceNew: DeviceModel?) {
        selectedDevice = selectedDeviceNew
        view.appsListLeftController.setDeviceModel(selectedDeviceNew)

    }

    init {
        setUIListener()
    }

    private fun setUIListener() {
        view.appsListLeftView.packageList.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                val selected = view.appsListLeftView.packageList.selectedValue
                view.appDetailsRightController.setSelectedPackageName(selectedDevice, selected)
                view.showDetailsPanel(selected != null && selected.isNotEmpty())
            }
        }
        // show placeholder initially
        view.showDetailsPanel(false)
    }


}