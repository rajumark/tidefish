package components.settings

import colors.LightColorsConst
import components.settings.about.AboutSettingsView
import components.settings.account.AccountSettingsView
import components.settings.theme.ThemeSettingsView
import components.settings.usage.UsageSettingsView
import decoreui.applyRoundedSelection
import decoreui.applyStyleSplitPan
import decoreui.setHint
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import javax.swing.*
import javax.swing.border.MatteBorder
import javax.swing.table.DefaultTableModel

class SoftwareSettingsDialog : JDialog() {
    
    // Search components copied  from AppsDetailsRightBasicInfoView (without refresh button)
    val searchField = JTextField().apply {
        columns = 15
        setHint("Search")
        border = null
    }
    
    private val searchButton = JButton("Search").apply {
        border = null

    }
    
    private val tableModel = object : DefaultTableModel(arrayOf("Key", "Value", "Description"), 0) {
        override fun isCellEditable(row: Int, column: Int): Boolean = false
    }
    
    val table = JTable(tableModel).apply {
        tableHeader.reorderingAllowed = false
        setShowGrid(false)
        fillsViewportHeight = true
        setBorder(BorderFactory.createEmptyBorder())
    }
    
    // Menu items
    val menuItems = arrayOf("Account", "About")
    val menuList = JList(menuItems).apply {
        border = null
        putClientProperty("JList.isFileList", true)
        background = LightColorsConst.color_background_sidemenu
        applyRoundedSelection()
    }
    
    init {
        // Set dialog properties
        title = "Software Settings"
        isModal = true
        defaultCloseOperation = DISPOSE_ON_CLOSE
        
        // Set size and center on screen
        preferredSize = Dimension(800, 600)
        
        // Create main content panel with split view
        val mainPanel = JPanel(BorderLayout())
        mainPanel.background = LightColorsConst.color_background_sidemenu
        mainPanel.border = null
        
        // Create split pane
        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT)
        splitPane.dividerLocation = 100
        splitPane.dividerSize = 3
        splitPane.isOneTouchExpandable = true
        splitPane.applyStyleSplitPan()
        splitPane.border = null

        // Left panel with search and menu
        val leftPanel = JPanel(BorderLayout())
        leftPanel.background = LightColorsConst.color_background_sidemenu
        leftPanel.border = null
        
        // Search panel (hidden)
        val searchPanel = JPanel(BorderLayout()).apply {
            border = MatteBorder(0, 0, 1, 0, LightColorsConst.color_divider)
            add(searchButton, BorderLayout.WEST)
            add(searchField, BorderLayout.CENTER)
            isVisible = false
        }
        
        // Menu panel
        val menuPanel = JPanel(BorderLayout()).apply {
            border = BorderFactory.createEmptyBorder(8, 8, 8, 8)
            add(menuList, BorderLayout.CENTER)
            background = LightColorsConst.color_background_sidemenu
        }
        
        // leftPanel.add(searchPanel, BorderLayout.NORTH) // Hidden search panel
        leftPanel.add(menuPanel, BorderLayout.CENTER)
        
        // Right panel with different views
        val rightPanel = JPanel(BorderLayout())
        rightPanel.background = Color.WHITE
        rightPanel.border = null
        
        // Import the different views
        val accountView = AccountSettingsView(){
            dispose()
        }
        // val themeView = ThemeSettingsView() // Hidden
        val aboutView = AboutSettingsView()
        // val usageView = UsageSettingsView() // Hidden
        
        // Initially show account view
        rightPanel.add(accountView, BorderLayout.CENTER)
        
        // Add menu selection listener
        menuList.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                val selectedIndex = menuList.selectedIndex
                if (selectedIndex >= 0) {
                    rightPanel.removeAll()
                    val selectedView = when (selectedIndex) {
                        0 -> accountView
                        1 -> aboutView
                        else -> accountView
                    }
                    rightPanel.add(selectedView, BorderLayout.CENTER)
                    rightPanel.revalidate()
                    rightPanel.repaint()
                }
            }
        }
        
        // Add panels to split pane
        splitPane.leftComponent = leftPanel
        splitPane.rightComponent = rightPanel
        
        // Add split pane to main panel
        mainPanel.add(splitPane, BorderLayout.CENTER)
        
        // Set initial menu selection
        menuList.selectedIndex = 0
        
        // Set content pane
        contentPane = mainPanel
        
        // Pack and validate first, then center
        pack()
        validate()
        setLocationRelativeTo(null)
    }
    

    
    fun setSearchCountHint(count: Int) {
        SwingUtilities.invokeLater {
            searchField.setHint("Search in ${count} keys")
        }
    }
    
    fun submitRows(rows: List<Triple<String, String, String>>) {
        SwingUtilities.invokeLater {
            tableModel.setRowCount(0)
            rows.forEach { (k, v, d) ->
                tableModel.addRow(arrayOf(k, v, d))
            }
            setSearchCountHint(tableModel.rowCount)
        }
    }
}
