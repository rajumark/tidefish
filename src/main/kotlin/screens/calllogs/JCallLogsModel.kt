package screens.calllogs

import adb.DeviceModel
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class JCallLogsModel {
    companion object {
        val PROP_calls_list = "calls_list"
        val PROP_search_text_query_call = "search_text_query_call"


    }
    var deviceModel: DeviceModel? = null
        private set

    fun setDeviceModel(deviceModelNew: DeviceModel?) {
        deviceModel = deviceModelNew
    }

    private val support = PropertyChangeSupport(this)

    var calls_list = mutableListOf<MutableMap<String, String?>>()
        private set

    fun setCallList(list: MutableList<MutableMap<String, String?>>) {
        val oldScreen = calls_list
        calls_list = list
        support.firePropertyChange(PROP_calls_list, oldScreen, list)
    }

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        support.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        support.removePropertyChangeListener(listener)
    }


    var searchTextQuery: String? = null
        private set

    fun setSearchTextQuery(name: String?) {
        val oldScreen = searchTextQuery
        searchTextQuery = name
        support.firePropertyChange(PROP_search_text_query_call, oldScreen, name)
    }
}