package screens.apps.left

import adb.DeviceModel
import decoreui.onTextChanged
import screens.packages.AppType
import screens.packages.TextDatabase
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPopupMenu
import javax.swing.JMenuItem
import screens.packages.PackagesListModel
import java.beans.PropertyChangeListener
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.SwingUtilities
import adb.ADBHelper
import screens.packages.ADBApps
import screens.packages.SAppAction
import screens.packages.confirmClearData
import screens.packages.confirmUnInstall
import screens.packages.slistAppActions

import screens.packages.packagedetails.PackageFullDetailsPage.Companion.showPackageDetails

class AppsListLeftController(private val model: AppsListLeftModel, private val view: AppsListLeftView) {

    private var selectedDevice: DeviceModel? = null
    private val refreshInterval = 2000
    private var timer: javax.swing.Timer? = null
    fun setDeviceModel(selectedDeviceNew: DeviceModel?) {
        if (selectedDevice != selectedDeviceNew) {
            selectedDevice = selectedDeviceNew
            fetchData()
        }
    }

    private fun fetchData() {
        if (selectedDevice == null) return
        if (isInProcessing) return
        fetchAppsList()
        refreshCurrentAppPackage()
    }

    val eventListener = PropertyChangeListener { event ->
        when (event.propertyName) {
            PackagesListModel.PROP_apps_list -> {
                //  println("PROP_apps_list to: ${event.newValue}")
                updateAppsListUI()
            }

            PackagesListModel.PROP_current_activity_name -> {
                // println("PROP_current_activity_name to: ${event.newValue}")
                updateAppsListUI()
            }

            PackagesListModel.PROP_search_text_query -> {
                //  println("PROP_search_text_query to: ${event.newValue}")
                updateAppsListUI()
            }

            PackagesListModel.PROP_appType -> {
                // println("PROP_search_text_query to: ${event.newValue}")
                view.setFilterBadge(model.appType != AppType.ALL_APPS)
                fetchData()
            }

            else -> {}
        }
    }

    private fun setModelChangeListener() {
        model.addPropertyChangeListener(eventListener)
    }

    private fun updateAppsListUI() {
        val list = getFilteredAppsList()
        view.submitAppsList(list)
        view.setPinnedPackages(TextDatabase.getPinList().toSet())
    }

    init {
        setModelChangeListener()
        setUpUIListener()
        attachFilterPopup()
        startFetchingData()
        view.setFilterBadge(model.appType != AppType.ALL_APPS)
        setupRightClickPopup()
    }

    private fun setUpUIListener() {
        view.searchField.onTextChanged {
            model.setSearchTextQuery(view.searchField.text)
        }
        view.onPinToggle = { packageName, shouldPin ->
            if (shouldPin) TextDatabase.pinPackageName(packageName) else TextDatabase.unPinPackage(packageName)
            updateAppsListUI()
        }
    }

