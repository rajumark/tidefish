package screens.apps.right.activities

import colors.LightColorsConst
import components.getIconJLabel
import decoreui.applyStyleSplitPan
import decoreui.setHint
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rtextarea.RTextScrollPane
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.*
import javax.swing.border.MatteBorder
import javax.swing.text.BadLocationException
import javax.swing.text.Highlighter

class AppsDetailsRightActivitiesView : JPanel() {
    val searchField = JTextField().apply {
        columns = 15
        setHint("Search")
        border = null
    }

    var onRefreshClick: (() -> Unit)? = null

    private val searchButton = this.getIconJLabel(icon = "ic_search.svg", onClick = {})
    private val refreshButton = this.getIconJLabel(icon = "refresh.svg", onClick = {
        onRefreshClick?.invoke()
    })

    private val listModel = DefaultListModel<String>()
    private val sectionsList = JList(listModel).apply {
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        border = null
    }

    // 🔹 Replace JTextArea with RSyntaxTextArea
    private val textArea = RSyntaxTextArea().apply {
        syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_YAML // closest to your resolver table format
        isCodeFoldingEnabled = true
        isEditable = false
        border = null
        background = Color.WHITE
    }

    private val textScrollPane = RTextScrollPane(textArea).apply {
        border = null
        viewportBorder = null
    }

    private val listScrollPane = JScrollPane(sectionsList).apply {
        border = null
        viewportBorder = null
    }

    // Inline search controls for the right text area
    private val textSearchField = JTextField().apply {
        columns = 12
        setHint("Find in text")
        border = null
        toolTipText = "Search within details"
    }

    private val textSearchPrevButton = this.getIconJLabel(icon = "ic_arrow_up.svg", onClick = { navigateMatch(previous = true) })
    private val textSearchNextButton = this.getIconJLabel(icon = "ic_arrow_down.svg", onClick = { navigateMatch(previous = false) })
    private val textSearchClearButton = this.getIconJLabel(icon = "ic_close.svg", onClick = {
        textSearchField.text = ""
        clearTextHighlights()
    })

    private val rightTopSearchPanel = JPanel(BorderLayout()).apply {
        border = MatteBorder(0, 0, 1, 0, LightColorsConst.color_divider)
        add(textSearchField, BorderLayout.CENTER)
        val btnPanel = JPanel().apply {
            add(textSearchClearButton)
            add(textSearchPrevButton)
            add(textSearchNextButton)
        }
        add(btnPanel, BorderLayout.EAST)
    }

    private val rightPanel = JPanel(BorderLayout()).apply {
        add(rightTopSearchPanel, BorderLayout.NORTH)
        add(textScrollPane, BorderLayout.CENTER)
    }

    private val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, rightPanel).apply {
        resizeWeight = 0.25
        isOneTouchExpandable = true
        dividerSize = 6
        applyStyleSplitPan()
        border = null
    }

    init {
        layout = BorderLayout()

        val topPanel = JPanel(BorderLayout()).apply {
            border = MatteBorder(0, 0, 1, 0, LightColorsConst.color_divider)
            add(searchButton, BorderLayout.WEST)
            add(searchField, BorderLayout.CENTER)
            add(refreshButton, BorderLayout.EAST)
        }
        // add(topPanel, BorderLayout.NORTH)

        add(splitPane, BorderLayout.CENTER)

        installTextSearch()
    }

    fun setTabs(keys: List<String>) {
        SwingUtilities.invokeLater {
            listModel.removeAllElements()
            keys.forEach { listModel.addElement(it) }
            if (listModel.size() > 0) {
                sectionsList.selectedIndex = 0
            }
            revalidate(); repaint()
        }
    }

    fun setOnTabChange(listener: (index: Int, title: String) -> Unit) {
        sectionsList.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                val idx = sectionsList.selectedIndex
                if (idx >= 0) {
                    val title = sectionsList.model.getElementAt(idx)
                    listener(idx, title)
                }
            }
        }
    }

    fun setText(content: String) {
        SwingUtilities.invokeLater {
            textArea.text = content
            textArea.caretPosition = 0
            clearTextHighlights()
        }
    }

    // --- Text search & highlight helpers ---
    private var currentMatchIndex: Int = -1
    private val matchOffsets: MutableList<IntRange> = mutableListOf()

    private fun installTextSearch() {
        fun updateSearch() {
            val query = textSearchField.text.orEmpty()
            highlightMatches(query)
        }

        textSearchField.addActionListener { updateSearch() }
        textSearchField.document.addDocumentListener(object : javax.swing.event.DocumentListener {
            override fun insertUpdate(e: javax.swing.event.DocumentEvent?) = updateSearch()
            override fun removeUpdate(e: javax.swing.event.DocumentEvent?) = updateSearch()
            override fun changedUpdate(e: javax.swing.event.DocumentEvent?) = updateSearch()
        })
    }

    private fun highlightMatches(query: String) {
        clearTextHighlights()
        if (query.isBlank()) return

        val content = textArea.text
        val lowerContent = content.lowercase()
        val lowerQuery = query.lowercase()

        var fromIndex = 0
        while (true) {
            val start = lowerContent.indexOf(lowerQuery, fromIndex)
            if (start == -1) break
            val end = start + query.length
            matchOffsets.add(start until end)
            try {
                textArea.highlighter.addHighlight(
                    start,
                    end,
                    javax.swing.text.DefaultHighlighter.DefaultHighlightPainter(Color(255, 236, 153))
                )
            } catch (_: BadLocationException) { }
            fromIndex = end
        }
        currentMatchIndex = if (matchOffsets.isNotEmpty()) 0 else -1
        scrollToCurrentMatch()
    }

    private fun clearTextHighlights() {
        val highlighter: Highlighter = textArea.highlighter
        highlighter.removeAllHighlights()
        matchOffsets.clear()
        currentMatchIndex = -1
    }

    private fun navigateMatch(previous: Boolean) {
        if (matchOffsets.isEmpty()) return
        currentMatchIndex = if (currentMatchIndex == -1) 0 else currentMatchIndex
        currentMatchIndex = if (previous) {
            if (currentMatchIndex - 1 < 0) matchOffsets.lastIndex else currentMatchIndex - 1
        } else {
            if (currentMatchIndex + 1 > matchOffsets.lastIndex) 0 else currentMatchIndex + 1
        }
        scrollToCurrentMatch()
    }

    private fun scrollToCurrentMatch() {
        if (currentMatchIndex !in matchOffsets.indices) return
        val range = matchOffsets[currentMatchIndex]
        try {
            textArea.caretPosition = range.first
            textArea.scrollRectToVisible(textArea.modelToView2D(range.first).bounds)
        } catch (_: BadLocationException) { }
    }
}
