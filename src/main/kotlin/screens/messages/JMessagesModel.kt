package screens.messages

import adb.DeviceModel
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class JMessagesModel {
    companion object {
        val PROP_messages_list = "messages_list"
        val PROP_search_text_query_messages = "search_text_query_messages"
        val PROP_show_original_messages = "show_original_messages"


    }
    var deviceModel: DeviceModel? = null
        private set

    fun setDeviceModel(deviceModelNew: DeviceModel?) {
        deviceModel = deviceModelNew
    }

    private val support = PropertyChangeSupport(this)

    var messages_list = mutableListOf<MutableMap<String, String?>>()
        private set

    fun setMessagesList(list: MutableList<MutableMap<String, String?>>) {
        val oldScreen = messages_list
        messages_list = list
        support.firePropertyChange(PROP_messages_list, oldScreen, list)
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
        support.firePropertyChange(PROP_search_text_query_messages, oldScreen, name)
    }

    var showOriginal: Boolean = false
        private set

    fun setShowOriginal(name:Boolean) {
        val oldScreen = showOriginal
        showOriginal = name
        support.firePropertyChange( PROP_show_original_messages, oldScreen, name)
    }
}