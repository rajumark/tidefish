package screens.settings

import colors.LightColorsConst
import components.getIconJLabel
import decoreui.setHint
import java.awt.BorderLayout
import java.awt.Font
import java.awt.FontMetrics
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.border.MatteBorder
import javax.swing.JTable
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellRenderer

class JSettingsButtonsView : JPanel() {
    // Callbacks
    var onRefreshClick: (() -> Unit)? = null
    var onItemClick: ((SettingItem) -> Unit)? = null

    // Search UI copied style from JCallLogsView
    val searchField by lazy {
        JTextField().apply {
            columns = 15
            preferredSize = java.awt.Dimension(200, 28)
            setHint("Search")
            border = null
        }
    }
    private val searchButton by lazy { this.getIconJLabel(icon = "ic_search.svg", onClick = {}) }
    val refreshButton by lazy {
        this.getIconJLabel(icon = "refresh.svg", onClick = { onRefreshClick?.invoke() })
    }

    // Section containers
    private val pinnedSectionTitle = JLabel("Pinned").apply {
        font = font.deriveFont(Font.BOLD, 12f)
        border = BorderFactory.createEmptyBorder(10, 10, 4, 10)
        isOpaque = true
        background = Color.WHITE
    }
    private val otherSectionTitle = JLabel("Other Settings").apply {
        font = font.deriveFont(Font.BOLD, 12f)
        border = BorderFactory.createEmptyBorder(12, 10, 4, 10)
        isOpaque = true
        background = Color.WHITE
    }

    private val pinnedTableModel = object : DefaultTableModel(0, 0) {
        override fun isCellEditable(row: Int, column: Int): Boolean = false
    }
    private val otherTableModel = object : DefaultTableModel(0, 0) {
        override fun isCellEditable(row: Int, column: Int): Boolean = false
    }

