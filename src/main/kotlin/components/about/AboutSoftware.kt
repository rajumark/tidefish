package components.about

import adb.ADBHelper
import java.awt.Component
import java.awt.Desktop
import java.awt.FlowLayout
import java.awt.Image
import java.net.URI
import java.util.*
import javax.swing.*


fun showAboutDialog(parent: JFrame) {
    val (appName, version) = parent.getAppInfo()
    val creator = "ADBCard Contributors"
    val website = "https://github.com/ADBCard/ADBCard"
    val javaVersion = System.getProperty("java.version")
    val swingVersion = UIManager.getLookAndFeel().name
    val osName = System.getProperty("os.name")
    val osVersion = System.getProperty("os.version")

    // Main container
    val mainPanel = JPanel()
    mainPanel.layout = BoxLayout(mainPanel, BoxLayout.Y_AXIS)
    mainPanel.border = BorderFactory.createEmptyBorder(20, 20, 20, 20)

    // Top panel with image and app name
    val topPanel = JPanel()
    topPanel.layout = BoxLayout(topPanel, BoxLayout.X_AXIS)
    topPanel.alignmentX = Component.LEFT_ALIGNMENT
    topPanel.border = BorderFactory.createEmptyBorder(0, 0, 15, 0)

    // Load logo image
    val imageUrl = parent.javaClass.getResource("/ic_adbcard_logo.png")
    val originalIcon = if (imageUrl != null) ImageIcon(imageUrl) else null
    val scaledIcon = originalIcon?.image?.getScaledInstance(32, 32, Image.SCALE_SMOOTH)?.let {
        ImageIcon(it)
    }
    val imageLabel = JLabel(scaledIcon)
    imageLabel.border = BorderFactory.createEmptyBorder(0, 0, 0, 12)

    val nameLabel = JLabel("<html><div style='font-size:14pt;'><b>$appName</b></div>Version $version</html>")
    nameLabel.alignmentY = Component.CENTER_ALIGNMENT

    topPanel.add(imageLabel)
    topPanel.add(nameLabel)

    // Info section
    val infoLabel = JLabel("""
        <html>
            <b>Created by:</b> $creator<br><br>
            <b>Java Version:</b> $javaVersion<br>
            <b>Swing Theme:</b> $swingVersion<br>
            <b>OS:</b> $osName $osVersion
        </html>
    """.trimIndent())
    infoLabel.alignmentX = Component.LEFT_ALIGNMENT
    infoLabel.border = BorderFactory.createEmptyBorder(0, 0, 15, 0)

    // Button section
    val buttonPanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))
    buttonPanel.alignmentX = Component.LEFT_ALIGNMENT
    val websiteButton = JButton("Visit Website")
    websiteButton.addActionListener { openWebpage(URI(website)) }
    buttonPanel.add(websiteButton)

    // Assemble
    mainPanel.add(topPanel)
    mainPanel.add(infoLabel)
    mainPanel.add(buttonPanel)

    // Show dialog
    JOptionPane.showMessageDialog(parent, mainPanel, "About $appName", JOptionPane.INFORMATION_MESSAGE)
}


// Function to open a webpage in the default browser
fun openWebpage(uri: URI) {
    try {
        Desktop.getDesktop().browse(uri)
    } catch (e: Exception) {
        JOptionPane.showMessageDialog(null, "Could not open website!", "Error", JOptionPane.ERROR_MESSAGE)
    }
}


fun JFrame.getAppInfo(): Pair<String, String> {
    val properties = Properties()
    val currentVersion = ADBHelper.getCurrentVersion()


    val appName = properties.getProperty("app.name", "ADBCard")
    val version = properties.getProperty("app.version", currentVersion)

    return Pair(appName, version)
}

