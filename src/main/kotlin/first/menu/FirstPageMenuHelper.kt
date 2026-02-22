package first.menu

import first.FirstPageHelper.restartSwingApp
import first.navigation.TypeOfScreens
import javax.swing.*


fun JFrame.setupFirstPageMenu(menuBar: JMenuBar,onNavigationClick:(TypeOfScreens)->Unit) {
    val fileMenu = JMenu("File")
    val openItem = JMenuItem("Open").apply {
        addActionListener {
            JOptionPane.showMessageDialog(this, "Open clicked!", "Info", JOptionPane.INFORMATION_MESSAGE)
        }
    }
    val restartItem = JMenuItem("Restart").apply {
        addActionListener {
           restartSwingApp(this@setupFirstPageMenu)
        }
    }
    val exitItem = JMenuItem("Exit").apply {
        addActionListener { System.exit(0) }
    }
    //fileMenu.add(openItem)
    fileMenu.add(restartItem)
    fileMenu.add(exitItem)

    menuBar.add(fileMenu)
    menuBar.add(getNavigationMenuHelper(onNavigationClick))
    menuBar.add(getThemeMenuObject())
    menuBar.add(getAboutMenuHelper(this))
    jMenuBar = menuBar
}