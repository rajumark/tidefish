package screens.apps.right.providers

import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel

class AppsDetailsRightProvidersView : JPanel() {
    init {
        layout = BorderLayout()
        add(JLabel("Content Providers"), BorderLayout.NORTH)
    }
}


