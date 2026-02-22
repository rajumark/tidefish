package screens.apps.right

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTabbedPane

class AppDetailsRightView : JPanel() {

    val titleLabel = JLabel("")
    private val tabbedPane = JTabbedPane().apply {
        background = Color(0, 0, 0, 0) // fully transparent
        isOpaque = false // make tabbedPane non-opaque

    }

    init {
        layout = BorderLayout()
        border = null

        val header = JPanel(BorderLayout()).apply {
            border = BorderFactory.createEmptyBorder(10, 12, 10, 12)
        }
        titleLabel.font = titleLabel.font.deriveFont(Font.BOLD, titleLabel.font.size2D + 2f)
        header.add(titleLabel, BorderLayout.CENTER)

        add(header, BorderLayout.NORTH)

        // Tabs
        add(tabbedPane, BorderLayout.CENTER)
    }

    fun setPackageName(name: String?) {
        titleLabel.text = name.orEmpty()
    }

    fun setupTabs(panels: List<Pair<String, JPanel>>) {
        tabbedPane.removeAll()
        panels.forEach { (title, panel) ->
            tabbedPane.addTab(title, panel)
        }
        revalidate()
        repaint()
    }
}