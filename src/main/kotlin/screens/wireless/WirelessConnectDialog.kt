package screens.wireless

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Window
import javax.swing.BorderFactory
import javax.swing.JDialog
import javax.swing.JPanel

class WirelessConnectDialog(owner: Window?) : JDialog(owner, "Connect via Wireless ADB", ModalityType.APPLICATION_MODAL) {
    init {
        defaultCloseOperation = DISPOSE_ON_CLOSE
        layout = BorderLayout()
        background = Color.WHITE

        // Empty placeholder panel
        val contentPanel = JPanel().apply {
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
        }

        add(contentPanel, BorderLayout.CENTER)

        setSize(400, 300)
        setLocationRelativeTo(owner)
    }
}
