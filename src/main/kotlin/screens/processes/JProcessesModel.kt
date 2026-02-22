package screens.processes

import adb.DeviceModel
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class JProcessesModel {
    companion object {
        val PROP_process_list = "process_list"
        val PROP_search_text_query = "search_text_query"
    }

    var deviceModel: DeviceModel? = null
        private set

    fun setDeviceModel(deviceModelNew: DeviceModel?) {
        deviceModel = deviceModelNew
    }

    private val support = PropertyChangeSupport(this)

    var process_list = mutableListOf<MutableMap<String, String?>>()
        private set

    fun setProcessList(list: MutableList<MutableMap<String, String?>>) {
        val oldList = process_list
        process_list = list
        support.firePropertyChange(PROP_process_list, oldList, list)
    }

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        support.addPropertyChangeListener(listener)
    }

    var searchTextQuery: String? = null
        private set

    fun setSearchTextQuery(text: String?) {
        val old = searchTextQuery
        searchTextQuery = text
        support.firePropertyChange(PROP_search_text_query, old, text)
    }
}
