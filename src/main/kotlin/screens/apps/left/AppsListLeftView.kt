package screens.apps.left

import colors.LightColorsConst
import components.getIconJLabel
import decoreui.setHint
import javax.swing.DefaultListModel
import javax.swing.JList
import javax.swing.ListCellRenderer
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextField
import javax.swing.BorderFactory
import javax.swing.border.MatteBorder
import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import java.awt.Insets
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JLabel
import javax.swing.ImageIcon
import javax.swing.border.Border
import javax.swing.border.CompoundBorder
import javax.swing.JPopupMenu

class AppsListLeftView : JPanel() {

      val searchField = JTextField().apply {
        columns = 15
        border = BorderFactory.createEmptyBorder(4, 4, 4, 4)
        // You can later apply your rounded corners extension if needed
        setHint("Search apps")
    }

    private val listModel = DefaultListModel<String>()
    val packageList = JList(listModel).apply {
        border = null // Remove JList border
        //applyRoundedSelection2()
        selectionBackground = LightColorsConst.color_background_sidemenu_item
    }

    var onFilterClick: (() -> Unit)? = null
    val filterButton = this.getIconJLabel(icon = "ic_filter.svg", onClick = {
        onFilterClick?.invoke()
    })
    private var originalFilterButtonBorder: Border? = null
    private var isFilterBadgeActive: Boolean = false

