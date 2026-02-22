package adb_terminal

import adb.DeviceModel
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class ADBTerminalModel {
    companion object {
        const val PROP_HISTORY = "history"
        const val PROP_OUTPUT = "output"
        const val PROP_FILTER = "filter"
        const val PROP_SEARCH_QUERY = "search_query"
        const val PROP_IS_RUNNING = "is_running"
        const val PROP_AUTOSCROLL = "autoscroll"
    }

    private val support = PropertyChangeSupport(this)

    var deviceModel: DeviceModel? = null
        private set

    fun setDeviceModel(deviceModelNew: DeviceModel?) {
        deviceModel = deviceModelNew
    }

    var commandHistory: MutableList<String> = mutableListOf()
        private set

    fun setHistory(newHistory: List<String>) {
        val old = commandHistory.toList()
        commandHistory = newHistory.toMutableList()
        support.firePropertyChange(PROP_HISTORY, old, commandHistory)
    }

    var outputText: String = ""
        private set

    fun appendOutput(text: String) {
        val old = outputText
        outputText += text
        support.firePropertyChange(PROP_OUTPUT, old, outputText)
    }

    fun clearOutput() {
        val old = outputText
        outputText = ""
        support.firePropertyChange(PROP_OUTPUT, old, outputText)
    }

    var filterText: String? = null
        private set

    fun setFilter(text: String?) {
        val old = filterText
        filterText = text
        support.firePropertyChange(PROP_FILTER, old, text)
    }

    var searchQuery: String? = null
        private set

    fun setSearchQuery(text: String?) {
        val old = searchQuery
        searchQuery = text
        support.firePropertyChange(PROP_SEARCH_QUERY, old, text)
    }

    var isRunning: Boolean = false
        private set

    fun setRunning(running: Boolean) {
        val old = isRunning
        isRunning = running
        support.firePropertyChange(PROP_IS_RUNNING, old, running)
    }

    var autoScrollEnabled: Boolean = true
        private set

    fun setAutoScroll(enabled: Boolean) {
        val old = autoScrollEnabled
        autoScrollEnabled = enabled
        support.firePropertyChange(PROP_AUTOSCROLL, old, enabled)
    }

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        support.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        support.removePropertyChangeListener(listener)
    }
}


