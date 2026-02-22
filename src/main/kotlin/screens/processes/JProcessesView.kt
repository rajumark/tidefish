package screens.processes

import colors.LightColorsConst
import components.getIconJLabel
import decoreui.setHint
import java.awt.BorderLayout
import java.awt.Dimension
import java.util.*
import javax.swing.*
import javax.swing.border.MatteBorder
import javax.swing.table.DefaultTableModel
import utils.table.TableWidthProcesses
import utils.table.applyTableColumnsWidth

class JProcessesView : JPanel() {
    var onRefreshClick:(()->Unit)?=null
    private val searchButton by lazy { this.getIconJLabel(icon = "ic_search.svg", onClick = {}) }
    val refreshButton by lazy { this.getIconJLabel(icon = "refresh.svg", onClick = { onRefreshClick?.invoke() }) }

    val searchField by lazy {
        JTextField(15).apply {
            preferredSize = Dimension(200, 28)
            border = null
        }
    }

    private val defaultTableModel = DefaultTableModel(0, 0)

    private val jtable by lazy {
        JTable().apply {
            autoResizeMode = JTable.AUTO_RESIZE_OFF
            model = defaultTableModel
            tableHeader.reorderingAllowed = false
            setShowGrid(false)
            fillsViewportHeight = true
            setBorder(BorderFactory.createEmptyBorder())
            applyTableColumnsWidth(TableWidthProcesses.widths)
        }
    }

    private val scrollPane by lazy {
        JScrollPane(jtable).apply {
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            viewport.border = null
            setBorder(BorderFactory.createEmptyBorder())
        }
    }

    fun removeAllRows() {
        defaultTableModel.rowCount = 0
    }

    fun submitProcessList(list: MutableList<MutableMap<String, String?>>) {
        removeAllRows()
        addInJTableDataModel(list)
    }

    private fun addInJTableDataModel(list: MutableList<MutableMap<String, String?>>) {
        val mainList = list
        val keys: List<String> = mainList
            .flatMap { it.keys }
            .filter { !it.startsWith("Row: ") }
            .distinct()
            .sortedBy { key ->
                val index = MProcessSortingString.listProcessesColumSorting.indexOfFirst { it == key }
                if (index == -1) 9999 else index
            }

        val header = arrayOf("No.") + keys.toTypedArray()
        defaultTableModel.setColumnIdentifiers(header)

        list.forEachIndexed { index, row ->
            val rowData = Vector<Any>()
            rowData.add((index + 1).toString())
            keys.forEach { key ->
                rowData.add(row.getOrDefault(key, "") ?: "")
            }
            defaultTableModel.addRow(rowData)
        }

        jtable.invalidate()
        jtable.repaint()
        setSearchCountHint(defaultTableModel.rowCount)
        jtable.applyTableColumnsWidth(TableWidthProcesses.widths)
    }


    val topPanel = JPanel(BorderLayout()).apply {
        border = MatteBorder(0, 0, 1, 0, LightColorsConst.color_divider)
        add(refreshButton, BorderLayout.WEST)
        val searchPanel = JPanel(BorderLayout()).apply {
            add(searchButton, BorderLayout.WEST)
            add(searchField, BorderLayout.CENTER)
        }
        add(searchPanel, BorderLayout.CENTER)
    }

    init {
        layout = BorderLayout()
        add(topPanel, BorderLayout.NORTH)
        add(scrollPane, BorderLayout.CENTER)
    }

    private fun setSearchCountHint(count: Int) {
        SwingUtilities.invokeLater {
            searchField.setHint("Search in ${count} items")
        }
    }
}