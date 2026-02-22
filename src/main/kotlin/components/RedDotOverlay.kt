package components

import java.awt.*
import javax.swing.JComponent

class RedDotOverlay : JComponent() {
    private var visible = false
    
    init {
        preferredSize = Dimension(8, 8)
        minimumSize = Dimension(8, 8)
        maximumSize = Dimension(8, 8)
        isOpaque = false
    }
    
    fun setVisibleView(visible: Boolean) {
        this.visible = visible
        repaint()
    }
    
    override fun paintComponent(g: Graphics) {
        if (visible) {
            val g2 = g as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.color = Color.RED
            g2.fillOval(0, 0, width, height)
        }
    }
}
