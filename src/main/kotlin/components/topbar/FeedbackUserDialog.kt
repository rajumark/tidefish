package components.topbar

import adb.ADBHelper.getCurrentVersion
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Cursor
import java.awt.Dimension
import java.awt.Font
import java.awt.Image
import java.awt.Insets
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.SwingConstants
import javax.swing.UIManager
import javax.swing.border.EmptyBorder

class FeedbackUserDialog : JDialog() {
    private val feedbackArea: JTextArea

    init {
        title = "Send Feedback"
        layout = BorderLayout()
        isModal = true
        isResizable = false
        preferredSize = Dimension(720, 520)

        // ==== TOP PANEL ====
        val topPanel = JPanel()
        topPanel.layout = BoxLayout(topPanel, BoxLayout.X_AXIS)
        topPanel.border = BorderFactory.createEmptyBorder(20, 20, 10, 20)
        topPanel.isOpaque = false
        topPanel.alignmentX = Component.CENTER_ALIGNMENT

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
        textPanel.alignmentY = Component.CENTER_ALIGNMENT

        val softwareName = JLabel("Tidefish")
        softwareName.font = UIManager.getFont("Label.font")?.deriveFont(Font.BOLD, 18f)

        val versionLabel = JLabel("v${getCurrentVersion()}")
        versionLabel.font = UIManager.getFont("Label.font")?.deriveFont(12f)

        textPanel.add(softwareName)
        textPanel.add(Box.createVerticalStrut(3))
        textPanel.add(versionLabel)

        topPanel.add(textPanel)
        add(topPanel, BorderLayout.NORTH)

        // ==== CENTER FEEDBACK PANEL ====
        val headerLabel = JLabel("We’d Love Your Feedback", SwingConstants.CENTER).apply {
            font = UIManager.getFont("Label.font")?.deriveFont(Font.BOLD, 24f)
            alignmentX = Component.CENTER_ALIGNMENT
        }

        val sublineLabel = JLabel("Tell us about your experience using Tidefish", SwingConstants.CENTER).apply {
            font = UIManager.getFont("Label.font")?.deriveFont(16f)
            alignmentX = Component.CENTER_ALIGNMENT
        }

        feedbackArea = JTextArea(8, 50).apply {
            font = UIManager.getFont("TextArea.font")?.deriveFont(14f)
            lineWrap = true
            wrapStyleWord = true
            margin = Insets(10, 10, 10, 10)
        }

        val scrollPane = JScrollPane(feedbackArea).apply {
            preferredSize = Dimension(640, 180)
        }

        val submitButton = JButton("Submit Feedback").apply {
            font = UIManager.getFont("Button.font")?.deriveFont(Font.BOLD, 14f)
            preferredSize = Dimension(200, 40)
            isFocusPainted = false
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)

            addActionListener {
                val feedbackText = feedbackArea.text.trim()
                if (feedbackText.isNullOrBlank().not()) {
                    feedbackArea.text = ""
                    JOptionPane.showMessageDialog(
                        this@FeedbackUserDialog,
                        "✅ Thanks for your feedback!\nWe’ll work on it and provide updates ASAP.",
                        "Feedback Submitted",
                        JOptionPane.INFORMATION_MESSAGE
                    )
                    dispose()
                } else {
                    JOptionPane.showMessageDialog(
                        this@FeedbackUserDialog,
                        "Please enter your feedback before submitting.",
                        "Empty Feedback",
                        JOptionPane.WARNING_MESSAGE
                    )
                }
            }
        }

        val centerPanel = JPanel()
        centerPanel.layout = BoxLayout(centerPanel, BoxLayout.Y_AXIS)
        centerPanel.isOpaque = false
        centerPanel.border = EmptyBorder(20, 20, 20, 20)

        centerPanel.add(headerLabel)
        centerPanel.add(Box.createVerticalStrut(16))
        centerPanel.add(sublineLabel)
        centerPanel.add(Box.createVerticalStrut(24))
        centerPanel.add(scrollPane)
        centerPanel.add(Box.createVerticalStrut(16))
        centerPanel.add(submitButton)

        add(centerPanel, BorderLayout.CENTER)

        pack()
        setLocationRelativeTo(null)
    }
}


