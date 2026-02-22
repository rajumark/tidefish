package screens.contacts


import colors.LightColorsConst
import components.getIconJLabel
import decoreui.applyStyleSplitPan
import decoreui.setHint
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.util.*
import javax.swing.*
import javax.swing.border.MatteBorder
import javax.swing.table.DefaultTableModel

class JContactsView : JPanel() {
    var onRefreshClick:(()->Unit)?=null

    private val searchButton by lazy { this.getIconJLabel(icon = "ic_search.svg", onClick = {}) }
    val refreshButton by lazy { this.getIconJLabel(icon = "refresh.svg", onClick = { onRefreshClick?.invoke() }) }

    val searchField by lazy {
        JTextField(15).apply {
            preferredSize = Dimension(200, 28)
            border = null
        }
    }

    val leftListModel = DefaultListModel<ContactMaster>()

    private val contactList by lazy {
        JList(leftListModel).apply {
            border = null
            //fixedCellHeight = 30
            cellRenderer = ContactListRenderer()
            selectionBackground = LightColorsConst.color_background_sidemenu_item
            selectionForeground = LightColorsConst.color_textview_on_background
        }
    }

    private val scrollPaneLeft by lazy {
        JScrollPane(contactList).apply {
            viewport.border = null
            setBorder(BorderFactory.createEmptyBorder())
        }
    }

    private val tableModelRight = DefaultTableModel(0, 0)

    private val detailTable by lazy {
        JTable(tableModelRight).apply {
            autoResizeMode = JTable.AUTO_RESIZE_OFF
            tableHeader.reorderingAllowed = false
            setShowGrid(false)
            fillsViewportHeight = true
            setBorder(BorderFactory.createEmptyBorder())
        }
    }

    private val scrollPaneRight by lazy {
        JScrollPane(detailTable).apply {
            viewport.border = null
            setBorder(BorderFactory.createEmptyBorder())
        }
    }

    val leftPanel by lazy {
        JPanel(BorderLayout()).apply {
            preferredSize = Dimension(300, 0)
            add(scrollPaneLeft, BorderLayout.CENTER)
        }
    }

    val rightPanel by lazy {
        JPanel(BorderLayout()).apply {
            add(scrollPaneRight, BorderLayout.CENTER)
        }
    }

    private val splitPane by lazy {
        JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel).apply {
            dividerSize = 5
            resizeWeight = 0.1
            isContinuousLayout = true
            applyStyleSplitPan()
        }
    }

    val topPanel by lazy {
        JPanel(BorderLayout()).apply {
            border = MatteBorder(0, 0, 1, 0, LightColorsConst.color_divider)
            add(refreshButton, BorderLayout.WEST)
            val searchPanel = JPanel(BorderLayout()).apply {
                add(searchButton, BorderLayout.WEST)
                add(searchField, BorderLayout.CENTER)
            }
            add(searchPanel, BorderLayout.CENTER)
        }
    }

    init {
        layout = BorderLayout()
        add(topPanel, BorderLayout.NORTH)
        add(splitPane, BorderLayout.CENTER)
    }

    fun submitContactList(list: List<ContactMaster>) {
        leftListModel.clear()
        list.forEach { leftListModel.addElement(it) }
        searchField.setHint("Search in ${leftListModel.size()} contacts")
    }

    fun showContactDetails(contact: ContactMaster) {
        val keys = contact.rawdata
            .flatMap { it.keys }
            .filter { !it.startsWith("Row: ") }
            .distinct()
            .sortedBy { it } // Use a sort list if needed

        val headers = keys.toTypedArray()
        val tableModel = DefaultTableModel(0, 0)
        tableModel.setColumnIdentifiers(headers)

        contact.rawdata.forEach { entry ->
            val row = Vector<Any>()
            keys.forEach { key ->
                row.add(entry.getOrDefault(key, "") ?: "")
            }
            tableModel.addRow(row)
        }

        detailTable.model = tableModel
        detailTable.invalidate()
        detailTable.repaint()
    }

    inner class ContactListRenderer : DefaultListCellRenderer() {
        override fun getListCellRendererComponent(
            list: JList<*>?, value: Any?, index: Int,
            isSelected: Boolean, cellHasFocus: Boolean
        ): Component {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
            text = if (value is ContactMaster) {
                value.displayName.ifBlank { value.number.firstOrNull() ?: "Unknown" }
            } else {
                "Unknown"
            }
            return this
        }
    }

    fun addListSelectionListener(listener: (ContactMaster) -> Unit) {
        contactList.addListSelectionListener {
            if (!it.valueIsAdjusting) {
                val selected = contactList.selectedValue
                if (selected != null) listener(selected)
            }
        }
    }
}
