package first


import adb.ZipHelper
import autoupdate.ADBDownload.showDownloadDialog
import screens.versionchange.showWhatsNewDialogIfNotSeenAfterShown
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.beans.PropertyChangeListener
import java.io.File
import javax.swing.SwingWorker

class FirstPageController(private val model: FirstPageModel, private val view: FirstPageView) {
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
        view.topbarViewController.onItemChange = {
            model.setDeviceModel(it)
        }
        view.onNavigationClick = {
            model.setCurrentScreen(it)
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

    private fun waitCheckAndCloseIfFailedDownload(view: FirstPageView) {
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
