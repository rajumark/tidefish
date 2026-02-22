package screens.lifecycle



import adb.DeviceModel
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class JLifeCycleModel {
    companion object {
        const val PROP_LIFECYCLE_LIST = "lifecycle_list"
        const val PROP_SEARCH_TEXT = "search_text"
    }

    private val support = PropertyChangeSupport(this)

    var deviceModel: DeviceModel? = null
        private set

    var lifecycleList = listOf<LogEntry>()
        private set

    var searchText: String = ""
        private set

    var lastClearLine: LogEntry? = null
        private set

    fun setDeviceModel(deviceModelNew: DeviceModel?) {
        deviceModel = deviceModelNew
    }

    fun setLifecycleList(newList: List<LogEntry>) {
        val old = lifecycleList
        lifecycleList = newList
        support.firePropertyChange(PROP_LIFECYCLE_LIST, old, newList)
    }

    fun setSearchText(text: String) {
        val old = searchText
        searchText = text
        support.firePropertyChange(PROP_SEARCH_TEXT, old, text)
    }

    fun setLastClearLine(entry: LogEntry?) {
        lastClearLine = entry
    }

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        support.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        support.removePropertyChangeListener(listener)
    }
}
