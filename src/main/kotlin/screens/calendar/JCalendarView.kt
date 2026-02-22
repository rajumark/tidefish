package screens.calendar

import colors.LightColorsConst
import components.getIconJLabel
import utils.table.TableWidthCalendar
import utils.table.applyTableColumnsWidth
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.*
import javax.swing.border.MatteBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.table.DefaultTableModel

class JCalendarView : JPanel(BorderLayout()) {
    var onRefreshClick:(()->Unit)?=null

    val searchField = JTextField().apply {
        preferredSize = Dimension(200, 28)
        border = null
    }
    private val searchButton by lazy { this.getIconJLabel(icon = "ic_search.svg", onClick = {}) }
    val refreshButton by lazy { this.getIconJLabel(icon = "refresh.svg", onClick = { onRefreshClick?.invoke() }) }
    val dataTypeComboBox = JComboBox(arrayOf("Events", "Type"))
    val formatToggle = JToggleButton("Show Original")

    private val tableModel = DefaultTableModel()
    private val table = JTable(tableModel).apply {
        autoResizeMode = JTable.AUTO_RESIZE_OFF
        fillsViewportHeight = true
        tableHeader.reorderingAllowed = false
        setShowGrid(false)
        setBorder(BorderFactory.createEmptyBorder())
        applyTableColumnsWidth(TableWidthCalendar.widths)
    }

    init {
        // Top Panel with controls
        val topPanel = JPanel(BorderLayout()).apply {
            border = MatteBorder(0, 0, 1, 0, LightColorsConst.color_divider)
            add(refreshButton, BorderLayout.WEST)
            val searchPanel = JPanel(BorderLayout()).apply {
                add(searchButton, BorderLayout.WEST)
                add(searchField, BorderLayout.CENTER)
            }
            add(searchPanel, BorderLayout.CENTER)
            val rightControls = JPanel(FlowLayout(FlowLayout.RIGHT)).apply {
                add(dataTypeComboBox)
                add(formatToggle)
            }
            add(rightControls, BorderLayout.EAST)
        }

        // Add a scroll pane for both horizontal and vertical scrolling
        val scrollPane = JScrollPane(table).apply {
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            viewport.border = null
            setBorder(BorderFactory.createEmptyBorder())
        }

        // Add components to main panel
        add(topPanel, BorderLayout.NORTH)
        add(scrollPane, BorderLayout.CENTER)
    }

    /**
     * Updates the table with the provided list of calendar data.
     * @param dataList A list of maps representing calendar entries.
     */
    fun updateTable(dataList: List<Map<String, String?>>) {
        // Clear existing data
        tableModel.setRowCount(0)
        tableModel.setColumnCount(0)

        if (dataList.isEmpty()) return

        // Flatten keys and sort them based on the custom sorting list
        val keys = dataList
            .flatMap { it.keys }
            .distinct()
            .sortedBy { key ->
                val index = MCalenderEventSortingString.listCalenderEventsColumSorting.indexOf(key)
                if (index == -1) 9999 else index
            }

        // Set sorted columns
        tableModel.setColumnIdentifiers(keys.toTypedArray())

        // Add rows in the same sorted order
        for (data in dataList) {
            val row = keys.map { key -> data[key] ?: "" }.toTypedArray()
            tableModel.addRow(row)
        }

        table.applyTableColumnsWidth(TableWidthCalendar.widths)
        searchField.putClientProperty("JTextField.placeholderText", "Search in ${tableModel.rowCount} items")

    }

    /**
     * Adds a listener to the search field to handle text changes.
     * @param onSearchTextChanged Callback invoked with the new text.
     */
    fun addSearchListener(onSearchTextChanged: (String) -> Unit) {
        searchField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = onSearchTextChanged(searchField.text)
            override fun removeUpdate(e: DocumentEvent?) = onSearchTextChanged(searchField.text)
            override fun changedUpdate(e: DocumentEvent?) = onSearchTextChanged(searchField.text)
        })
    }



    /**
     * Adds a listener to the data type combo box.
     * @param onDataTypeChanged Callback invoked with the selected index.
     */
    fun addDataTypeChangeListener(onDataTypeChanged: (Int) -> Unit) {
        dataTypeComboBox.addActionListener {
            onDataTypeChanged(dataTypeComboBox.selectedIndex)
        }
    }

    /**
     * Adds a listener to the format toggle button.
     * @param onFormatToggle Callback invoked with the toggle state.
     */
    fun addFormatToggleListener(onFormatToggle: (Boolean) -> Unit) {
        formatToggle.addActionListener {
            onFormatToggle(formatToggle.isSelected)
        }
    }
}
