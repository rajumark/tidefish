package screens.notifications

import adb.DeviceModel
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class JNotificationsModel {
    companion object {
        const val PROP_notifications_list = "notifications_list"
        const val PROP_search_text_query = "search_text_query"
        const val PROP_selected_notification = "selected_notification"
    }

    private val support = PropertyChangeSupport(this)

    var deviceModel: DeviceModel? = null
        private set

    var notificationsList: MutableList<NotificationMaster> = mutableListOf()
        private set

    var searchTextQuery: String? = null
        private set

    var selectedNotification: NotificationMaster? = null
        private set

    fun setDeviceModel(newDeviceModel: DeviceModel?) {
        if (deviceModel != newDeviceModel) {
            deviceModel = newDeviceModel
        }
    }

    fun setNotificationsList(list: MutableList<NotificationMaster>) {
        val oldList = notificationsList
        notificationsList = list
        support.firePropertyChange(PROP_notifications_list, oldList, list)
    }

    fun setSearchTextQuery(query: String?) {
        val oldQuery = searchTextQuery
        searchTextQuery = query
        support.firePropertyChange(PROP_search_text_query, oldQuery, query)
    }

    fun setSelectedNotification(notification: NotificationMaster?) {
        val oldNotification = selectedNotification
        selectedNotification = notification
        support.firePropertyChange(PROP_selected_notification, oldNotification, notification)
    }

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        support.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        support.removePropertyChangeListener(listener)
    }
}
