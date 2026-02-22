package screens.deviceproperties


import adb.DeviceModel
import java.beans.PropertyChangeListener
import javax.swing.SwingWorker
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class DevicePropertiesController(
    val model: DevicePropertiesModel,
    val view: DevicePropertiesView
) {
    private var isInProcessing = false

    private val eventListener = PropertyChangeListener { event ->
        when (event.propertyName) {
            DevicePropertiesModel.PROP_device_properties_list -> {
                updateDevicePropertiesUI()
            }
            DevicePropertiesModel.PROP_search_text_query_device -> {
                updateDevicePropertiesUI()
            }
        }
    }

    init {
        setModelChangeListener()
        setupViewListener()
    }

    fun setDeviceModel(deviceModel: DeviceModel?) {
        if (model.deviceModel != deviceModel) {
            model.setDeviceModel(deviceModel)
            fetchData()
        }
    }

    private fun setupViewListener() {
        view.onRefreshClick = { fetchData() }
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
        getDeviceProperties()
    }

    private fun getDeviceProperties() {
        if (isInProcessing) return
        isInProcessing = true
        view.refreshButton.isEnabled = false
        object : SwingWorker<List<SettingItem>, Void>() {
            override fun doInBackground(): List<SettingItem> {
                return MADBDeviceProperties.fetchAllSettings(model.deviceModel!!.id)
            }

            override fun done() {
                val result = get().orEmpty()
                model.setDevicePropertiesList(result.toMutableList())
                isInProcessing = false
                view.refreshButton.isEnabled = true
            }
        }.execute()

    }
    private fun filterDevicePropertiesLogic(item: SettingItem): Boolean {
        val query = model.searchTextQuery?.lowercase() ?: return false
        return item.key.lowercase().contains(query) ||
                (item.value?.lowercase()?.contains(query) ?: false)
    }
    private fun updateDevicePropertiesUI() {
        val filteredList = if (model.searchTextQuery.isNullOrBlank()) {
            model.devicePropertiesList
        } else {
            model.devicePropertiesList.filter { model ->
                filterDevicePropertiesLogic(model)
            }
        }.toMutableList()

        view.submitDevicePropertiesList(filteredList)
    }




    private fun setModelChangeListener() {
        model.addPropertyChangeListener(eventListener)
    }
}
