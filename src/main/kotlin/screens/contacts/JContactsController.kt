package screens.contacts

import adb.DeviceModel
import java.beans.PropertyChangeListener
import java.util.*
import javax.swing.SwingUtilities
import javax.swing.SwingWorker
import javax.swing.Timer
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class JContactsController(
    private val model: JContactsModel,
    private val view: JContactsView
) {
    @Volatile private var currentFilterWorker: SwingWorker<List<ContactMaster>, Void>? = null
    private var searchDebounceTimer: Timer? = null
    private val debounceDelay = 200 // ms

    private val eventListener = PropertyChangeListener { event ->
        when (event.propertyName) {
            JContactsModel.PROP_contacts_list -> updateContactListUI()
            JContactsModel.PROP_search_text_query -> scheduleDebouncedFilter()
            JContactsModel.PROP_selected_contact -> model.selectedContact?.let {
                view.showContactDetails(it)
            }
        }
    }

    init {
        setModelChangeListener()
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
            override fun insertUpdate(e: DocumentEvent?) = model.setSearchTextQuery(view.searchField.text)
            override fun removeUpdate(e: DocumentEvent?) = model.setSearchTextQuery(view.searchField.text)
            override fun changedUpdate(e: DocumentEvent?) = model.setSearchTextQuery(view.searchField.text)
        })

        view.addListSelectionListener { contact -> model.setSelectedContact(contact) }
    }

    private fun fetchData() {
        val device = model.deviceModel ?: return
        view.refreshButton.isEnabled = false

        object : SwingWorker<List<ContactMaster>, Void>() {
            override fun doInBackground(): List<ContactMaster> {
                return MADBContacts.getContactsAll(device.id)
            }

            override fun done() {
                val contacts = try { get() } catch (e: Exception) { emptyList() }
                model.setContactsList(contacts.toMutableList())
                view.refreshButton.isEnabled = true
            }
        }.execute()
    }

    private fun updateContactListUI() {
        currentFilterWorker?.cancel(true)

        val query = model.searchTextQuery.orEmpty().lowercase(Locale.getDefault())
        val sourceList = model.contactsList.toList() // snapshot

        if (query.isBlank()) {
            SwingUtilities.invokeLater { view.submitContactList(sourceList) }
            return
        }

        val worker = object : SwingWorker<List<ContactMaster>, Void>() {
            override fun doInBackground(): List<ContactMaster> {
                return sourceList.asSequence()
                    .filter { contact ->
                        if (isCancelled) return@filter false
                        contact.displayName.lowercase(Locale.getDefault()).contains(query) ||
                                contact.contact_id.lowercase(Locale.getDefault()).contains(query) ||
                                contact.number.any { it?.lowercase(Locale.getDefault())?.contains(query) == true }
                    }
                    .toList()
            }

            override fun done() {
                if (isCancelled) return
                val result = try { get() } catch (e: Exception) { emptyList() }
                SwingUtilities.invokeLater { view.submitContactList(result) }
            }
        }

        currentFilterWorker = worker
        worker.execute()
    }

    private fun scheduleDebouncedFilter() {
        if (searchDebounceTimer == null) {
            searchDebounceTimer = Timer(debounceDelay, null).apply {
                isRepeats = false
                addActionListener { updateContactListUI() }
            }
        }
        searchDebounceTimer!!.restart()
    }

    private fun setModelChangeListener() {
        model.addPropertyChangeListener(eventListener)
    }
}
