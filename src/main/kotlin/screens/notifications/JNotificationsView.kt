package screens.notifications

import colors.LightColorsConst
import components.getIconJLabel
import decoreui.setHint
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*
import javax.swing.border.MatteBorder

class JNotificationsView : JPanel() {
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

    private val comingSoonLabel by lazy {
        JLabel("Notifications coming soon").apply {
            horizontalAlignment = SwingConstants.CENTER
            font = font.deriveFont(18f)
        }
    }

    init {
        layout = BorderLayout()
        add(topPanelExtra, BorderLayout.NORTH)
        
        val centerPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(Box.createVerticalGlue())
            add(comingSoonLabel)
            add(Box.createVerticalGlue())
        }
        
        add(centerPanel, BorderLayout.CENTER)
    }
}
