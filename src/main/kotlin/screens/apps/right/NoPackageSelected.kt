package screens.apps.right

import colors.LightColorsConst
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class NoPackageSelected : JPanel() {

    private val messageLabel = JLabel("Select package to view details", SwingConstants.CENTER)

    init {
        layout = BorderLayout()
        border = BorderFactory.createEmptyBorder(0, 0, 0, 0)

        background = LightColorsConst.color_background_sidemenu

        messageLabel.font = messageLabel.font.deriveFont(Font.PLAIN, messageLabel.font.size2D + 2f)
        messageLabel.foreground = Color.DARK_GRAY

        add(messageLabel, BorderLayout.CENTER)
    }
}
