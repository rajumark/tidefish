package decoreui

import java.awt.*
import javax.swing.JTextField
import javax.swing.border.AbstractBorder
import java.awt.*
import java.awt.*

import javax.swing.plaf.basic.BasicTextFieldUI

// Custom rounded border
class RoundedBorder(private val radius: Int) : AbstractBorder() {
    override fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.color = Color.GRAY
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius)
    }
}

// Custom UI for drawing hint text
class HintTextFieldUI(private val hint: String) : BasicTextFieldUI() {
    override fun paintSafely(g: Graphics) {
        super.paintSafely(g)
        val c = component as JTextField
        if (c.text.isEmpty() && !c.hasFocus()) {
            val g2 = g as Graphics2D
            g2.color = Color.GRAY
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            val ins = c.insets
            val fm = g2.fontMetrics
            val y = c.height / 2 + fm.ascent / 2 - 2
            g2.drawString(hint, ins.left, y) // respects margin/insets
        }
    }
}

// Extension function
fun JTextField.applyRoundedCorners(
    radius: Int = 4,
    height: Int = 26,
    horizontalPadding: Int = 4,
    hint: String? = null
) {
    this.border = RoundedBorder(radius * 2)
    val pref = this.preferredSize
    this.preferredSize = Dimension(pref.width, height)
    this.margin = Insets(0, horizontalPadding, 0, horizontalPadding) // typed text padding
    if (hint != null) {
        this.ui = HintTextFieldUI(hint)
    }
}
