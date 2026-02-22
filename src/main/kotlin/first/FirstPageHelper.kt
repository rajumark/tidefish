package first

import adb.ZipHelper.refreshDeviceData
import nav.NavPageController
import nav.NavPageModel
import nav.NavPageView
import theme.FlatLafHelper
import java.awt.EventQueue
import javax.swing.JFrame
import javax.swing.SwingUtilities

object FirstPageHelper {
    fun loadFirstPage() {
        val model = NavPageModel()
        val view = NavPageView()
        NavPageController(model, view)
        view.isVisible = true


    }

    fun restartSwingApp(currentWindow: JFrame) {
        SwingUtilities.invokeLater {
            currentWindow.dispose()
            FlatLafHelper.initTheme()
            launchFirstPage()
        }
    }


}
fun launchFirstPage() {
    refreshDeviceData()
    EventQueue.invokeLater(FirstPageHelper::loadFirstPage)
    /*

    // Show splash screen first
    val splashScreen = SplashScreen { shouldLetUserMove ->
        if (shouldLetUserMove) {
            // Continue with the normal flow after splash screen
            refreshDeviceData()
            
//            // Validate subscription based on device fingerprint
//            if (!StartupValidator.validateSubscriptionOnStartup()) {
//                // Subscription validation failed - user is blocked
//                return@SplashScreen
//            }
            
            // Subscription is valid - load the utils.gumroad.main page
            EventQueue.invokeLater(FirstPageHelper::loadFirstPage)
        }
    }
    
    // Show the splash screen
    splashScreen.showSplash()*/
}