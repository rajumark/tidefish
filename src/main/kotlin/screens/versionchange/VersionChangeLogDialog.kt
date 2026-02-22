package screens.versionchange

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import javax.swing.*

class VersionChangeLogDialog(parent: JFrame, currentVersion: String) :
    JDialog(parent, "What's New - $currentVersion", true) {

    private val versionListModel = DefaultListModel<String>()
    private val versionList = JList<String>(versionListModel)
    private val changeLogArea = JTextArea()

    init {
        layout = BorderLayout()
        size = Dimension(700, 500)
        setLocationRelativeTo(parent)
        defaultCloseOperation = DISPOSE_ON_CLOSE

        // Populate version list
        versionLogs.forEach { versionLog ->
            versionListModel.addElement(versionLog.version)
        }

        versionList.selectionMode = ListSelectionModel.SINGLE_SELECTION
        versionList.selectedIndex = 0  // Select the first version by default
        versionList.fixedCellHeight = 28
        versionList.font = Font("SansSerif", Font.PLAIN, 13)

        // Add listener to update changelog based on selection
        versionList.addListSelectionListener {
            val selectedIndex = versionList.selectedIndex
            if (selectedIndex >= 0) {
                val selectedVersionLog = versionLogs[selectedIndex]
                updateChangeLog(selectedVersionLog.logs)
            }
        }

        val listScrollPane = JScrollPane(versionList)

        // Text area for changelog
        changeLogArea.isEditable = false
        changeLogArea.lineWrap = true
        changeLogArea.wrapStyleWord = true
        changeLogArea.font = Font("Monospaced", Font.PLAIN, 13)

        val contentScrollPane = JScrollPane(changeLogArea)

        // Split pane layout: left version list, right changelog
        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, contentScrollPane).apply {
            dividerLocation = 100
            resizeWeight = 0.15
        }

        add(splitPane, BorderLayout.CENTER)

        // Close button
        val closeButton = JButton("Close").apply {
            preferredSize = Dimension(100, 28)
            addActionListener { dispose() }
        }

        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT)).apply {
            add(closeButton)
        }

        add(buttonPanel, BorderLayout.SOUTH)

        // Initial changelog content based on the first version in the list
        if (versionLogs.isNotEmpty()) {
            val firstVersionLog = versionLogs.first()
            updateChangeLog(firstVersionLog.logs)  // Load the first version's changelog
        }
    }

    private fun updateChangeLog(points: List<String>) {
        val sb = StringBuilder()
        for (point in points) {
            sb.append("• ").append(point).append("\n")
        }
        changeLogArea.text = sb.toString()
        changeLogArea.caretPosition = 0
    }
}
