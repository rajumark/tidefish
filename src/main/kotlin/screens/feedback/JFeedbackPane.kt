package screens.feedback

import adb.ADBHelper.getCurrentVersion
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder

class JFeedbackPane : JPanel() {
    private val feedbackArea: JTextArea
    private val submitButton: JButton

    init {
        layout = BorderLayout()
        isOpaque = true
        border = EmptyBorder(40, 80, 40, 80)

        // ==== TOP PANEL ====
        val topPanel = JPanel()
        topPanel.layout = BoxLayout(topPanel, BoxLayout.X_AXIS)
        topPanel.border = BorderFactory.createEmptyBorder(20, 20, 10, 20)
        topPanel.isOpaque = false
        topPanel.alignmentX = Component.CENTER_ALIGNMENT

        val imageUrl = javaClass.getResource("/ic_adbcard_logo.png")
        val originalIcon = if (imageUrl != null) ImageIcon(imageUrl) else null
        val scaledIcon = originalIcon?.image?.getScaledInstance(56, 56, Image.SCALE_SMOOTH)?.let {
            ImageIcon(it)
        }

        val imageLabel = JLabel(scaledIcon)
        imageLabel.border = BorderFactory.createEmptyBorder(0, 0, 0, 10)
        topPanel.add(imageLabel)

        val textPanel = JPanel()
        textPanel.layout = BoxLayout(textPanel, BoxLayout.Y_AXIS)
        textPanel.isOpaque = false
        textPanel.alignmentY = Component.CENTER_ALIGNMENT

        val softwareName = JLabel("ADBCard")
        softwareName.font = UIManager.getFont("Label.font")?.deriveFont(Font.BOLD, 22f)

        val versionLabel = JLabel("v${getCurrentVersion()}")
        versionLabel.font = UIManager.getFont("Label.font")?.deriveFont(12f)

        textPanel.add(softwareName)
        textPanel.add(Box.createVerticalStrut(4))
        textPanel.add(versionLabel)

        topPanel.add(textPanel)
        add(topPanel, BorderLayout.NORTH)

        // ==== CENTER FEEDBACK PANEL ====
        val headerLabel = JLabel("We’d Love Your Feedback", SwingConstants.CENTER).apply {
            font = UIManager.getFont("Label.font")?.deriveFont(Font.BOLD, 28f)
            alignmentX = Component.CENTER_ALIGNMENT
        }

        val sublineLabel = JLabel("Tell us about your experience using ADBCard", SwingConstants.CENTER).apply {
            font = UIManager.getFont("Label.font")?.deriveFont(18f)
            alignmentX = Component.CENTER_ALIGNMENT
        }

        feedbackArea = JTextArea(10, 50).apply {
            font = UIManager.getFont("TextArea.font")?.deriveFont(16f)
            lineWrap = true
            wrapStyleWord = true
            margin = Insets(10, 10, 10, 10)
        }

        val scrollPane = JScrollPane(feedbackArea).apply {
            preferredSize = Dimension(800, 200)
        }

        submitButton = JButton("Submit Feedback").apply {
            font = UIManager.getFont("Button.font")?.deriveFont(Font.BOLD, 16f)
            preferredSize = Dimension(200, 45)
            isFocusPainted = false
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)

            addActionListener {
                val feedbackText = feedbackArea.text.trim()
                if (feedbackText.isNullOrBlank().not()) {
                    feedbackArea.text = ""
                    JOptionPane.showMessageDialog(
                        this@JFeedbackPane,
                        "✅ Thanks for your feedback!\nWe’ll work on it and provide updates ASAP.",
                        "Feedback Submitted",
                        JOptionPane.INFORMATION_MESSAGE
                    )
                } else {
                    JOptionPane.showMessageDialog(
                        this@JFeedbackPane,
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
        centerPanel.border = EmptyBorder(40, 0, 0, 0)

        centerPanel.add(headerLabel)
        centerPanel.add(Box.createVerticalStrut(20))
        centerPanel.add(sublineLabel)
        centerPanel.add(Box.createVerticalStrut(40))
        centerPanel.add(scrollPane)
        centerPanel.add(Box.createVerticalStrut(30))
        centerPanel.add(submitButton)

        add(centerPanel, BorderLayout.CENTER)
    }


}
