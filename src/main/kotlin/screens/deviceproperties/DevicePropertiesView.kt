package screens.deviceproperties


import colors.LightColorsConst
import components.getIconJLabel
import java.awt.BorderLayout
import java.awt.Dimension
import java.util.*
import javax.swing.*
import javax.swing.border.MatteBorder
import javax.swing.table.DefaultTableModel

class DevicePropertiesView : JPanel() {
    var onRefreshClick:(()->Unit)?=null
    private val searchButton by lazy { this.getIconJLabel(icon = "ic_search.svg", onClick = {}) }
    val refreshButton by lazy { this.getIconJLabel(icon = "refresh.svg", onClick = { onRefreshClick?.invoke() }) }

    val searchField by lazy {
        JTextField(15).apply {
            preferredSize = Dimension(200, 28)
            border = null
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

    val topPanelExtra by lazy {
        JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(topPanel)
        }
    }

    val defaultTableModelDeviceProperties = DefaultTableModel(0, 0)

    private val jTable by lazy {
        JTable().apply {
            autoResizeMode = JTable.AUTO_RESIZE_OFF
            model = defaultTableModelDeviceProperties
            tableHeader.reorderingAllowed = false
            setShowGrid(false)
            fillsViewportHeight = true
            setBorder(BorderFactory.createEmptyBorder())
        }
    }

    private val scrollPane by lazy {
        JScrollPane(jTable).apply {
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            viewport.border = null
            setBorder(BorderFactory.createEmptyBorder())
        }
    }

    init {
        layout = BorderLayout()
        add(topPanelExtra, BorderLayout.NORTH)
        add(scrollPane, BorderLayout.CENTER)
    }

    fun removeAllRows() {
        defaultTableModelDeviceProperties.rowCount = 0
    }

    fun submitDevicePropertiesList(propertiesList: MutableList<SettingItem>) {
        removeAllRows()
        addInJTableDataModel(propertiesList)
        setColumnWidths()
    }

    private fun addInJTableDataModel(propertiesList: MutableList<SettingItem>) {
        val keys = listOf("Type", "Key", "Value", "Description")

        val header = arrayOf("No.") + keys.toTypedArray()
        defaultTableModelDeviceProperties.setColumnIdentifiers(header)

        propertiesList.forEachIndexed { index, property ->
            val row = Vector<Any>()
            row.add((index + 1).toString()) // No. column

            row.add(property.type.name)    // Type
            row.add(property.key)           // Key
            row.add(property.value ?: "")   // Value
            row.add(property.description ?: "") // Description

            defaultTableModelDeviceProperties.addRow(row)
        }

        rePaintTable()
        searchField.putClientProperty(
            "JTextField.placeholderText",
            "Search in ${defaultTableModelDeviceProperties.rowCount} items"
        )
    }


    private fun rePaintTable() {
        jTable.invalidate()
        jTable.repaint()
    }

    fun setColumnWidths() {
        jTable.model?.let {
            val columnCount = jTable.columnModel.columnCount
            for (i in PropertiesColumnWidthConst.indices) {
                if (i < columnCount) {
                    jTable.columnModel.getColumn(i).minWidth  = PropertiesColumnWidthConst[i]
                }
            }
        }
    }
}
