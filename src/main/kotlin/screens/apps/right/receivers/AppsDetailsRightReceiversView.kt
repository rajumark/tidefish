package screens.apps.right.receivers

import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel

class AppsDetailsRightReceiversView : JPanel() {
    init {
        layout = BorderLayout()
        add(JLabel("Recievers"), BorderLayout.NORTH)
    }
}


