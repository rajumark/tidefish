package screens.media


import adb.DeviceModel
import screens.calendar.MADBMedia
import java.beans.PropertyChangeListener
import javax.swing.SwingWorker
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class JMediaController(val model: JMediaModel, val view: JMediaView) {
    private var isInProcessing = false

    val eventListener = PropertyChangeListener { event ->
        when (event.propertyName) {
            JMediaModel.PROP_media_list -> {
             //   println("PROP_media_list to: ${event.newValue}")
                updateMediaListUI()
            }
            JMediaModel.PROP_search_text_query_media -> {
              //  println("PROP_search_text_query_media to: ${event.newValue}")
                updateMediaListUI()
            }
            JMediaModel.PROP_show_original_media -> {
              //  println("PROP_show_original_media to: ${event.newValue}")
                fetchData()
            }
            JMediaModel.PROP_sourcetype_media -> {
              //  println("PROP_show_original_media to: ${event.newValue}")
                fetchData()
            }
            JMediaModel.PROP_contenttype_media -> {
              //  println("PROP_show_original_media to: ${event.newValue}")
                fetchData()
            }
            else -> {}
        }
    }

    fun setDeviceModel(deviceModel: DeviceModel?) {
        if(model.deviceModel!=deviceModel){
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

        view.comboBoxSourceType.addActionListener {
            val selected =  view.comboBoxSourceType.selectedItem as? MediaSourceType?
            selected?.let { it_selected ->
                model.setSourceType(it_selected)
            }
        }
        view.comboBoxContentType.addActionListener {
            val selected =  view.comboBoxContentType.selectedItem as? MediaContentType?
            selected?.let { it_selected ->
                model.setContentType(it_selected)
            }
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
        getMediaData()
    }

    private fun getMediaData() {
        if(isInProcessing) return
        isInProcessing = true
        view.refreshButton.isEnabled=false
        view.checkBoxShowOriginal.isEnabled=false
        object : SwingWorker<MutableList<MutableMap<String, String?>>, Void>() {
            override fun doInBackground(): MutableList<MutableMap<String, String?>> {
                return MADBMedia.getMediaAll(
                    model.deviceModel!!.id,
                    model.sourceType.value,
                    model.contentType.value,
                    model.showOriginal)
            }

            override fun done() {
                val result = get().orEmpty()
                model.setMediaList(result.toMutableList())
                isInProcessing = false
                view.refreshButton.isEnabled=true
                view.checkBoxShowOriginal.isEnabled=true
            }
        }.execute()
    }





    private fun updateMediaListUI() {

        val filteredList = if (model.searchTextQuery.isNullOrBlank()) {
            model.media_list
        } else {
            model.media_list.filter { it.any(::filterMediaLogic) }
        }.toMutableList()

        view.submitMediaList(filteredList)
    }

    private fun filterMediaLogic(aa: Map.Entry<String, String?>) =
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