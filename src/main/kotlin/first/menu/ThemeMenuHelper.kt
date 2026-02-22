package first.menu

import com.formdev.flatlaf.themes.FlatMacDarkLaf
import com.formdev.flatlaf.themes.FlatMacLightLaf
import javax.swing.*



fun getThemeMenuObject(): JMenu {
    val themeMenu = JMenu("Theme")

    val lightItem = JCheckBoxMenuItem("Light")
    val darkItem = JCheckBoxMenuItem("Dark")

    // Sync the current theme selection
    if (isDarkTheme()) {
        darkItem.isSelected = true
    } else {
        lightItem.isSelected = true
    }

    lightItem.addActionListener {
        if (!lightItem.isSelected) return@addActionListener // Prevent unchecking

        UIManager.setLookAndFeel(FlatMacLightLaf())
        SwingUtilities.updateComponentTreeUI(JFrame.getFrames()[0])
        darkItem.isSelected = false  // Uncheck dark mode
    }

    darkItem.addActionListener {
        if (!darkItem.isSelected) return@addActionListener // Prevent unchecking

        UIManager.setLookAndFeel(FlatMacDarkLaf())
        SwingUtilities.updateComponentTreeUI(JFrame.getFrames()[0])
        lightItem.isSelected = false  // Uncheck light mode
    }

    themeMenu.add(lightItem)
    themeMenu.add(darkItem)

    return themeMenu
}

fun isDarkTheme(): Boolean {
    return UIManager.getBoolean("laf.dark")  // Checks if the theme is dark
}
