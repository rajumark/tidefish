package screens.messages

import colors.LightColorsConst
import components.getIconJLabel
import decoreui.setHint
import screens.messages.MMessagesSortingString.listmessagesColumSorting
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.util.*
import javax.swing.*
import javax.swing.border.MatteBorder
import javax.swing.table.DefaultTableModel
import utils.table.TableWidthMessages
import utils.table.applyTableColumnsWidth

class JMessagesView : JPanel() {
    var onRefreshClick:(()->Unit)?=null
    val checkBoxShowOriginal by lazy {
        JCheckBox("Show Original")
    }

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
            val rightControls = JPanel(FlowLayout(FlowLayout.RIGHT)).apply {
                add(checkBoxShowOriginal)
            }
            add(rightControls, BorderLayout.EAST)
        }
    }
    val topPanelExtra by lazy {
        JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(topPanel)
        }
    }


    val defaultTableModelMessage = DefaultTableModel(0, 0)

    private val jtable by lazy {
        JTable().apply {
            autoResizeMode = JTable.AUTO_RESIZE_OFF
            setModel(defaultTableModelMessage)
            tableHeader.reorderingAllowed = false
            setShowGrid(false)
            fillsViewportHeight = true
            setBorder(BorderFactory.createEmptyBorder())
            applyTableColumnsWidth(TableWidthMessages.widths)
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
        defaultTableModelMessage.rowCount = 0

    }

    fun submitMessageList(messagesList: MutableList<MutableMap<String, String?>>) {
        removeAllRows()
        addInJTableDataModel(messagesList)

    }

    private fun rePaintTable() {
        jtable.invalidate()
        jtable.repaint()
    }

    private fun addInJTableDataModel(messagesList: MutableList<MutableMap<String, String?>>) {
        val mainlist = messagesList
        val keys: List<String> = mainlist
            .flatMap { it.keys }
            .filter { !it.startsWith("Row: ") }.distinct().sortedBy { kk ->
                val index = listmessagesColumSorting.indexOfFirst { it == kk }
                if (index == -1) {
                    9999
                } else {
                    index
                }
            }

        val header = arrayOf("No.") + keys.toTypedArray()
        defaultTableModelMessage.setColumnIdentifiers(header)

        messagesList.forEachIndexed { index, modelmessage ->
            val data: Vector<Any> = Vector<Any>()
            keys.forEachIndexed { index2, singleKey ->
                if (index2 == 0) {
                    data.add((index + 1).toString())
                }
                data.add(modelmessage.getOrDefault(singleKey, "") ?: "")

            }
            defaultTableModelMessage.addRow(data)
        }
        rePaintTable()
        setSearchCountHint(defaultTableModelMessage.rowCount)
        jtable.applyTableColumnsWidth(TableWidthMessages.widths)

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