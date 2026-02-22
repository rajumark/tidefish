package screens.apps.right.services

import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel

class AppsDetailsRightServicesView : JPanel() {
    init {
        layout = BorderLayout()
        add(JLabel("Services"), BorderLayout.NORTH)
    }
}


