package screens.notifications

import adb.DeviceModel
import java.beans.PropertyChangeListener
import java.util.*
import javax.swing.SwingUtilities
import javax.swing.SwingWorker
import javax.swing.Timer
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class JNotificationsController(
    private val model: JNotificationsModel,
    private val view: JNotificationsView
) {
    @Volatile private var currentFilterWorker: SwingWorker<List<NotificationMaster>, Void>? = null
    private var searchDebounceTimer: Timer? = null
    private val debounceDelay = 200 // ms

    private val eventListener = PropertyChangeListener { event ->
        when (event.propertyName) {
            JNotificationsModel.PROP_notifications_list -> updateNotificationListUI()
            JNotificationsModel.PROP_search_text_query -> scheduleDebouncedFilter()
            JNotificationsModel.PROP_selected_notification -> model.selectedNotification?.let {
                view.showNotificationDetails(it)
            }
        }
    }

    init {
        setModelChangeListener()
        setupViewListeners()
    }

    fun setDeviceModel(deviceModel: DeviceModel?) {
        println("DEBUG: setDeviceModel called with: $deviceModel")
        if (model.deviceModel != deviceModel) {
            model.setDeviceModel(deviceModel)
            println("DEBUG: Device model changed, fetching data")
            fetchData()
        } else {
            println("DEBUG: Device model unchanged, skipping fetch")
        }
    }

    private fun setupViewListeners() {
        view.onRefreshClick = { fetchData() }

        view.searchField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = model.setSearchTextQuery(view.searchField.text)
            override fun removeUpdate(e: DocumentEvent?) = model.setSearchTextQuery(view.searchField.text)
            override fun changedUpdate(e: DocumentEvent?) = model.setSearchTextQuery(view.searchField.text)
        })

        view.addListSelectionListener { notification -> model.setSelectedNotification(notification) }
    }

    private fun fetchData() {
        val device = model.deviceModel
        println("DEBUG: fetchData called, device: $device")
        if (device == null) {
            println("DEBUG: No device available, skipping fetch")
            return
        }
        view.refreshButton.isEnabled = false
        println("DEBUG: Starting notification fetch for device: ${device.id}")

        object : SwingWorker<List<NotificationMaster>, Void>() {
            override fun doInBackground(): List<NotificationMaster> {
                println("DEBUG: doInBackground - calling getNotificationsAll")
                return MADBNotifications.getNotificationsAll(device.id)
            }

            override fun done() {
                val notifications = try { 
                    get() 
                } catch (e: Exception) { 
                    println("DEBUG: Exception in doInBackground: ${e.message}")
                    e.printStackTrace()
                    emptyList() 
                }
                println("DEBUG: Received ${notifications.size} notifications from parser")
                model.setNotificationsList(notifications.toMutableList())
                view.refreshButton.isEnabled = true
                println("DEBUG: Fetch completed, button re-enabled")
            }
        }.execute()
    }

    private fun updateNotificationListUI() {
        currentFilterWorker?.cancel(true)

        val query = model.searchTextQuery.orEmpty().lowercase(Locale.getDefault())
        val sourceList = model.notificationsList.toList() // snapshot
        println("DEBUG: updateNotificationListUI called with query: '$query', sourceList size: ${sourceList.size}")

        if (query.isBlank()) {
            println("DEBUG: No query, showing all ${sourceList.size} notifications")
            SwingUtilities.invokeLater { view.submitNotificationList(sourceList) }
            return
        }

        val worker = object : SwingWorker<List<NotificationMaster>, Void>() {
            override fun doInBackground(): List<NotificationMaster> {
                return sourceList.asSequence()
                    .filter { notification ->
                        if (isCancelled) return@filter false
                        val displayName = MADBNotifications.getPackageDisplayName(notification.packageName)
                        val matches = displayName.lowercase(Locale.getDefault()).contains(query) ||
                                notification.title.lowercase(Locale.getDefault()).contains(query) ||
                                notification.text.lowercase(Locale.getDefault()).contains(query) ||
                                notification.packageName.lowercase(Locale.getDefault()).contains(query) ||
                                notification.notification_key.lowercase(Locale.getDefault()).contains(query)
                        if (matches) {
                            println("DEBUG: Filter match: ${notification.packageName} - ${notification.title}")
                        }
                        matches
                    }
                    .toList()
            }

            override fun done() {
                if (isCancelled) return
                val result = try { get() } catch (e: Exception) { 
                    println("DEBUG: Exception in filter: ${e.message}")
                    emptyList() 
                }
                println("DEBUG: Filtered result size: ${result.size}")
                SwingUtilities.invokeLater { view.submitNotificationList(result) }
            }
        }

        currentFilterWorker = worker
        worker.execute()
    }

    private fun scheduleDebouncedFilter() {
        if (searchDebounceTimer == null) {
            searchDebounceTimer = Timer(debounceDelay, null).apply {
                isRepeats = false
                addActionListener { updateNotificationListUI() }
            }
        }
        searchDebounceTimer!!.restart()
    }

    private fun setModelChangeListener() {
        model.addPropertyChangeListener(eventListener)
    }
}
