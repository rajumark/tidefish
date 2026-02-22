package screens.apps.right.permissions

import colors.LightColorsConst
import components.getIconJLabel
import decoreui.applyRoundedSelection2
import decoreui.setHint
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.FlowLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.net.URI
import java.net.URLEncoder
import javax.swing.*
import javax.swing.border.MatteBorder

class AppsDetailsRightPermissionsView : JPanel() {

    val searchField = JTextField().apply {
        columns = 15
        setHint("Search")
        border = null
    }

    var onRefreshClick: (() -> Unit)? = null
    var onRestartClick: (() -> Unit)? = null
    var onAppInfoClick: (() -> Unit)? = null
    var onGrantAllClick: (() -> Unit)? = null
    var onRevokeAllClick: (() -> Unit)? = null
    var onTogglePermission: ((permission: String, granted: Boolean) -> Unit)? = null

    private val searchButton = this.getIconJLabel(icon = "ic_search.svg", onClick = {})
    private val refreshButton = this.getIconJLabel(icon = "refresh.svg", onClick = {
        onRefreshClick?.invoke()
    })

    val grantAllButton = JButton("Grant All")
    val revokeAllButton = JButton("Revoke All")
    val restartButton = JButton("Restart")
    val appInfoButton = JButton("App Info")

    // Horizontal radio buttons for selecting permission category
    val filterRequestedRadio = JRadioButton("Requested permissions")
    val filterInstallRadio = JRadioButton("Install permissions")
    val filterRuntimeRadio = JRadioButton("Runtime permissions")
    private val filterGroup = ButtonGroup().apply {
        add(filterRuntimeRadio)
        add(filterRequestedRadio)
        add(filterInstallRadio)

    }
    init {
        filterRuntimeRadio.isSelected = true
        // Repaint list when filter changes so renderer can update checkbox visibility
        val repaintList = { SwingUtilities.invokeLater { list.repaint() } }
        filterRequestedRadio.addActionListener { repaintList() }
        filterInstallRadio.addActionListener { repaintList() }
        filterRuntimeRadio.addActionListener { repaintList() }
    }
    fun selectedFilterIndex(): Int = when {
        filterRequestedRadio.isSelected -> 0
        filterInstallRadio.isSelected -> 1
        else -> 2
    }

    data class PermissionItem(var granted: Boolean, val permission: String)

    private val listModel = DefaultListModel<PermissionItem>()

    private val checkboxMeasure = JCheckBox()

