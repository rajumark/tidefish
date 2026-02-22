package adb_terminal

import colors.LightColorsConst
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.border.MatteBorder

class ADBTerminalView : JPanel() {
    var onSendClick: (() -> Unit)? = null
    var onClearOutputClick: (() -> Unit)? = null
    var onHistorySelected: ((String) -> Unit)? = null
    var onToggleAutoscroll: ((Boolean) -> Unit)? = null
    var onSearchChanged: ((String) -> Unit)? = null
    var onSearchNext: (() -> Unit)? = null
    var onSearchPrev: (() -> Unit)? = null
    var onSaveToFile: (() -> Unit)? = null
    var onFilterChanged: ((String) -> Unit)? = null

    val deviceLabel by lazy { JLabel("Device: -") }

    val commandField by lazy {
        JTextField().apply {
            columns = 30
            preferredSize = Dimension(350, 28)
            this.putClientProperty("JTextField.placeholderText", "adb ...")
        }
    }

    val sendButton by lazy { JButton("Send") }
    val clearButton by lazy { JButton("Clear Output") }
    val historyCombo by lazy { JComboBox<String>() }
    val autoScrollCheck by lazy { JCheckBox("Auto-scroll", true) }

    val searchField by lazy { JTextField().apply { preferredSize = Dimension(180, 28) } }
    val searchNextButton by lazy { JButton("Next") }
    val searchPrevButton by lazy { JButton("Prev") }
    val filterField by lazy { JTextField().apply { preferredSize = Dimension(180, 28); toolTipText = "Filter lines (e.g., Error)" } }
    val saveButton by lazy { JButton("Save Log") }

    val outputArea by lazy {
        JTextArea().apply {
            font = Font(Font.MONOSPACED, Font.PLAIN, 13)
            lineWrap = false
            isEditable = false
        }
    }

    private val scrollPane by lazy { JScrollPane(outputArea) }

    val topPanel by lazy {
        JPanel(BorderLayout()).apply {
            border = MatteBorder(0, 0, 1, 0, LightColorsConst.color_divider)
            val left = JPanel().apply {
                add(commandField)
                add(sendButton)
                add(clearButton)
            }
            val right = JPanel().apply {
                add(JLabel("History:"))
                add(historyCombo)
                add(autoScrollCheck)
            }
            add(left, BorderLayout.WEST)
            add(right, BorderLayout.EAST)
        }
    }

    val searchPanel by lazy {
        JPanel().apply {
            add(JLabel("Search:"))
            add(searchField)
            add(searchPrevButton)
            add(searchNextButton)
            add(JLabel("Filter:"))
            add(filterField)
            add(saveButton)
        }
    }

    init {
        layout = BorderLayout()
        add(topPanel, BorderLayout.NORTH)
        add(scrollPane, BorderLayout.CENTER)
        add(searchPanel, BorderLayout.SOUTH)

        sendButton.addActionListener { onSendClick?.invoke() }
        clearButton.addActionListener { onClearOutputClick?.invoke() }
        historyCombo.addActionListener {
            (historyCombo.selectedItem as? String)?.let { onHistorySelected?.invoke(it) }
        }
        autoScrollCheck.addActionListener { onToggleAutoscroll?.invoke(autoScrollCheck.isSelected) }
        searchField.document.addDocumentListener(SimpleDocListener { onSearchChanged?.invoke(searchField.text) })
        searchNextButton.addActionListener { onSearchNext?.invoke() }
        searchPrevButton.addActionListener { onSearchPrev?.invoke() }
        saveButton.addActionListener { onSaveToFile?.invoke() }
        filterField.document.addDocumentListener(SimpleDocListener { onFilterChanged?.invoke(filterField.text) })

        // shortcuts
        commandField.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) onSendClick?.invoke()
                val metaOrCtrl = e.isMetaDown || e.isControlDown
                if (metaOrCtrl && e.keyCode == KeyEvent.VK_L) onClearOutputClick?.invoke()
                if (e.keyCode == KeyEvent.VK_UP) {
                    if (historyCombo.itemCount > 0) {
                        val idx = historyCombo.selectedIndex.takeIf { it >= 0 } ?: 0
                        historyCombo.selectedIndex = (idx - 1).coerceAtLeast(0)
                    }
                } else if (e.keyCode == KeyEvent.VK_DOWN) {
                    if (historyCombo.itemCount > 0) {
                        val idx = historyCombo.selectedIndex.takeIf { it >= 0 } ?: -1
                        historyCombo.selectedIndex = (idx + 1).coerceAtMost(historyCombo.itemCount - 1)
                    }
                }
            }
        })
    }

    fun setDeviceIdLabel(deviceId: String?) {
        deviceLabel.text = "Device: ${deviceId ?: "-"}"
    }

    fun setHistoryItems(items: List<String>) {
        historyCombo.model = DefaultComboBoxModel(items.toTypedArray())
    }

    fun setRunning(isRunning: Boolean) {
        sendButton.isEnabled = !isRunning
    }

    fun setAutoScroll(enabled: Boolean) {
        autoScrollCheck.isSelected = enabled
    }

    fun appendOutput(text: String, autoScroll: Boolean) {
        outputArea.append(text)
        if (autoScroll) {
            outputArea.caretPosition = outputArea.document.length
        }
    }

    fun clearOutput() {
        outputArea.text = ""
    }

    private class SimpleDocListener(val onChange: () -> Unit) : javax.swing.event.DocumentListener {
        override fun insertUpdate(e: javax.swing.event.DocumentEvent?) = onChange()
        override fun removeUpdate(e: javax.swing.event.DocumentEvent?) = onChange()
        override fun changedUpdate(e: javax.swing.event.DocumentEvent?) = onChange()
    }
}


