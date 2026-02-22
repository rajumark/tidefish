package nav

import adb.DeviceModel
import first.navigation.TypeOfScreens
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class NavPageModel {
    private val support = PropertyChangeSupport(this)

    var currentScreen: TypeOfScreens = TypeOfScreens.apps
        private set

    fun setCurrentScreen(screen: TypeOfScreens) {
        val oldScreen = currentScreen
        currentScreen = screen
        support.firePropertyChange("currentScreen", oldScreen, screen)
    }

    var deviceModel: DeviceModel? = null
        private set

    fun setDeviceModel(deviceModelNew: DeviceModel?) {
        val oldDevice = deviceModel
        deviceModel = deviceModelNew
        support.firePropertyChange("deviceModel", oldDevice, deviceModelNew)
    }

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        support.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        support.removePropertyChangeListener(listener)
    }
}