    init {
        layout = BorderLayout()
        border = null

        // Panel to hold search field + filter button in same line
        val topPanel = JPanel(BorderLayout()).apply {
            border = MatteBorder(0, 0, 1, 0, LightColorsConst.color_divider)
        }


        val searchButton = this.getIconJLabel(icon = "ic_search.svg", onClick = {

        })

        topPanel.add(searchField, BorderLayout.CENTER)
        topPanel.add(searchButton, BorderLayout.WEST)
        topPanel.add(filterButton, BorderLayout.EAST)

        add(topPanel, BorderLayout.NORTH)

        // Add JList in center with scroll
        val scrollPane = JScrollPane(packageList).apply {
            border = null // Remove scroll pane border
            viewport.border = null // Remove viewport border
        }
        add(scrollPane, BorderLayout.CENTER)

        // Start with empty; controller will populate
        searchField.setHint("Search in 0 items")
        originalFilterButtonBorder = filterButton.border

        packageList.cellRenderer = PackageCellRenderer(::isPinnedSupplier)

        // Preload pin/unpin icons for renderer
        val extraName = if (first.menu.isDarkTheme()) "_dark" else ""
        utils.loadSvgAsIconAsync("/ic_pin${extraName}.svg", 18, 18) { icon ->
            pinIcon = icon
            packageList.repaint()
        }
        utils.loadSvgAsIconAsync("/ic_unpin${extraName}.svg", 18, 18) { icon ->
            unpinIcon = icon
            packageList.repaint()
        }

        // Track hover to reveal pin icon for non-pinned items
        packageList.addMouseMotionListener(object : java.awt.event.MouseMotionAdapter() {
            override fun mouseMoved(e: java.awt.event.MouseEvent) {
                val idx = packageList.locationToIndex(e.point)
                val inIcon = if (idx >= 0) {
                    val cellBounds = packageList.getCellBounds(idx, idx)
                    e.x < (cellBounds.x + 28)
                } else false
                if (idx != hoveredIndex || inIcon != hoveredOverIconArea) {
                    val oldIdx = hoveredIndex
                    hoveredIndex = idx
                    hoveredOverIconArea = inIcon
                    if (oldIdx >= 0) packageList.repaint(packageList.getCellBounds(oldIdx, oldIdx))
                    if (idx >= 0) packageList.repaint(packageList.getCellBounds(idx, idx))
                }
            }
        })
        packageList.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseExited(e: java.awt.event.MouseEvent) {
                val oldIdx = hoveredIndex
                hoveredIndex = -1
                hoveredOverIconArea = false
                if (oldIdx >= 0) packageList.repaint(packageList.getCellBounds(oldIdx, oldIdx))
            }
        })
    }

    fun showContextMenu(menu: JPopupMenu, x: Int, y: Int) {
        menu.show(packageList, x, y)
    }

    fun submitAppsList(list: List<String>) {
        listModel.removeAllElements()
        list.forEach { listModel.addElement(it) }
        searchField.setHint("Search in ${list.size} items")
    }

    fun setFilterBadge(active: Boolean) {
        if (isFilterBadgeActive == active) return
        isFilterBadgeActive = active
        if (active) {
            val badge = RedDotBadgeBorder()
            filterButton.border = CompoundBorder(originalFilterButtonBorder, badge)
        } else {
            filterButton.border = originalFilterButtonBorder
        }
        filterButton.repaint()
    }

    private class RedDotBadgeBorder : Border {
        override fun getBorderInsets(c: Component?): Insets {
            return Insets(0, 0, 0, 0)
        }

        override fun isBorderOpaque(): Boolean = false

        override fun paintBorder(c: Component?, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
            val dotDiameter = 8
            val dotX = x + width - dotDiameter
            val dotY = y
            val oldColor = g.color
            g.color = Color(0xE5, 0x39, 0x35) // red 600
            g.fillOval(dotX, dotY, dotDiameter, dotDiameter)
            g.color = oldColor
        }
    }

    // Pinned state and toggle support
    private var pinnedPackages: Set<String> = emptySet()
    fun setPinnedPackages(set: Set<String>) {
        pinnedPackages = set
        packageList.repaint()
    }
    private fun isPinnedSupplier(pkg: String): Boolean = pinnedPackages.contains(pkg)

    var onPinToggle: ((packageName: String, shouldPin: Boolean) -> Unit)? = null

    init {
        packageList.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent) {
                val index = packageList.locationToIndex(e.point)
                if (index >= 0) {
                    val cellBounds = packageList.getCellBounds(index, index)
                    // Consider first 24px as icon area
                    val clickedInIcon = e.x < (cellBounds.x + 28)
                    if (clickedInIcon) {
                        val pkg = listModel.getElementAt(index)
                        val isPinned = pinnedPackages.contains(pkg)
                        onPinToggle?.invoke(pkg, !isPinned)
                    }
                }
            }
        })
    }

    private inner class PackageCellRenderer(val isPinned: (String) -> Boolean) : JPanel(), ListCellRenderer<String> {
        private val pinIconLabel = JLabel()
        private val label = JLabel()

        init {
            layout = BorderLayout()
            pinIconLabel.preferredSize = Dimension(24, 24)
            pinIconLabel.minimumSize = Dimension(24, 24)
            pinIconLabel.horizontalAlignment = JLabel.CENTER
            add(pinIconLabel, BorderLayout.WEST)
            add(label, BorderLayout.CENTER)
            isOpaque = true
        }

        override fun getListCellRendererComponent(
            list: JList<out String>?,
            value: String?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): Component {
            val pkg = value.orEmpty()
            label.text = pkg

            val pinned = isPinned(pkg)
            val shouldShowIcon = pinned || (index == hoveredIndex && hoveredOverIconArea)
            pinIconLabel.icon = when {
                !shouldShowIcon -> null
                pinned -> unpinIcon ?: pinIconLabel.icon
                else -> pinIcon ?: pinIconLabel.icon
            }
            label.border = BorderFactory.createEmptyBorder()

            background = if (isSelected) LightColorsConst.color_background_sidemenu_item else list?.background
            foreground = if (isSelected) list?.selectionForeground else list?.foreground
            return this
        }
    }

    // Cached icons for cell renderer
    private var pinIcon: ImageIcon? = null
    private var unpinIcon: ImageIcon? = null

    // Hover state for revealing non-pinned icon
    private var hoveredIndex: Int = -1
    private var hoveredOverIconArea: Boolean = false
}
