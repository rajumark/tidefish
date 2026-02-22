package screens.contacts


import adb.DeviceModel
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class JContactsModel {
    companion object {
        const val PROP_contacts_list = "contacts_list"
        const val PROP_search_text_query = "search_text_query"
        const val PROP_selected_contact = "selected_contact"
    }

    private val support = PropertyChangeSupport(this)

    var deviceModel: DeviceModel? = null
        private set

    var contactsList: MutableList<ContactMaster> = mutableListOf()
        private set

    var searchTextQuery: String? = null
        private set

    var selectedContact: ContactMaster? = null
        private set

    fun setDeviceModel(newDeviceModel: DeviceModel?) {
        if (deviceModel != newDeviceModel) {
            deviceModel = newDeviceModel
        }
    }

    fun setContactsList(list: MutableList<ContactMaster>) {
        val oldList = contactsList
        contactsList = list
        support.firePropertyChange(PROP_contacts_list, oldList, list)
    }

    fun setSearchTextQuery(query: String?) {
        val oldQuery = searchTextQuery
        searchTextQuery = query
        support.firePropertyChange(PROP_search_text_query, oldQuery, query)
    }

    fun setSelectedContact(contact: ContactMaster?) {
        val oldContact = selectedContact
        selectedContact = contact
        support.firePropertyChange(PROP_selected_contact, oldContact, contact)
    }

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        support.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        support.removePropertyChangeListener(listener)
    }
}
