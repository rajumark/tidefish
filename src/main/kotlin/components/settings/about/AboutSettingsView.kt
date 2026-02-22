package components.settings.about

import java.awt.*
import javax.swing.*

class AboutSettingsView : JPanel() {
    
    init {
        layout = BorderLayout()
        background = Color.WHITE
        border = null
        
        val titleLabel = JLabel("About").apply {
            font = font.deriveFont(Font.BOLD, 18f)
            border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
        }
        
        val contentPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createEmptyBorder(0, 20, 20, 20)
            background = Color.WHITE
        }
        
        // App name and version
        val appNameLabel = JLabel("Tidefish").apply {
            font = font.deriveFont(Font.BOLD, 24f)
            alignmentX = Component.CENTER_ALIGNMENT
        }
        
        val versionLabel = JLabel("Version 1.0.0").apply {
            font = font.deriveFont(Font.PLAIN, 14f)
            foreground = Color.GRAY
            alignmentX = Component.CENTER_ALIGNMENT
        }
        
        // Description
        val descriptionLabel = JLabel("<html><div style='text-align: center; width: 300px;'>" +
            "Tidefish is a powerful Android Debug Bridge (ADB) management tool that provides " +
            "comprehensive device management capabilities for developers and Android enthusiasts.</div></html>").apply {
            alignmentX = Component.CENTER_ALIGNMENT
            border = BorderFactory.createEmptyBorder(20, 0, 20, 0)
        }
        
        // Copyright
        val copyrightLabel = JLabel("© 2024 Tidefish. All rights reserved.").apply {
            font = font.deriveFont(Font.PLAIN, 12f)
            foreground = Color.GRAY
            alignmentX = Component.CENTER_ALIGNMENT
        }
        
        // Links
        val linksPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            alignmentX = Component.CENTER_ALIGNMENT
        }
        
        val websiteButton = JButton("Visit Website").apply {
            foreground = Color.BLACK
            border = null
            preferredSize = Dimension(120, 35)
            alignmentX = Component.CENTER_ALIGNMENT
        }
        
        val githubButton = JButton("GitHub").apply {
            background = Color.BLACK
            foreground = Color.WHITE
            border = null
            preferredSize = Dimension(120, 35)
            alignmentX = Component.CENTER_ALIGNMENT
        }
        
        linksPanel.add(websiteButton)
        linksPanel.add(Box.createVerticalStrut(10))
        linksPanel.add(githubButton)
        
        contentPanel.add(appNameLabel)
        contentPanel.add(Box.createVerticalStrut(5))
        contentPanel.add(versionLabel)
        contentPanel.add(Box.createVerticalStrut(20))
        contentPanel.add(descriptionLabel)
        contentPanel.add(Box.createVerticalStrut(30))
        contentPanel.add(linksPanel)
        contentPanel.add(Box.createVerticalStrut(30))
        contentPanel.add(copyrightLabel)
        
        add(titleLabel, BorderLayout.NORTH)
        add(contentPanel, BorderLayout.CENTER)
    }
}
