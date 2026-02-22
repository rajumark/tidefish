package components.topbar

import adb.ADBHelper.getCurrentVersion
import java.awt.*
import java.net.URI
import javax.swing.*
import javax.swing.border.EmptyBorder

class UpdateDialog(
    private val newVersion: String,
    private val downloadUrl: String?
) : JDialog() {

    init {
        title = "Update Available"
        layout = BorderLayout()
        isModal = true
        isResizable = false
        preferredSize = Dimension(420, 260)

        // ==== TOP PANEL ====
        val topPanel = JPanel()
        topPanel.layout = BoxLayout(topPanel, BoxLayout.X_AXIS)
        topPanel.border = BorderFactory.createEmptyBorder(20, 20, 10, 20)
        topPanel.isOpaque = false

        val imageUrl = javaClass.getResource("/ic_tidefish_logo.png")
        val originalIcon = if (imageUrl != null) ImageIcon(imageUrl) else null
        val scaledIcon = originalIcon?.image?.getScaledInstance(40, 40, Image.SCALE_SMOOTH)?.let {
            ImageIcon(it)
        }

        val imageLabel = JLabel(scaledIcon)
        imageLabel.border = BorderFactory.createEmptyBorder(0, 0, 0, 10)
        topPanel.add(imageLabel)

        val textPanel = JPanel()
        textPanel.layout = BoxLayout(textPanel, BoxLayout.Y_AXIS)
        textPanel.isOpaque = false

        val softwareName = JLabel("Tidefish Update").apply {
            font = UIManager.getFont("Label.font")?.deriveFont(Font.BOLD, 18f)
            alignmentX = Component.LEFT_ALIGNMENT
        }

        val currentVersionLabel = JLabel("Current: v${getCurrentVersion()}").apply {
            font = UIManager.getFont("Label.font")?.deriveFont(12f)
            alignmentX = Component.LEFT_ALIGNMENT
        }

        textPanel.add(softwareName)
        textPanel.add(Box.createVerticalStrut(3))
        textPanel.add(currentVersionLabel)

        topPanel.add(textPanel)
        add(topPanel, BorderLayout.NORTH)

        // ==== CENTER PANEL ====
        val centerPanel = JPanel()
        centerPanel.layout = BoxLayout(centerPanel, BoxLayout.Y_AXIS)
        centerPanel.isOpaque = false
        centerPanel.border = EmptyBorder(10, 20, 20, 20)

        // Divider line before content
        val divider = JSeparator()
        divider.maximumSize = Dimension(Int.MAX_VALUE, 1)
        centerPanel.add(divider)
        centerPanel.add(Box.createVerticalStrut(15))

        val headerLabel = JLabel("New Version Available!").apply {
            font = UIManager.getFont("Label.font")?.deriveFont(Font.BOLD, 20f)
            alignmentX = Component.LEFT_ALIGNMENT
            horizontalAlignment = SwingConstants.LEFT
        }

        val versionInfoLabel = JLabel("New Version: v$newVersion").apply {
            font = UIManager.getFont("Label.font")?.deriveFont(16f)
            alignmentX = Component.LEFT_ALIGNMENT
            horizontalAlignment = SwingConstants.LEFT
        }

        centerPanel.add(headerLabel)
        centerPanel.add(Box.createVerticalStrut(10))
        centerPanel.add(versionInfoLabel)

        add(centerPanel, BorderLayout.CENTER)

        // ==== BOTTOM PANEL (BUTTON) ====
        val buttonPanel = JPanel(BorderLayout())
        buttonPanel.border = EmptyBorder(10, 20, 20, 20)

        val downloadButton = JButton("Download Update").apply {
            font = UIManager.getFont("Button.font")?.deriveFont(Font.BOLD, 14f)
            preferredSize = Dimension(180, 36)
            isFocusPainted = false
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)

            addActionListener {
                openDownloadUrl()
                dispose()
            }
        }

        val buttonWrapper = JPanel()
        buttonWrapper.layout = BoxLayout(buttonWrapper, BoxLayout.X_AXIS)
        buttonWrapper.isOpaque = false
        buttonWrapper.add(Box.createHorizontalGlue())
        buttonWrapper.add(downloadButton)

        buttonPanel.add(buttonWrapper, BorderLayout.EAST)
        add(buttonPanel, BorderLayout.SOUTH)

        pack()
        setLocationRelativeTo(null)
    }

    private fun openDownloadUrl() {
        try {
            val url = if (downloadUrl.isNullOrBlank()) {
                "https://github.com/ADBCard/ADBCard-Releases/tree/main"
            } else {
                downloadUrl
            }
            Desktop.getDesktop().browse(URI(url))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
