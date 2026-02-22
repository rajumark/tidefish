package components.settings.account

import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JLabel
import javax.swing.JPanel

class AccountSettingsView(onClose:()->Unit) : JPanel() {

    init {
        layout = BorderLayout()
        background = Color.WHITE
        border = null

        add(JLabel("in Maintenance"), BorderLayout.CENTER)
    }
}
