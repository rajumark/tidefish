package screens.notifications

import colors.LightColorsConst
import components.getIconJLabel
import decoreui.applyStyleSplitPan
import decoreui.setHint
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.*
import javax.swing.border.MatteBorder

class JNotificationsView : JPanel() {
    var onRefreshClick:(()->Unit)?=null

    private val searchButton by lazy { this.getIconJLabel(icon = "ic_search.svg", onClick = {}) }
    val refreshButton by lazy { this.getIconJLabel(icon = "refresh.svg", onClick = { onRefreshClick?.invoke() }) }

    val searchField by lazy {
        JTextField(15).apply {
            preferredSize = Dimension(200, 28)
            border = null
        }
    }

    val leftListModel = DefaultListModel<NotificationMaster>()

    private val notificationList by lazy {
        JList(leftListModel).apply {
            border = null
            cellRenderer = NotificationListRenderer()
            selectionBackground = LightColorsConst.color_background_sidemenu_item
            selectionForeground = LightColorsConst.color_textview_on_background
        }
    }

    private val scrollPaneLeft by lazy {
        JScrollPane(notificationList).apply {
            viewport.border = null
            setBorder(BorderFactory.createEmptyBorder())
        }
    }

    private val detailTextArea by lazy {
        JTextArea().apply {
            isEditable = false
            font = font.deriveFont(12f)
            lineWrap = true
            wrapStyleWord = true
            margin = java.awt.Insets(10, 10, 10, 10)
        }
    }

    private val scrollPaneRight by lazy {
        JScrollPane(detailTextArea).apply {
            viewport.border = null
            setBorder(BorderFactory.createEmptyBorder())
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        }
    }

    val leftPanel by lazy {
        JPanel(BorderLayout()).apply {
            preferredSize = Dimension(300, 0)
            add(scrollPaneLeft, BorderLayout.CENTER)
        }
    }

    val rightPanel by lazy {
        JPanel(BorderLayout()).apply {
            add(scrollPaneRight, BorderLayout.CENTER)
        }
    }

    private val splitPane by lazy {
        JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel).apply {
            dividerSize = 5
            resizeWeight = 0.1
            isContinuousLayout = true
            applyStyleSplitPan()
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

    init {
        layout = BorderLayout()
        add(topPanel, BorderLayout.NORTH)
        add(splitPane, BorderLayout.CENTER)
    }

    fun submitNotificationList(list: List<NotificationMaster>) {
        println("DEBUG: submitNotificationList called with ${list.size} items")
        leftListModel.clear()
        list.forEach { 
            leftListModel.addElement(it)
            println("DEBUG: Added notification: ${it.packageName} - ${it.title}")
        }
        searchField.setHint("Search in ${leftListModel.size()} notifications")
        println("DEBUG: List model now has ${leftListModel.size()} items")
    }

    fun showNotificationDetails(notification: NotificationMaster) {
        println("DEBUG: showNotificationDetails called for ${notification.packageName}")
        val dumpText = notification.rawdata.firstOrNull()?.get("dump") ?: "No dump data available"
        detailTextArea.text = dumpText
        detailTextArea.caretPosition = 0 // Scroll to top
        println("DEBUG: Set detail text with ${dumpText.length} characters")
    }

    inner class NotificationListRenderer : DefaultListCellRenderer() {
        private val timeFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

        override fun getListCellRendererComponent(
            list: JList<*>?, value: Any?, index: Int,
            isSelected: Boolean, cellHasFocus: Boolean
        ): Component {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
            text = if (value is NotificationMaster) {
                val displayName = MADBNotifications.getPackageDisplayName(value.packageName)
                val title = value.title.ifBlank { "No Title" }
                val timeStr = if (value.postTime > 0) timeFormat.format(Date(value.postTime)) else ""
                "$displayName: $title${if (timeStr.isNotEmpty()) " - $timeStr" else ""}"
            } else {
                "Unknown"
            }
            return this
        }
    }

    fun addListSelectionListener(listener: (NotificationMaster) -> Unit) {
        notificationList.addListSelectionListener {
            if (!it.valueIsAdjusting) {
                val selected = notificationList.selectedValue
                if (selected != null) listener(selected)
            }
        }
    }
}
