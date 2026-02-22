package screens.calendar


import adb.DeviceModel

import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class JCalendarModel {

    companion object {
        const val PROP_calendar_list = "calendar_list"
        const val PROP_search_text_query = "search_text_query"
        const val PROP_selected_data_type = "selected_data_type"
        const val PROP_show_original = "show_original"
    }

    private val support = PropertyChangeSupport(this)

    var deviceModel: DeviceModel? = null
        private set

    var calendarList: MutableList<MutableMap<String, String?>> = mutableListOf()
        private set

    var searchTextQuery: String? = null
        private set

    var selectedDataTypeIndex: Int = 0 // 0 = Events, 1 = Type
        private set

    var showOriginal: Boolean = false
        private set

    fun setDeviceModel(newDeviceModel: DeviceModel?) {
        if (deviceModel != newDeviceModel) {
            deviceModel = newDeviceModel
        }
    }

    fun setCalendarList(list: MutableList<MutableMap<String, String?>>) {
        val oldList = calendarList
        calendarList = list
        support.firePropertyChange(PROP_calendar_list, oldList, list)
    }

    fun setSearchTextQuery(query: String?) {
        val old = searchTextQuery
        searchTextQuery = query
        support.firePropertyChange(PROP_search_text_query, old, query)
    }

    fun setSelectedDataTypeIndex(index: Int) {
        val old = selectedDataTypeIndex
        selectedDataTypeIndex = index
        support.firePropertyChange(PROP_selected_data_type, old, index)
    }

    fun setShowOriginal(show: Boolean) {
        val old = showOriginal
        showOriginal = show
        support.firePropertyChange(PROP_show_original, old, show)
    }

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        support.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        support.removePropertyChangeListener(listener)
    }

    fun fetchCalendarData() {
        val device = deviceModel ?: return
        val result: MutableList<MutableMap<String, String?>> = when (selectedDataTypeIndex) {
            0 -> MADBCalenderEvents.getCalendersEventsAll(device.id, showOriginal)
            1 -> MADBCalendar.getCalendarsAll(device.id, showOriginal)
            else -> mutableListOf()
        }
        setCalendarList(result)
    }
}
