package components.settings.theme

import java.awt.*
import javax.swing.*

class ThemeSettingsView : JPanel() {
    
    init {
        layout = BorderLayout()
        background = Color.WHITE
        border = null
        
        val titleLabel = JLabel("Theme Settings").apply {
            font = font.deriveFont(Font.BOLD, 18f)
            border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
        }
        
        val contentPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createEmptyBorder(0, 20, 20, 20)
            background = Color.WHITE
        }
        
        // Theme selection
        val themeLabel = JLabel("Select Theme:")
        val themeComboBox = JComboBox(arrayOf("Light", "Dark", "System Default"))
        
        val themePanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            background = Color.WHITE
            add(themeLabel)
            add(themeComboBox)
        }
        
        // Color scheme
        val colorLabel = JLabel("Color Scheme:")
        val colorComboBox = JComboBox(arrayOf("Blue", "Green", "Purple", "Orange"))
        
        val colorPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            background = Color.WHITE
            add(colorLabel)
            add(colorComboBox)
        }
        
        // Font size
        val fontSizeLabel = JLabel("Font Size:")
        val fontSizeSlider = JSlider(JSlider.HORIZONTAL, 10, 20, 14).apply {
            majorTickSpacing = 2
            paintTicks = true
            paintLabels = true
        }
        
        val fontSizePanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            background = Color.WHITE
            add(fontSizeLabel)
            add(fontSizeSlider)
        }
        
        // Apply button
        val applyButton = JButton("Apply Theme").apply {

            border = null
            preferredSize = Dimension(120, 35)
        }
        
        val buttonPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            background = Color.WHITE
            add(applyButton)
        }
        
        contentPanel.add(themePanel)
        contentPanel.add(Box.createVerticalStrut(15))
        contentPanel.add(colorPanel)
        contentPanel.add(Box.createVerticalStrut(15))
        contentPanel.add(fontSizePanel)
        contentPanel.add(Box.createVerticalStrut(20))
        contentPanel.add(buttonPanel)
        
        add(titleLabel, BorderLayout.NORTH)
        add(contentPanel, BorderLayout.CENTER)
    }
}
