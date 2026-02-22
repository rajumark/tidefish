package utils

import java.awt.Desktop
import java.net.URI
import javax.swing.JOptionPane


fun JopenLink(url: String) {
    try {
        if (Desktop.isDesktopSupported()) {
            val uri = URI(url)
            Desktop.getDesktop().browse(uri)
        } else {
            JOptionPane.showMessageDialog(null, "Desktop is not supported on this platform.")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        JOptionPane.showMessageDialog(null, "Failed to open URL: ${e.message}")
    }
}