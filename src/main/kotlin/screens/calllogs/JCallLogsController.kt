package screens.calllogs

import adb.DeviceModel
import java.beans.PropertyChangeListener
import javax.swing.SwingWorker
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class JCallLogsController(val model: JCallLogsModel, val view: JCallLogsView) {
    private var isInProcessing = false

    val eventListener = PropertyChangeListener { event ->
        when (event.propertyName) {
            JCallLogsModel.PROP_calls_list -> {
             //   println("PROP_apps_list to: ${event.newValue}")
                updateCallListUI()
            }
            JCallLogsModel.PROP_search_text_query_call -> {
               // println("PROP_apps_list to: ${event.newValue}")
                updateCallListUI()
            }
            else -> {}
        }
    }

    fun setDeviceModel(deviceModel: DeviceModel?) {
        if(model.deviceModel!=deviceModel) {
            model.setDeviceModel(deviceModel)
            fetchData()
        }
    }

    init {
        setModelChangeListener()
        setupViewListener()
    }
    fun setupViewListener(){
        view.onRefreshClick={
            fetchData()
        }

        view.searchField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                model.setSearchTextQuery(view.searchField.text)
            }

            override fun removeUpdate(e: DocumentEvent?) {
                model.setSearchTextQuery(view.searchField.text)
            }

            override fun changedUpdate(e: DocumentEvent?) {
                model.setSearchTextQuery(view.searchField.text)
            }
        })
    }



    private fun fetchData() {
        if (model.deviceModel == null) return
        if (isInProcessing) return
        getCallData()
    }

    private fun getCallData() {
        if(isInProcessing) return
        isInProcessing = true
        view.refreshButton.isEnabled=false

        object : SwingWorker<MutableList<MutableMap<String, String?>>, Void>() {
            override fun doInBackground(): MutableList<MutableMap<String, String?>> {
                return MADBCalls.getCallssAll(model.deviceModel!!.id)
            }

            override fun done() {
                val result = get().orEmpty()
                model.setCallList(result.toMutableList())
                isInProcessing = false
                view.refreshButton.isEnabled=true

            }
        }.execute()
    }





    private fun updateCallListUI() {

        val filteredList = if (model.searchTextQuery.isNullOrBlank()) {
            model.calls_list
        } else {
            model.calls_list.filter { it.any(::filterCallsLogic) }
        }.toMutableList()

        view.submitCallList(filteredList)
    }

    private fun filterCallsLogic(aa: Map.Entry<String, String?>) =
        if (aa.key == "name" || aa.key == "number") {
            aa.value?.lowercase()?.contains((model.searchTextQuery ?: "").lowercase()) ?: false
        } else {
            false
        }

    private fun setModelChangeListener() {

        model.addPropertyChangeListener(eventListener)
    }
}