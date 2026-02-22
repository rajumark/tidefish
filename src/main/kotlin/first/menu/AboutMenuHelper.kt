package first.menu



import adb.ADBHelper.getCurrentVersion
import components.about.showAboutDialog
import first.FirstPageHelper.restartSwingApp
import screens.versionchange.VersionChangeLogDialog
import javax.swing.JFrame
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JOptionPane

fun getAboutMenuHelper(jFrame: JFrame): JMenu {
    val helpMenu = JMenu("Help")
    val aboutItem = JMenuItem("About").apply {
        addActionListener {
            showAboutDialog(jFrame)
        }
    }

    val changeLogItem = JMenuItem("ChangeLogs").apply {
        addActionListener {
            val currentVersion = getCurrentVersion()
            VersionChangeLogDialog(jFrame, currentVersion).apply {
                isVisible = true
            }
        }
    }
    val logout = JMenuItem("Logout").apply {
        addActionListener {
            val result = JOptionPane.showConfirmDialog(
                null, // parent component
                "Are you sure you want to logout?\nYou will not be able to access any features after logging out.\n\n", // message
                "Confirm Logout", // title
                JOptionPane.YES_NO_OPTION // options
            )

            if (result == JOptionPane.YES_OPTION) {
                restartSwingApp(jFrame)
            }
        }
    }
    helpMenu.add(changeLogItem)
    helpMenu.add(aboutItem)
    helpMenu.add(logout)
    return helpMenu
}