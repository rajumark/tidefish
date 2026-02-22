package components

import javax.swing.JSeparator
import javax.swing.SwingConstants

fun horizontalDivider(): JSeparator {
    val sep = JSeparator(SwingConstants.HORIZONTAL)
    sep.maximumSize = java.awt.Dimension(Int.MAX_VALUE, 2) // full width, thin line
    return sep
}
