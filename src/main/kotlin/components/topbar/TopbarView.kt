package components.topbar

import colors.LightColorsConst
import components.RedDotOverlay
import components.deviceselector.JDeviceSelectorView
import components.getIconJLabel
import components.settings.SoftwareSettingsDialog
import java.awt.Dimension
import java.awt.Font
import javax.swing.*
import javax.swing.border.MatteBorder

class TopbarView : JPanel() {
    var onMenuClick: (() -> Unit)? = null
    var onQuickTilesClick: (() -> Unit)? = null
    private val titleLabel = JLabel("ADBCard")
    val deviceSelectorView = JDeviceSelectorView()
    val settingsDialog by lazy {
        SoftwareSettingsDialog()
    }


    init {
        layout = BoxLayout(this, BoxLayout.X_AXIS)

        preferredSize = Dimension(0, 32)
        background = LightColorsConst.color_background_sidemenu
        border = MatteBorder(0, 0, 1, 0, LightColorsConst.color_divider)

        // Configure title label
        titleLabel.font = Font("Arial", Font.PLAIN, 14)
        titleLabel.alignmentY = CENTER_ALIGNMENT
        add(Box.createRigidArea(Dimension(8, 0)))

        // Menu button
        val menuButton = this.getIconJLabel(icon = "ic_menu.svg", onClick = {
            onMenuClick?.invoke()
        })
        menuButton.toolTipText = "Menu"
        add(menuButton)
        add(Box.createRigidArea(Dimension(8, 0)))

        // Title label
        add(titleLabel)
        add(Box.createHorizontalStrut(10))

        // Device selector
        deviceSelectorView.alignmentY = CENTER_ALIGNMENT
        add(deviceSelectorView)

        add(Box.createHorizontalGlue())


        // Feedback button
        val feedbackButton = this.getIconJLabel(icon = "ic_feedback.svg", onClick = {
            FeedbackUserDialog().isVisible = true
        })
        feedbackButton.toolTipText = "Send Feedback"
        add(feedbackButton)
        add(Box.createRigidArea(Dimension(4, 0)))

        // Settings button
        val settingsButton = this.getIconJLabel(icon = "ic_ij_settings.svg", onClick = {
            settingsDialog.isVisible = true
        })
        settingsButton.toolTipText = "Settings"
//        add(settingsButton)
//        add(Box.createRigidArea(Dimension(4, 0)))
        // Settings button
        val quickTilesButton = this.getIconJLabel(icon = "ic_toolbar.svg", onClick = {
            onQuickTilesClick?.invoke()
        })
        quickTilesButton.toolTipText = "Quick Settings"
        add(quickTilesButton)
        add(Box.createRigidArea(Dimension(4, 0)))

    }

}
