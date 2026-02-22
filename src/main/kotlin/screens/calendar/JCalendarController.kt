package screens.calendar


import adb.DeviceModel
import java.beans.PropertyChangeListener
import javax.swing.SwingWorker

class JCalendarController(
    private val model: JCalendarModel,
    private val view: JCalendarView
) {
    private var isLoading = false

    private val propertyChangeListener = PropertyChangeListener { event ->
        when (event.propertyName) {
            JCalendarModel.PROP_calendar_list,
            JCalendarModel.PROP_search_text_query -> updateCalendarListUI()
        }
    }

    init {
        setupModelListener()
        setupViewListener()
    }

    fun setDeviceModel(deviceModel: DeviceModel?) {
        if (model.deviceModel != deviceModel) {
            model.setDeviceModel(deviceModel)
            fetchCalendarData()
        }
    }

    private fun setupModelListener() {
        model.addPropertyChangeListener(propertyChangeListener)
    }

    private fun setupViewListener() {
        view.onRefreshClick = { fetchCalendarData() }

        view.addSearchListener { query ->
            model.setSearchTextQuery(query)
        }

        view.addDataTypeChangeListener { index ->
            model.setSelectedDataTypeIndex(index)
            fetchCalendarData()
        }

        view.addFormatToggleListener { showOriginal ->
            model.setShowOriginal(showOriginal)
            fetchCalendarData()
        }
    }

    private fun fetchCalendarData() {
        if (model.deviceModel == null || isLoading) return

        isLoading = true
        view.refreshButton.isEnabled = false

        object : SwingWorker<MutableList<MutableMap<String, String?>>, Void>() {
            override fun doInBackground(): MutableList<MutableMap<String, String?>> {
                model.fetchCalendarData()
                return model.calendarList
            }

            override fun done() {
                updateCalendarListUI()
                isLoading = false
                view.refreshButton.isEnabled = true
            }
        }.execute()
    }

    private fun updateCalendarListUI() {
        val filteredList = if (model.searchTextQuery.isNullOrBlank()) {
            model.calendarList
        } else {
            model.calendarList.filter { item ->
                item.any { (_, value) ->
                    value?.contains(model.searchTextQuery!!, ignoreCase = true) == true
                }
            }
        }

        view.updateTable(filteredList)
    }
}
