package screens.apps.right.datafiles

import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel

class AppsDetailsRightDataFilesView : JPanel() {
    init {
        layout = BorderLayout()
        add(JLabel("Data Files"), BorderLayout.NORTH)
    }
}


