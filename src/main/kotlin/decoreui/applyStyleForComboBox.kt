package decoreui

import javax.swing.JComboBox
import kotlin.collections.firstOrNull

fun JComboBox<String>.applyStyleForComboBox() {
    isFocusable = false
    isOpaque = false
    background = null
    border = null
    font = font.deriveFont(13f)
    (components.firstOrNull { it is javax.swing.JButton } as? javax.swing.JButton)?.apply {
        isContentAreaFilled = false
        isOpaque = false
        border = null
        background = null
    }
}