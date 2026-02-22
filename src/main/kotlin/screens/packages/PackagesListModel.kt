package screens.packages

import adb.DeviceModel
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class PackagesListModel {
    companion object {
        val PROP_apps_list = "apps_list"
        val PROP_current_activity_name = "current_activity_name"
        val PROP_search_text_query = "search_text_query"
        val PROP_appType = "appType"
    }

    var deviceModel: DeviceModel? = null
        private set

    fun setDeviceModel(deviceModelNew: DeviceModel?) {
        deviceModel = deviceModelNew
    }

    private val support = PropertyChangeSupport(this)

      var apps_list = mutableListOf<String>()
          private set

    fun setAppsList(list: MutableList<String>) {
        val oldScreen = apps_list
        apps_list = list
        support.firePropertyChange(PROP_apps_list, oldScreen, list)
    }

     var currentActivityName: String? = null
         private set

    fun setCurrentActivityName(name: String?) {
        val oldScreen = currentActivityName
        currentActivityName = name
        support.firePropertyChange(PROP_current_activity_name, oldScreen, name)
    }


    var searchTextQuery: String? = null
        private set

    fun setSearchTextQuery(name: String?) {
        val oldScreen = searchTextQuery
        searchTextQuery = name
        support.firePropertyChange(PROP_search_text_query, oldScreen, name)
    }


    var appType: AppType = AppType.USER_APPS
        private set

    fun setAppType(name: AppType) {
        val oldScreen = appType
        appType = name
        support.firePropertyChange(PROP_appType, oldScreen, name)
    }


    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        support.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        support.removePropertyChangeListener(listener)
    }



}