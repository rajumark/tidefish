package screens.apps.right.dex

import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel

class AppsDetailsRightDexView : JPanel() {
    init {
        layout = BorderLayout()
        add(JLabel("Dex"), BorderLayout.NORTH)
    }
}