    val list = JList(listModel).apply {
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        border = BorderFactory.createEmptyBorder()
        applyRoundedSelection2()
        cellRenderer = ListCellRenderer { _, value, index, isSelected, cellHasFocus ->
            val panel = JPanel(BorderLayout())
            val cb = JCheckBox()
            cb.isSelected = value.granted
            cb.isOpaque = false
            // In Install filter, show status but disable interactions
            cb.isEnabled = !filterInstallRadio.isSelected
            val label = JLabel(value.permission)
            val inner = JPanel(FlowLayout(FlowLayout.LEFT, 8, 4))
            inner.isOpaque = false
            // Show checkbox only when not in "Requested permissions" filter
            if (!filterRequestedRadio.isSelected) {
                inner.add(cb)
            }
            inner.add(label)
            panel.add(inner, BorderLayout.CENTER)
            if (isSelected) {
                panel.background = LightColorsConst.color_background_sidemenu_item
                panel.isOpaque = true
            } else {
                panel.background = UIManager.getColor("List.background")
                panel.isOpaque = true
            }
            panel
        }
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val idx = locationToIndex(e.point)
                if (idx < 0) return
                val cellBounds = getCellBounds(idx, idx) ?: return
                val relativeX = e.x - cellBounds.x
                // Only allow toggling in Runtime filter (not Requested, not Install)
                val toggleZoneWidth = if (!filterRequestedRadio.isSelected && !filterInstallRadio.isSelected) checkboxMeasure.preferredSize.width + 12 else 0 // checkbox + gap
                if (toggleZoneWidth > 0 && relativeX <= toggleZoneWidth) {
                    val item = listModel.getElementAt(idx)
                    item.granted = !item.granted
                    repaint(cellBounds)
                    onTogglePermission?.invoke(item.permission, item.granted)
                } else {
                    selectedIndex = idx
                }
            }
        })
        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_SPACE) {
                    // Ignore toggle via space when checkbox is hidden or disabled (Requested or Install)
                    if (filterRequestedRadio.isSelected || filterInstallRadio.isSelected) return
                    val idx = selectedIndex
                    if (idx >= 0) {
                        val item = listModel.getElementAt(idx)
                        item.granted = !item.granted
                        val b = getCellBounds(idx, idx)
                        if (b != null) repaint(b) else repaint()
                        onTogglePermission?.invoke(item.permission, item.granted)
                        e.consume()
                    }
                }
            }
        })
        // Context menu on right click
        fun showContextMenu(e: MouseEvent, index: Int) {
            if (index < 0) return
            selectedIndex = index
            val item = listModel.getElementAt(index)
            val permissionText = item.permission

            val menu = JPopupMenu()
            val copyItem = JMenuItem("Copy")
            copyItem.addActionListener {
                val sel = StringSelection(permissionText)
                Toolkit.getDefaultToolkit().systemClipboard.setContents(sel, null)
            }
            menu.add(copyItem)

            val docItem = JMenuItem("Online Documentation")
            docItem.addActionListener {
                val query = "Android permission $permissionText meaning"
                val encoded = URLEncoder.encode(query, "UTF-8")
                val uri = URI.create("https://www.google.com/search?q=$encoded")
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(uri)
                }
            }
            menu.add(docItem)

            val askAi = JMenu("Ask AI")
            val chatGpt = JMenuItem("ChatGPT")
            chatGpt.addActionListener {
                val query = "Android permission $permissionText meaning"
                val encoded = URLEncoder.encode(query, "UTF-8")
                val uri = URI.create("https://chat.openai.com/?q=$encoded")
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(uri)
                }
            }
            val perplexity = JMenuItem("Perplexity")
            perplexity.addActionListener {
                val query = "Android permission $permissionText meaning"
                val encoded = URLEncoder.encode(query, "UTF-8")
                val uri = URI.create("https://www.perplexity.ai/?q=$encoded")
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(uri)
                }
            }
            askAi.add(chatGpt)
            askAi.add(perplexity)
            menu.add(askAi)

            menu.show(this, e.x, e.y)
        }

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (e.isPopupTrigger) {
                    val idx = locationToIndex(e.point)
                    showContextMenu(e, idx)
                }
            }
            override fun mouseReleased(e: MouseEvent) {
                if (e.isPopupTrigger) {
                    val idx = locationToIndex(e.point)
                    showContextMenu(e, idx)
                }
            }
        })
        fixedCellHeight = 28
    }

    init {
        layout = BorderLayout()
        border = null

        // Top panel with search and buttons
        val topPanel = JPanel(BorderLayout()).apply {
            border = MatteBorder(0, 0, 1, 0, LightColorsConst.color_divider)
            val left = JPanel(BorderLayout()).apply {
                add(searchButton, BorderLayout.WEST)
                add(searchField, BorderLayout.CENTER)
            }
            val right = JPanel(FlowLayout(FlowLayout.RIGHT, 6, 6)).apply {
                add(grantAllButton)
                add(revokeAllButton)
                add(restartButton)
                add(appInfoButton)
                add(refreshButton)
            }
            add(left, BorderLayout.CENTER)
            add(right, BorderLayout.EAST)
        }
        add(topPanel, BorderLayout.NORTH)

        // Radio buttons row (replaces combo box/tabs)
        val filterRow = JPanel(FlowLayout(FlowLayout.LEFT, 8, 8)).apply {
            add(filterRuntimeRadio)
            add(filterRequestedRadio)
            add(filterInstallRadio)
        }

        val listScroll = JScrollPane(list).apply {
            viewport.border = null
            border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
        }

        // Center panel: combo on top, table fills remaining space
        val centerPanel = JPanel(BorderLayout())
        centerPanel.add(filterRow, BorderLayout.NORTH)
        centerPanel.add(listScroll, BorderLayout.CENTER)

        add(centerPanel, BorderLayout.CENTER)

        setSearchCountHint(0)
    }

    fun setSearchCountHint(count: Int) {
        SwingUtilities.invokeLater {
            searchField.setHint("Search in $count permissions")
        }
    }

    fun submitPermissionsRows(rows: List<Pair<Boolean, String>>) {
        SwingUtilities.invokeLater {
            listModel.clear()
            rows.forEach { (granted, perm) ->
                listModel.addElement(PermissionItem(granted, perm))
            }
            setSearchCountHint(listModel.size())
            list.repaint()
        }
    }

    fun grantAll() {
        for (i in 0 until listModel.size()) {
            listModel.get(i).granted = true
        }
        list.repaint()
    }

    fun revokeAll() {
        for (i in 0 until listModel.size()) {
            listModel.get(i).granted = false
        }
        list.repaint()
    }
}
