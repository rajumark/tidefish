package screens.deviceproperties


import adb.DeviceModel
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class DevicePropertiesModel {
    companion object {
        const val PROP_device_properties_list = "device_properties_list"
        const val PROP_search_text_query_device = "search_text_query_device"
    }

    var deviceModel: DeviceModel? = null
        private set

    private val support = PropertyChangeSupport(this)

    var devicePropertiesList = mutableListOf<SettingItem>()
        private set

    var searchTextQuery: String? = null
        private set

    fun setDeviceModel(deviceModelNew: DeviceModel?) {
        deviceModel = deviceModelNew
    }

    fun setDevicePropertiesList(list: List<SettingItem> ) {
        val oldList = devicePropertiesList
        devicePropertiesList = list.toMutableList()
        support.firePropertyChange(PROP_device_properties_list, oldList, list)
    }

    fun setSearchTextQuery(query: String?) {
        val oldQuery = searchTextQuery
        searchTextQuery = query
        support.firePropertyChange(PROP_search_text_query_device, oldQuery, query)
    }

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        support.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        support.removePropertyChangeListener(listener)
    }
}
