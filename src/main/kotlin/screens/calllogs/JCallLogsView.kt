package screens.calllogs

import colors.LightColorsConst
import components.getIconJLabel
import decoreui.setHint
import screens.calllogs.MCallsSortingString.listcallesColumSorting
import utils.table.TableWidthCallLogs
import utils.table.applyTableColumnsWidth
import java.awt.BorderLayout
import java.awt.Dimension
import java.util.*
import javax.swing.*
import javax.swing.border.MatteBorder
import javax.swing.table.DefaultTableModel

class JCallLogsView : JPanel() {
    var onRefreshClick: (() -> Unit)? = null
    val searchField by lazy {
        JTextField().apply {
            columns = 15
            preferredSize = Dimension(200, 28)
            setHint("Search")
            border = null
        }
    }

    private val searchButton by lazy { this.getIconJLabel(icon = "ic_search.svg", onClick = {}) }
    val refreshButton by lazy {
        this.getIconJLabel(icon = "refresh.svg", onClick = {
            onRefreshClick?.invoke()
        })
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


    val defaultTableModelCall = DefaultTableModel(0, 0)

    private val jtable by lazy {
        JTable().apply {
            autoResizeMode = JTable.AUTO_RESIZE_OFF
            setModel(defaultTableModelCall)
            tableHeader.reorderingAllowed = false
            setShowGrid(false)
            fillsViewportHeight = true
            setBorder(BorderFactory.createEmptyBorder())

            applyTableColumnsWidth(TableWidthCallLogs.widths)
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
        defaultTableModelCall.rowCount = 0

    }

    fun submitCallList(callsList: MutableList<MutableMap<String, String?>>) {
        removeAllRows()
        addInJTableDataModel(callsList)

    }

    private fun rePaintTable() {
        jtable.invalidate()
        jtable.repaint()
    }

    private fun addInJTableDataModel(callsList: MutableList<MutableMap<String, String?>>) {
        val mainlist = callsList
        val keys: List<String> = mainlist
            .flatMap { it.keys }
            .filter { !it.startsWith("Row: ") }.distinct().sortedBy { kk ->
                val index = listcallesColumSorting.indexOfFirst { it == kk }
                if (index == -1) {
                    9999
                } else {
                    index
                }
            }

        val header = arrayOf("No.") + keys.toTypedArray()
        defaultTableModelCall.setColumnIdentifiers(header)

        callsList.forEachIndexed { index, modelcall ->
            val data: Vector<Any> = Vector<Any>()
            keys.forEachIndexed { index2, singleKey ->
                if (index2 == 0) {
                    data.add((index + 1).toString())
                }
                data.add(modelcall.getOrDefault(singleKey, "") ?: "")

            }
            defaultTableModelCall.addRow(data)
        }
        rePaintTable()
        setSearchCountHint(defaultTableModelCall.rowCount)
        jtable.applyTableColumnsWidth(TableWidthCallLogs.widths)

    }

    private fun setSearchCountHint(count: Int) {
        SwingUtilities.invokeLater {
            searchField.setHint("Search in ${count} items")
        }
    }

    init {
        layout = BorderLayout()
        add(topPanelExtra, BorderLayout.NORTH)
        add(scrollPane, BorderLayout.CENTER)

    }


}

