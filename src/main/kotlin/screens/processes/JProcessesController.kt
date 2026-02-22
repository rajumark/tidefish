package screens.processes

import adb.DeviceModel
import java.beans.PropertyChangeListener
import javax.swing.SwingWorker
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class JProcessesController(val model: JProcessesModel, val view: JProcessesView) {
    private var isInProcessing = false

    private val eventListener = PropertyChangeListener { event ->
        when (event.propertyName) {
            JProcessesModel.PROP_process_list, JProcessesModel.PROP_search_text_query -> updateProcessListUI()
        }
    }

    init {
        model.addPropertyChangeListener(eventListener)
        setupViewListeners()
    }

    fun setDeviceModel(deviceModel: DeviceModel?) {
        if (model.deviceModel != deviceModel) {
            model.setDeviceModel(deviceModel)
            fetchData()
        }
    }

    private fun setupViewListeners() {
        view.onRefreshClick = { fetchData() }

        view.searchField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = updateQuery()
            override fun removeUpdate(e: DocumentEvent?) = updateQuery()
            override fun changedUpdate(e: DocumentEvent?) = updateQuery()

            private fun updateQuery() {
                model.setSearchTextQuery(view.searchField.text)
            }
        })
    }

    private fun fetchData() {
        if (model.deviceModel == null || isInProcessing) return

        isInProcessing = true
        view.refreshButton.isEnabled = false

        object : SwingWorker<MutableList<MutableMap<String, String?>>, Void>() {
            override fun doInBackground(): MutableList<MutableMap<String, String?>> {
                return SADBProcesses.getServiceListPair(model.deviceModel!!.id)
            }

            override fun done() {
                val result = get().orEmpty()
                model.setProcessList(result.toMutableList())
                isInProcessing = false
                view.refreshButton.isEnabled = true
            }
        }.execute()
    }

    private fun updateProcessListUI() {
        val filtered = if (model.searchTextQuery.isNullOrBlank()) {
            model.process_list
        } else {
            model.process_list.filter {
                it.any { entry ->
                    (entry.key == "pid" || entry.key == "name") &&
                            (entry.value?.contains(model.searchTextQuery!!, ignoreCase = true) == true)
                }
            }
        }.toMutableList()

        view.submitProcessList(filtered)
    }
}
