package components

import colors.LightColorsConst.color_background_hover_icon_button
import colors.LightColorsConst.color_background_pressed_icon_button
import com.formdev.flatlaf.extras.FlatSVGIcon
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants


fun JPanel.getIconJLabel(
    icon:String,
    size: Int = 24,
    iconSize: Int = 16,
    onClick: () -> Unit
): JLabel {
    val svgIcon = FlatSVGIcon(icon, iconSize, iconSize)

    return object : JLabel(svgIcon) {
        private var hovered = false
        private var pressed = false
        private val radius = 4

        init {
            preferredSize = Dimension(size, size)
            minimumSize = Dimension(size, size)
            maximumSize = Dimension(size, size)

            horizontalAlignment = SwingConstants.CENTER
            verticalAlignment = SwingConstants.CENTER

            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)

            addMouseListener(object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent) {
                    hovered = true
                    repaint()
                }

                override fun mouseExited(e: MouseEvent) {
                    hovered = false
                    pressed = false
                    repaint()
                }

                override fun mousePressed(e: MouseEvent) {
                    pressed = true
                    repaint()
                }

                override fun mouseReleased(e: MouseEvent) {
                    if (pressed && hovered) {
                        onClick()
                    }
                    pressed = false
                    repaint()
                }
            })
        }

        override fun paintComponent(g: Graphics) {
            val g2 = g as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            when {
                pressed -> {
                    g2.color = color_background_pressed_icon_button
                    g2.fillRoundRect(0, 0, width, height, radius * 2, radius * 2)
                }
                hovered -> {
                    g2.color = color_background_hover_icon_button
                    g2.fillRoundRect(0, 0, width, height, radius * 2, radius * 2)
                }
            }

            super.paintComponent(g)
        }
    }
}