    private val pinnedTable = JTable(pinnedTableModel).apply {
        setShowGrid(false)
        setBorder(BorderFactory.createEmptyBorder())
        tableHeader = null
        autoResizeMode = JTable.AUTO_RESIZE_OFF
        rowHeight = 28
        background = Color.WHITE
        isOpaque = true
        setCellSelectionEnabled(true)
        setRowSelectionAllowed(true)
        setColumnSelectionAllowed(true)
        setDefaultRenderer(Any::class.java, ChipCellRenderer())
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                handleTableClick(this@apply, pinnedTableModel, e)
            }
            override fun mousePressed(e: MouseEvent) {
                setPressedCell(true, this@apply, e)
            }
            override fun mouseReleased(e: MouseEvent) {
                clearPressedCell(true)
            }
        })
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) { if (e.isPopupTrigger) showContextMenu(this@apply, pinned = true, e) }
            override fun mouseReleased(e: MouseEvent) { if (e.isPopupTrigger) showContextMenu(this@apply, pinned = true, e) }
        })
    }
    private val otherTable = JTable(otherTableModel).apply {
        setShowGrid(false)
        setBorder(BorderFactory.createEmptyBorder())
        tableHeader = null
        autoResizeMode = JTable.AUTO_RESIZE_OFF
        rowHeight = 28
        background = Color.WHITE
        isOpaque = true
        setCellSelectionEnabled(true)
        setRowSelectionAllowed(true)
        setColumnSelectionAllowed(true)
        setDefaultRenderer(Any::class.java, ChipCellRenderer())
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                handleTableClick(this@apply, otherTableModel, e)
            }
            override fun mousePressed(e: MouseEvent) {
                setPressedCell(false, this@apply, e)
            }
            override fun mouseReleased(e: MouseEvent) {
                clearPressedCell(false)
            }
        })
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) { if (e.isPopupTrigger) showContextMenu(this@apply, pinned = false, e) }
            override fun mouseReleased(e: MouseEvent) { if (e.isPopupTrigger) showContextMenu(this@apply, pinned = false, e) }
        })
    }

    private val contentPanel = JPanel().apply {
        layout = javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS)
        border = BorderFactory.createEmptyBorder()
        isOpaque = true
        background = Color.WHITE
    }

    private val scrollPane by lazy {
        JScrollPane(contentPanel).apply {
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            viewport.border = null
            setBorder(BorderFactory.createEmptyBorder())
            background = Color.WHITE
            viewport.background = Color.WHITE
        }
    }

    // Icons for pin/unpin (lazy loaded like AppsListLeftView)
    private var pinIcon: ImageIcon? = null
    private var unpinIcon: ImageIcon? = null

    // Backing data
    private var allItems: List<SettingItem> = emptyList()
    private var pinnedIds: MutableSet<String> = mutableSetOf()

    init {
        layout = BorderLayout()
        border = null

        val topPanel = JPanel(BorderLayout()).apply {
            border = MatteBorder(0, 0, 1, 0, LightColorsConst.color_divider)
            val searchPanel = JPanel(BorderLayout()).apply {
                add(searchButton, BorderLayout.WEST)
                add(searchField, BorderLayout.CENTER)
            }
            add(refreshButton, BorderLayout.WEST)
            add(searchPanel, BorderLayout.CENTER)
        }

        val topPanelExtra = JPanel().apply {
            layout = javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS)
            add(topPanel)
        }
        add(topPanelExtra, BorderLayout.NORTH)
        add(scrollPane, BorderLayout.CENTER)

        // Preload icons respecting theme
        val extraName = if (first.menu.isDarkTheme()) "_dark" else ""
        utils.loadSvgAsIconAsync("/ic_pin${extraName}.svg", 18, 18) { icon ->
            pinIcon = icon
            repaint()
        }
        utils.loadSvgAsIconAsync("/ic_unpin${extraName}.svg", 18, 18) { icon ->
            unpinIcon = icon
            repaint()
        }

        // Search filter behavior
        searchField.document.addDocumentListener(object : javax.swing.event.DocumentListener {
            override fun insertUpdate(e: javax.swing.event.DocumentEvent?) = filterAndRender()
            override fun removeUpdate(e: javax.swing.event.DocumentEvent?) = filterAndRender()
            override fun changedUpdate(e: javax.swing.event.DocumentEvent?) = filterAndRender()
        })

        // Improve scroll speed
        scrollPane.verticalScrollBar.unitIncrement = 48

        // Re-layout tables on resize
        scrollPane.viewport.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                layoutTables()
            }
        })

        // Initial data
        submitSettings(SettingsDataList)
    }

    fun submitSettings(list: List<SettingItem>) {
        allItems = list
        pinnedIds = SettingsPinDatabase.getPinList().toMutableSet()
        filterAndRender()
    }

    private fun filterAndRender() {
        val q = searchField.text.orEmpty().trim().lowercase()
        val filtered = if (q.isEmpty()) allItems else allItems.filter { it.text.lowercase().contains(q) }
        renderSections(filtered)
        setSearchCountHint(filtered.size)
    }

    private fun renderSections(items: List<SettingItem>) {
        contentPanel.removeAll()

        val pinned = items.filter { pinnedIds.contains(it.id) }
        val other = items.filter { !pinnedIds.contains(it.id) }

        // Titles visible only if both exist
        val showTitles = true
        if (pinned.isNotEmpty()) {
            if (showTitles) contentPanel.add(pinnedSectionTitle)
            buildTableData(pinned, pinnedTableModel)
            contentPanel.add(pinnedTable)
        }
        if (other.isNotEmpty()) {
            if (showTitles) contentPanel.add(otherSectionTitle)
            buildTableData(other, otherTableModel)
            contentPanel.add(otherTable)
        }

        layoutTables()
        contentPanel.revalidate()
        contentPanel.repaint()
    }

    private fun buildTableData(items: List<SettingItem>, model: DefaultTableModel) {
        model.setRowCount(0)
        // Temporarily set 1 column; layoutTables will set proper column count and repack data.
        if (model.columnCount == 0) model.setColumnCount(1) else {
            // keep as-is; layoutTables will rebuild
        }
        // Fill a single-column model with items for now; layoutTables will reflow into grid
        items.forEach { model.addRow(arrayOf(it)) }
    }

    private fun layoutTables() {
        layoutTable(pinnedTable, pinnedTableModel)
        layoutTable(otherTable, otherTableModel)
    }

    private fun layoutTable(table: JTable, model: DefaultTableModel) {
        if (model.rowCount == 0) return
        // Collect items from current model into a flat list
        val items = mutableListOf<SettingItem>()
        for (r in 0 until model.rowCount) {
            for (c in 0 until model.columnCount) {
                val v = model.getValueAt(r, c)
                if (v is SettingItem) items.add(v)
            }
        }
        // Measure widest label
        val fm: FontMetrics = table.getFontMetrics(table.font)
        val padding = 20 // left+right padding inside cell
        val minCellWidth = 120
        val maxTextWidth = items.maxOfOrNull { fm.stringWidth(it.text) } ?: 80
        val cellWidth = maxOf(minCellWidth, maxTextWidth + padding)
        val availableWidth = scrollPane.viewport.width.takeIf { it > 0 } ?: contentPanel.width
        val columns = maxOf(1, if (availableWidth > 0) availableWidth / cellWidth else 3)

        // Rebuild model as grid
        model.setRowCount(0)
        model.setColumnCount(columns)
        val rows = ((items.size + columns - 1) / columns)
        var idx = 0
        for (r in 0 until rows) {
            val rowData = Array<Any?>(columns) { null }
            for (c in 0 until columns) {
                if (idx < items.size) {
                    rowData[c] = items[idx]
                }
                idx++
            }
            model.addRow(rowData)
        }
        // Set column widths
        for (c in 0 until columns) {
            val col = table.columnModel.getColumn(c)
            col.preferredWidth = cellWidth
            col.minWidth = 60
        }
        table.revalidate()
        table.repaint()
    }

    private inner class ChipCellRenderer : JLabel(), TableCellRenderer {
        init {
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LightColorsConst.color_divider, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
            )
        }
        override fun getTableCellRendererComponent(
            table: JTable?, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
        ): java.awt.Component {
            val item = value as? SettingItem
            text = item?.text ?: ""
            isOpaque = true
            val isPressed = isCellPressed(table, row, column)
            background = when {
                isPressed -> javax.swing.UIManager.getColor("Button.select")
                isSelected -> (table?.selectionBackground ?: javax.swing.UIManager.getColor("Table.selectionBackground"))
                else -> Color.WHITE
            }
            return this
        }
    }

    private fun handleTableClick(table: JTable, model: DefaultTableModel, e: MouseEvent) {
        val row = table.rowAtPoint(e.point)
        val col = table.columnAtPoint(e.point)
        if (row < 0 || col < 0) return
        val v = model.getValueAt(row, col) as? SettingItem ?: return
        if (e.button == MouseEvent.BUTTON1) {
            onItemClick?.invoke(v)
        }
    }

    private fun showContextMenu(table: JTable, pinned: Boolean, e: MouseEvent) {
        val row = table.rowAtPoint(e.point)
        val col = table.columnAtPoint(e.point)
        if (row < 0 || col < 0) return
        val model = table.model as DefaultTableModel
        val item = model.getValueAt(row, col) as? SettingItem ?: return
        table.requestFocusInWindow()
        table.changeSelection(row, col, false, false)
        val isPinned = pinnedIds.contains(item.id)
        val menu = javax.swing.JPopupMenu()
        val mi = javax.swing.JMenuItem(if (isPinned) "Unpin" else "Pin")
        mi.addActionListener {
            if (isPinned) {
                SettingsPinDatabase.unPinPackage(item.id)
                pinnedIds.remove(item.id)
            } else {
                SettingsPinDatabase.pinPackageName(item.id)
                pinnedIds.add(item.id)
            }
            filterAndRender()
        }
        menu.add(mi)
        menu.show(table, e.x, e.y)
    }

    // Pressed-state handling for click visual
    private var pinnedPressedCell: Pair<Int, Int>? = null
    private var otherPressedCell: Pair<Int, Int>? = null

    private fun setPressedCell(isPinnedTable: Boolean, table: JTable, e: MouseEvent) {
        val row = table.rowAtPoint(e.point)
        val col = table.columnAtPoint(e.point)
        if (row < 0 || col < 0) return
        if (isPinnedTable) {
            pinnedPressedCell = row to col
            pinnedTable.repaint()
        } else {
            otherPressedCell = row to col
            otherTable.repaint()
        }
    }
    private fun clearPressedCell(isPinnedTable: Boolean) {
        if (isPinnedTable) {
            pinnedPressedCell = null
            pinnedTable.repaint()
        } else {
            otherPressedCell = null
            otherTable.repaint()
        }
    }
    private fun isCellPressed(table: JTable?, row: Int, col: Int): Boolean {
        return when (table) {
            pinnedTable -> pinnedPressedCell?.let { it.first == row && it.second == col } == true
            otherTable -> otherPressedCell?.let { it.first == row && it.second == col } == true
            else -> false
        }
    }

    private fun setSearchCountHint(count: Int) {
        SwingUtilities.invokeLater {
            searchField.setHint("Search in ${count} items")
        }
    }
}