    private fun setupRightClickPopup() {
        val list = view.packageList
        list.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent) {
                if (e.isPopupTrigger || SwingUtilities.isRightMouseButton(e)) {
                    val index = list.locationToIndex(e.point)
                    if (index >= 0) {
                        list.selectedIndex = index
                        val pName = list.model.getElementAt(index)
                        val popup = buildPopupMenu(pName)
                        view.showContextMenu(popup, e.x, e.y)
                    }
                }
            }
        })
    }

    private fun buildPopupMenu(packageName: String): JPopupMenu {
        val popup = JPopupMenu()
        slistAppActions.forEach { action ->
            val item = JMenuItem(action.title)
            item.addActionListener {
                val device = selectedDevice ?: return@addActionListener
                runAppAction(action, packageName, device)
            }
            popup.add(item)
            if (action == SAppAction.copy) {
                popup.addSeparator()
            }
        }
        return popup
    }

    private fun runAppAction(modelAction: SAppAction, pName: String, selectedDevice: DeviceModel) {
        Thread {
            when (modelAction) {
                SAppAction.start -> ADBApps.startApp(pName, selectedDevice.id)
                SAppAction.force_stop -> ADBApps.forceStopApp(pName, selectedDevice.id)
                SAppAction.restart -> ADBApps.restartApp(pName, selectedDevice.id)
                SAppAction.uninstall -> {
                    confirmUnInstall(pName) {
                        ADBApps.uninstallApp(pName, selectedDevice.id)
                        fetchData()
                    }
                }
                SAppAction.clearData -> {
                    confirmClearData(pName) {
                        ADBApps.clearAppData(pName, selectedDevice.id)
                    }
                }
                SAppAction.enable -> {
                    ADBApps.enableDisableApp(pName, selectedDevice.id, makeEnable = true)
                    fetchData()
                }
                SAppAction.disable -> {
                    ADBApps.enableDisableApp(pName, selectedDevice.id, makeEnable = false)
                    fetchData()
                }
                SAppAction.home -> ADBApps.home(pName, selectedDevice.id)
                SAppAction.app_info -> ADBApps.openAppSettings(pName, selectedDevice.id)
                SAppAction.play_store -> ADBApps.openUrlInAndroidBrowser(ADBApps.buildPlayStoreUrl(pName), selectedDevice.id)
                SAppAction.view_in_desktop -> utils.JopenLink(ADBApps.buildPlayStoreUrl(pName))
                SAppAction.find_online -> utils.JopenLink(ADBApps.buildFindInMarketUrl(pName))
                SAppAction.copy -> ADBHelper.copyTextToClipboard(pName)
                SAppAction.download -> ADBApps.downloadAPK(pName, selectedDevice.id)
                SAppAction.showMore -> {
                    val frame = SwingUtilities.getWindowAncestor(view)
                    if (frame is java.awt.Frame) {
                        val jframe = frame as? javax.swing.JFrame
                        jframe?.let { showPackageDetails(selectedDevice, it, "$pName") }
                    }
                }
                else -> {}
            }
        }.start()
    }

    private fun attachFilterPopup() {
        view.onFilterClick = {
            showFilterMenu()
        }
    }

    private fun showFilterMenu() {
        val menu = JPopupMenu()
        val current = model.appType
        AppType.options().forEach { type ->
            val item = JMenuItem(type.displayName + if (type == current) "  ✓" else "")
            item.addActionListener {
                if (type != model.appType) {
                    model.setAppType(type)
                }
            }
            menu.add(item)
        }

        val location = view.filterButton.locationOnScreen
        val x = 0
        val y = view.filterButton.height
        menu.show(view.filterButton, x, y)
    }

    private var isInProcessing = false
    private fun fetchAppsList() {
        isInProcessing = true
        object : javax.swing.SwingWorker<List<String>, Void>() {
            override fun doInBackground(): List<String> {
                return screens.packages.SADBApps.getApps(selectedDevice!!.id, model.appType)
            }

            override fun done() {
                try {
                    val result = get()
                    model.setAppsList(result.toMutableList())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                isInProcessing = false
            }
        }.execute()
    }

    private fun refreshCurrentAppPackage() {
        object : javax.swing.SwingWorker<String?, Void>() {
            override fun doInBackground(): String? {
                return try {
                    screens.inspector.ADBInspect.getCurrentActivityName(selectedDevice!!.id)?.split("/")?.firstOrNull()
                } catch (e: Exception) {
                    null
                }
            }

            override fun done() {
                try {
                    val result = get()
                    model.setCurrentActivityName(result)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.execute()
    }

    private fun getFilteredAppsList(): List<String> {
        val pinSet = TextDatabase.getPinList().toSet()
        val baseList = if (model.searchTextQuery.isNullOrBlank()) model.apps_list else model.apps_list.filter {
            it.lowercase().contains(model.searchTextQuery.orEmpty().lowercase())
        }
        return baseList
            .sortedWith(compareByDescending<String> { pinSet.contains(it) }
                .thenByDescending { it == model.currentActivityName })
    }

    private fun startFetchingData() {
        if (timer == null || !(timer?.isRunning ?: false)) {
            timer = javax.swing.Timer(refreshInterval) {
                fetchData()
            }
            timer?.start()
        }
    }

    private fun stopFetchingData() {
        timer?.stop()
        timer = null
    }


}


