package screens.settings


import adb.DeviceModel
import java.awt.BorderLayout
import javax.swing.JPanel


class JSettingsListPage( ) : JPanel() {
    private var selectedDevice: DeviceModel?=null
    private val settingsView = JSettingsButtonsView()

    fun setDeviceModel(  selectedDeviceNew: DeviceModel?){
        if(selectedDevice!=selectedDeviceNew) {
            selectedDevice = selectedDeviceNew
        }
    }

    init {
        layout = BorderLayout()
        add(settingsView, BorderLayout.CENTER)

        settingsView.onItemClick = { item ->
            selectedDevice?.let { device ->
                ADBSettings.openSettingByName(device.id, item.intent)
            }
        }
        settingsView.onRefreshClick = {
            settingsView.submitSettings(SettingsDataList)
        }
    }
}