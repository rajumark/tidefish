package screens.nodevice

import screens.wireless.WirelessConnectDialog
import java.awt.BorderLayout
import java.awt.Color
import java.awt.FlowLayout
import java.awt.Font
import javax.swing.*

class NoDeviceView : JPanel() {
    init {
        layout = BorderLayout()
        background = Color.WHITE

        // Title label
        val messageLabel = JLabel("No Device Connected", SwingConstants.CENTER).apply {
            font = font.deriveFont(Font.BOLD, 18f)
            foreground = Color(0x2B2B2B)
            border = BorderFactory.createEmptyBorder(20, 0, 10, 0)
        }

        // Instruction text area
        val instructionText = """
            To connect your USB device:
            
            • Plug the device into an available USB port.
            • Ensure the connector is oriented correctly.
            • Wait for your operating system to detect the device.
            • If it does not appear, try another port or check the cable.
            • Refer to your device manual for further assistance.
        """.trimIndent()

        val instructionArea = JTextArea(instructionText).apply {
            isEditable = false
            lineWrap = true
            wrapStyleWord = true
            font = font.deriveFont(Font.PLAIN, 14f)
            foreground = Color(0x555555)
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(10, 20, 20, 20)
        }

        val scrollPane = JScrollPane(instructionArea).apply {
            border = null
            background = Color.WHITE
            viewport.background = Color.WHITE
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        }

        // Buttons
        val retryButton = JButton("Retry USB Connection").apply {
            font = font.deriveFont(Font.PLAIN, 14f)
            background = Color(0xEAEAEA)
            isFocusPainted = false
            border = BorderFactory.createEmptyBorder(8, 16, 8, 16)
        }

        val wirelessButton = JButton("Connect via Wireless ADB").apply {
            font = font.deriveFont(Font.PLAIN, 14f)
            background = Color(0xEAEAEA)
            isFocusPainted = false
            border = BorderFactory.createEmptyBorder(8, 16, 8, 16)

            // 👉 Action to open WirelessConnectDialog
            addActionListener {
                val owner = SwingUtilities.getWindowAncestor(this@NoDeviceView)
                val dialog = WirelessConnectDialog(owner)
                dialog.setSize(400, 250) // set preferred size
                dialog.setLocationRelativeTo(owner) // center relative to parent
                dialog.isVisible = true
            }
        }

        val buttonPanel = JPanel(FlowLayout(FlowLayout.CENTER, 15, 10)).apply {
            background = Color.WHITE
            add(retryButton)
            add(wirelessButton)
            border = BorderFactory.createEmptyBorder(10, 0, 20, 0)
        }

        // Assemble layout
        add(messageLabel, BorderLayout.NORTH)
        add(scrollPane, BorderLayout.CENTER)
        add(buttonPanel, BorderLayout.SOUTH)
    }
}
