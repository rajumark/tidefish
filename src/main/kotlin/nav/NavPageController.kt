package nav

import adb.ZipHelper
import autoupdate.ADBDownload.showDownloadDialog
import first.navigation.TypeOfScreens
import first.navigation.sideMenuList
import screens.packages.KeyValueStore
import screens.versionchange.showWhatsNewDialogIfNotSeenAfterShown
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.beans.PropertyChangeListener
import java.io.File
import javax.swing.SwingWorker

class NavPageController (private val model: NavPageModel, private val view: NavPageView) {
    val eventListener = PropertyChangeListener { event ->
        when (event.propertyName) {
            "currentScreen" -> {
                //  println("Screen changed to: ${event.newValue}")
                view.setThisScreen(model.deviceModel, model.currentScreen)
            }

            "deviceModel" -> {
                //  println("Device model updated: ${event.newValue}")
                // Update the view accordingly
                view.setThisScreen(model.deviceModel, model.currentScreen)
            }
        }
    }

    init {

        //check required software is installed or not for adb
        if (!ZipHelper.getAdbPathZipEXisti()) {
            val fileName = "adb.zip"
            val fileDownload = File(ZipHelper.getUserFolderForCurrentOS(), fileName)
            showDownloadDialog(view, fileDownload);
            waitCheckAndCloseIfFailedDownload(view)
        }

        //set value change listener
        setModelChangeListener()
        view.topbarViewController.onItemChange= { device_model->
            model.setDeviceModel(device_model)
        }
        view.list.addListSelectionListener { event ->
            if (!event.valueIsAdjusting) {
                val selectedValue = view.list.selectedValue
                val selectedEnum = sideMenuList.firstOrNull { it.title == selectedValue }
                model.setCurrentScreen(selectedEnum ?: TypeOfScreens.apps)
                // persist selection by title (stable across sessions)
                KeyValueStore.put("last_selected_menu_title", selectedValue ?: TypeOfScreens.apps.title)
            }
        }

        // Apply initial selection to right side on startup
        runCatching {
            val selectedValue = view.list.selectedValue
            val selectedEnum = sideMenuList.firstOrNull { it.title == selectedValue }
            model.setCurrentScreen(selectedEnum ?: TypeOfScreens.apps)
        }


        view.showWhatsNewDialogIfNotSeenAfterShown()
    }

    private fun setModelChangeListener() {
        model.addPropertyChangeListener(eventListener)
        view.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                dispose() // Call controller cleanup
            }
        })
    }

    fun dispose() {
        model.removePropertyChangeListener(eventListener)
        view.dispose()
    }

    private fun waitCheckAndCloseIfFailedDownload(view: NavPageView) {
        object : SwingWorker<String, Void>() {
            override fun doInBackground(): String {
                Thread.sleep(1000)
                return ""
            }

            override fun done() {
                super.done()
                if (!ZipHelper.getAdbPathZipEXisti()) {
                    view.dispose()
                }
            }
        }.execute()
    }
}
