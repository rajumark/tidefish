package components.settings.usage

import colors.LightColorsConst
import java.awt.*
import javax.swing.*
import javax.swing.table.DefaultTableModel

class UsageSettingsView : JPanel() {
    
    init {
        layout = BorderLayout()
        background = Color.WHITE
        border = null
        
        val titleLabel = JLabel("Usage Statistics").apply {
            font = font.deriveFont(Font.BOLD, 18f)
            border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
        }
        
        val contentPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createEmptyBorder(0, 20, 20, 20)
            background = Color.WHITE
        }
        
        // Usage stats table
        val tableModel = object : DefaultTableModel(arrayOf("Metric", "Value"), 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean = false
        }
        
        // Add sample data
        tableModel.addRow(arrayOf("Total Devices Connected", "12"))
        tableModel.addRow(arrayOf("Commands Executed", "1,247"))
        tableModel.addRow(arrayOf("Files Transferred", "89"))
        tableModel.addRow(arrayOf("App Installations", "156"))
        tableModel.addRow(arrayOf("Screen Captures", "23"))
        tableModel.addRow(arrayOf("Logs Captured", "45"))
        
        val usageTable = JTable(tableModel).apply {
            tableHeader.reorderingAllowed = false
            setShowGrid(false)
            fillsViewportHeight = true
            setBorder(BorderFactory.createEmptyBorder())
            rowHeight = 30
        }
        
        val tableScrollPane = JScrollPane(usageTable).apply {
            preferredSize = Dimension(400, 200)
            border = BorderFactory.createLineBorder(LightColorsConst.color_divider)
        }
        
        // Chart panel placeholder
        val chartPanel = JPanel().apply {
            background = Color.LIGHT_GRAY
            preferredSize = Dimension(400, 150)
            border = BorderFactory.createTitledBorder("Usage Chart")
            layout = BorderLayout()
            
            val chartLabel = JLabel("Chart visualization will be displayed here").apply {
                horizontalAlignment = SwingConstants.CENTER
                foreground = Color.DARK_GRAY
            }
            add(chartLabel, BorderLayout.CENTER)
        }
        
        // Export button
        val exportButton = JButton("Export Statistics").apply {

            border = null
            preferredSize = Dimension(140, 35)
        }
        
        val buttonPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            background = Color.WHITE
            add(exportButton)
        }
        
        contentPanel.add(tableScrollPane)
        contentPanel.add(Box.createVerticalStrut(20))
        contentPanel.add(chartPanel)
        contentPanel.add(Box.createVerticalStrut(20))
        contentPanel.add(buttonPanel)
        
        add(titleLabel, BorderLayout.NORTH)
        add(contentPanel, BorderLayout.CENTER)
    }
}
