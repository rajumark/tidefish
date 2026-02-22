package screens.apps.right.basic_info

import colors.LightColorsConst
import components.getIconJLabel
import decoreui.setHint
import java.awt.BorderLayout
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.border.MatteBorder
import javax.swing.table.DefaultTableModel

class AppsDetailsRightBasicInfoView : JPanel() {

    val searchField = JTextField().apply {
        columns = 15
        setHint("Search")
        border = null
    }

    var onRefreshClick: (() -> Unit)? = null

    private val searchButton = this.getIconJLabel(icon = "ic_search.svg", onClick = {})
    private val refreshButton = this.getIconJLabel(icon = "refresh.svg", onClick = {
        onRefreshClick?.invoke()
    })

    private val tableModel = object : DefaultTableModel(arrayOf("Key", "Value", "Description"), 0) {
        override fun isCellEditable(row: Int, column: Int): Boolean = false
    }
    val table = JTable(tableModel).apply {
//        selectionMode = ListSelectionModel.SINGLE_SELECTION
        tableHeader.reorderingAllowed = false
        setShowGrid(false)
        fillsViewportHeight = true
       setBorder(BorderFactory.createEmptyBorder());
    }

    init {
        layout = BorderLayout()
        border = null

        val topPanel = JPanel(BorderLayout()).apply {
            border = MatteBorder(0, 0, 1, 0, LightColorsConst.color_divider)
            add(searchButton, BorderLayout.WEST)
            add(searchField, BorderLayout.CENTER)
            add(refreshButton, BorderLayout.EAST)
        }
        add(topPanel, BorderLayout.NORTH)

        val scrollPane = JScrollPane(table).apply {
            viewport.border = null
            setBorder(BorderFactory.createEmptyBorder());
        }
        add(scrollPane, BorderLayout.CENTER)

        setSearchCountHint(0)
    }

    fun setSearchCountHint(count: Int) {
        SwingUtilities.invokeLater {
            searchField.setHint("Search in ${count} keys")
        }
    }

    fun submitRows(rows: List<Triple<String, String, String>>) {
        SwingUtilities.invokeLater {
            tableModel.setRowCount(0)
            rows.forEach { (k, v, d) ->
                tableModel.addRow(arrayOf(k, v, d))
            }
            setSearchCountHint(tableModel.rowCount)
        }
    }
}


