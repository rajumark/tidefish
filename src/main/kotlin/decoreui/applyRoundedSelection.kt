package decoreui

import colors.LightColorsConst
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.DefaultListCellRenderer
import javax.swing.JComponent
import javax.swing.JList
import javax.swing.border.EmptyBorder

fun <T> JList<T>.applyRoundedSelection() {
    val radius = 4
    val horizontalMargin = 8 // left and right margin
    val textSize = 13f
    this.cellRenderer = object : DefaultListCellRenderer() {

        private var cellSelected = false

        override fun getListCellRendererComponent(
            list: JList<*>,
            value: Any?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): Component {
            val component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)

            cellSelected = isSelected

            if (component is JComponent) {
                component.isOpaque = false
                // Top, left, bottom, right padding
                component.border = EmptyBorder(5, horizontalMargin, 5, horizontalMargin)
            }

            component.foreground = list.foreground
            component.font = component.font.deriveFont(textSize)
            return component
        }

        override fun paintComponent(g: Graphics) {
            val g2 = g as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            g2.color = if (cellSelected) {
                LightColorsConst.color_background_sidemenu_item
            } else {
                background
            }
            g2.fillRoundRect(0, 0, width, height, radius * 2, radius * 2)

            super.paintComponent(g)
        }
    }

    this.background = LightColorsConst.color_background_sidemenu
}
