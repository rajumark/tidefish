package screens.messages


import adb.DeviceModel
import java.beans.PropertyChangeListener
import javax.swing.SwingWorker
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class JMessagesController(val model: JMessagesModel, val view: JMessagesView) {
    private var isInProcessing = false

    val eventListener = PropertyChangeListener { event ->
        when (event.propertyName) {
            JMessagesModel.PROP_messages_list -> {
             //   println("PROP_messages_list to: ${event.newValue}")
                updateMessagesListUI()
            }
            JMessagesModel.PROP_search_text_query_messages -> {
              //  println("PROP_search_text_query_messages to: ${event.newValue}")
                updateMessagesListUI()
            }
            JMessagesModel.PROP_show_original_messages -> {
            //    println("PROP_show_original_messages to: ${event.newValue}")
                fetchData()
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
        view.onRefreshClick = { fetchData() }

        view.checkBoxShowOriginal.addActionListener {
            model.setShowOriginal(view.checkBoxShowOriginal.isSelected)
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
        getMessageData()
    }

    private fun getMessageData() {
        if(isInProcessing) return
        isInProcessing = true
        view.refreshButton.isEnabled=false
        view.checkBoxShowOriginal.isEnabled=false
        object : SwingWorker<MutableList<MutableMap<String, String?>>, Void>() {
            override fun doInBackground(): MutableList<MutableMap<String, String?>> {
                return MADBMessages.getMessagesAll(model.deviceModel!!.id,model.showOriginal)
            }

            override fun done() {
                val result = get().orEmpty()
                model.setMessagesList(result.toMutableList())
                isInProcessing = false
                view.refreshButton.isEnabled=true
                view.checkBoxShowOriginal.isEnabled=true
            }
        }.execute()
    }





    private fun updateMessagesListUI() {

        val filteredList = if (model.searchTextQuery.isNullOrBlank()) {
            model.messages_list
        } else {
            model.messages_list.filter { it.any(::filterMessagesLogic) }
        }.toMutableList()

        view.submitMessageList(filteredList)
    }

    private fun filterMessagesLogic(aa: Map.Entry<String, String?>) =
        if (  aa.key == "type" ||
            aa.key == "date" ||
            aa.key == "body" ||
            aa.key == "date_sent" ||
            aa.key == "address") {
            aa.value?.lowercase()?.contains((model.searchTextQuery ?: "").lowercase()) ?: false
        } else {
            false
        }

    private fun setModelChangeListener() {
        model.addPropertyChangeListener(eventListener)
    }
}