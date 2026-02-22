package screens.lifecycle


import colors.LightColorsConst
import components.getIconJLabel
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.util.*
import javax.swing.*
import javax.swing.border.MatteBorder
import javax.swing.table.DefaultTableModel
import utils.table.TableWidthLifecycle
import utils.table.applyTableColumnsWidth

class JLifeCycleView : JPanel() {
    var onRefreshClick:(()->Unit)?=null

    val searchField = JTextField().apply {
        preferredSize = Dimension(180, 28)
        border = null
    }

    private val searchButton by lazy { this.getIconJLabel(icon = "ic_search.svg", onClick = {}) }
    val refreshButton by lazy { this.getIconJLabel(icon = "refresh.svg", onClick = { onRefreshClick?.invoke() }) }

    val clearButton = JButton("Clear").apply {
        preferredSize = Dimension(100, 28)
    }

    val tableModel = DefaultTableModel(0, 0)
    val jtable = JTable(tableModel).apply {
        autoResizeMode = JTable.AUTO_RESIZE_OFF
        applyTableColumnsWidth(TableWidthLifecycle.widths)
    }

    fun removeAllRows() {
        tableModel.rowCount = 0
    }

    fun submitData(entries: List<LogEntry>) {
        removeAllRows()
        val header = arrayOf("time", "type", "packageName", "className")
        tableModel.setColumnIdentifiers(header)

        entries.forEach {
            val row = Vector<Any>()
            row.add(it.time ?: "")
            row.add(it.type ?: "")
            row.add(it.packageName ?: "")
            row.add(it.className ?: "")
            tableModel.addRow(row)
        }

        jtable.invalidate()
        jtable.repaint()
        jtable.applyTableColumnsWidth(TableWidthLifecycle.widths)

        searchField.putClientProperty("JTextField.placeholderText", "Search in ${tableModel.rowCount} items")

    }

    init {
        layout = BorderLayout()

        val topPanel = JPanel(BorderLayout()).apply {
            border = MatteBorder(0, 0, 1, 0, LightColorsConst.color_divider)
            add(refreshButton, BorderLayout.WEST)
            val searchPanel = JPanel(BorderLayout()).apply {
                add(searchButton, BorderLayout.WEST)
                add(searchField, BorderLayout.CENTER)
            }
            add(searchPanel, BorderLayout.CENTER)
            val rightControls = JPanel(FlowLayout(FlowLayout.RIGHT)).apply {
                add(clearButton)
            }
            add(rightControls, BorderLayout.EAST)
        }

        val topBoxPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(topPanel)
        }

        add(topBoxPanel, BorderLayout.NORTH)

        val scrollPane = JScrollPane(jtable).apply {
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            viewport.border = null
            setBorder(BorderFactory.createEmptyBorder())
        }
        add(scrollPane, BorderLayout.CENTER)
    }
}
