package screens.lifecycle


import java.beans.PropertyChangeListener
import javax.swing.SwingWorker
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class JLifeCycleController(
    private val model: JLifeCycleModel,
    private val view: JLifeCycleView
) {

    private val listener = PropertyChangeListener { event ->
        when (event.propertyName) {
            JLifeCycleModel.PROP_LIFECYCLE_LIST,
            JLifeCycleModel.PROP_SEARCH_TEXT -> updateFilteredList()
        }
    }

    init {
        model.addPropertyChangeListener(listener)
        setupListeners()
    }

    fun setDeviceModel(deviceModel: adb.DeviceModel?) {
        model.setDeviceModel(deviceModel)
        fetchData()
    }

    private fun setupListeners() {
        view.onRefreshClick = { fetchData() }

        view.clearButton.addActionListener {
            model.setLastClearLine(model.lifecycleList.firstOrNull())
            updateFilteredList()
        }

        view.searchField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = model.setSearchText(view.searchField.text)
            override fun removeUpdate(e: DocumentEvent?) = model.setSearchText(view.searchField.text)
            override fun changedUpdate(e: DocumentEvent?) = model.setSearchText(view.searchField.text)
        })
    }

    private fun fetchData() {
        val device = model.deviceModel ?: return
        view.refreshButton.isEnabled=false

        object : SwingWorker<List<LogEntry>, Void>() {
            override fun doInBackground(): List<LogEntry> {
                return ADBLifecycle.getUsageState(device.id)
            }

            override fun done() {
                view.refreshButton.isEnabled=true
                model.setLifecycleList(get().orEmpty())
            }
        }.execute()
    }

    private fun updateFilteredList() {
        val query = model.searchText.lowercase()
        val originalList = model.lifecycleList.getAboveOf(model.lastClearLine)

        val filtered = if (query.isBlank()) {
            originalList
        } else {
            originalList.filter {
                it.className?.lowercase()?.contains(query) == true ||
                        it.packageName?.lowercase()?.contains(query) == true ||
                        it.type?.lowercase()?.contains(query) == true
            }
        }

        view.submitData(filtered)
    }

    private fun List<LogEntry>.getAboveOf(lastClearLine: LogEntry?): List<LogEntry> {
        return if (lastClearLine == null) this else {
            val index = indexOf(lastClearLine)
            if (index > 0) subList(0, index) else emptyList()
        }
    }
}
