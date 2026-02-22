package screens.apps.right.paths

import colors.LightColorsConst
import components.getIconJLabel
import decoreui.setHint
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dialog
import javax.swing.*
import javax.swing.border.MatteBorder

class AppsDetailsRightPathsView : JPanel() {

    val searchField = JTextField().apply {
        columns = 15
        setHint("Search")
        border = null
    }

    var onRefreshClick: (() -> Unit)? = null
    var onDownloadAllClick: (() -> Unit)? = null

    private val searchButton = this.getIconJLabel(icon = "ic_search.svg", onClick = {})
    private val refreshButton = this.getIconJLabel(icon = "refresh.svg", onClick = {
        onRefreshClick?.invoke()
    })

    private val listModel = DefaultListModel<String>()
    private val listView = JList(listModel).apply {
        selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        background = Color.WHITE
        fixedCellHeight = 22
    }

    private val downloadAllBtn = JButton("Download All").apply {
        addActionListener { onDownloadAllClick?.invoke() }
    }

    private var loadingDialog: JDialog? = null

    init {
        layout = BorderLayout()
        border = null

        val topPanel = JPanel(BorderLayout()).apply {
            border = MatteBorder(0, 0, 1, 0, LightColorsConst.color_divider)
            val leftPanel = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                add(searchButton)
                add(Box.createHorizontalStrut(6))
                add(searchField)
            }
            val rightPanel = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                add(downloadAllBtn)
                add(Box.createHorizontalStrut(6))
                add(refreshButton)
            }
            add(leftPanel, BorderLayout.WEST)
            add(Box.createHorizontalStrut(8), BorderLayout.CENTER)
            add(rightPanel, BorderLayout.EAST)
        }
        add(topPanel, BorderLayout.NORTH)

        val scrollPane = JScrollPane(listView).apply {
            viewport.border = null
            background=Color.WHITE
            setBorder(BorderFactory.createEmptyBorder())
        }
        add(scrollPane, BorderLayout.CENTER)

        setSearchCountHint(0)
    }

    fun setSearchCountHint(count: Int) {
        SwingUtilities.invokeLater {
            searchField.setHint("Search in ${count} lines")
        }
    }

    fun submitLines(lines: List<String>) {
        SwingUtilities.invokeLater {
            listModel.removeAllElements()
            lines.forEach { listModel.addElement(it) }
            setSearchCountHint(lines.size)
        }
    }

    fun showLoading(message: String) {
        SwingUtilities.invokeLater {
            try { loadingDialog?.dispose() } catch (_: Exception) {}
            downloadAllBtn.isEnabled = false
            val wnd = SwingUtilities.getWindowAncestor(this)
            val dialog = JDialog(wnd, "Please wait", Dialog.ModalityType.MODELESS)
            val bar = JProgressBar().apply { isIndeterminate = true }
            val label = JLabel(message)
            val panel = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                border = BorderFactory.createEmptyBorder(16,16,16,16)
                add(label)
                add(Box.createVerticalStrut(8))
                add(bar)
            }
            dialog.contentPane.add(panel)
            dialog.pack()
            dialog.setLocationRelativeTo(this)
            dialog.isResizable = false
            dialog.isAlwaysOnTop = true
            dialog.defaultCloseOperation = JDialog.DO_NOTHING_ON_CLOSE
            loadingDialog = dialog
            dialog.isVisible = true
        }
    }

    fun hideLoading() {
        SwingUtilities.invokeLater {
            try { loadingDialog?.dispose() } catch (_: Exception) {}
            loadingDialog = null
            downloadAllBtn.isEnabled = true
        }
    }
}


