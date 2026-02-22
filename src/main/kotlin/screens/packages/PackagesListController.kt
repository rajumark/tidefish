package screens.packages


import adb.ADBHelper
import adb.DeviceModel
import screens.inspector.ADBInspect
import screens.packages.ADBApps.buildFindInMarketUrl
import screens.packages.ADBApps.buildPlayStoreUrl
import screens.packages.packagedetails.PackageFullDetailsPage.Companion.showPackageDetails
import screens.packages.packagedetails.getPackageDataInBackground
import screens.packages.packagedetails.getPackageMetadataModelFromCatch
import screens.packages.packagedetails.isAlreadyFetched
import utils.JopenLink
import java.awt.Point
import java.awt.event.ItemEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.beans.PropertyChangeListener
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class PackagesListController(val modelPackageData: PackagesListModel, val view: PackagesListView2) {
    private val refreshInterval = 2000 // 2 seconds
    private var timer: Timer? = null
    private var isInProcessing = false

    fun setDeviceModel(deviceModel: DeviceModel?) {
        if (modelPackageData.deviceModel != deviceModel) {
            modelPackageData.setDeviceModel(deviceModel)
            fetchData()
        }
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
                fetchData()
            }

            else -> {}
        }
    }

    private fun updateAppsListUI() {
        val showList = getFilterdAppsList()
        view.submitAppsList(showList)
    }

    private fun getFilterdAppsList(): List<PackageModel> {
        val showList =
            if (modelPackageData.searchTextQuery.isNullOrBlank()) {
                modelPackageData.apps_list
            } else {
                modelPackageData.apps_list.filter {
                    it.lowercase().contains(modelPackageData.searchTextQuery.orEmpty().lowercase())
                }
            }.sortedByDescending { it == modelPackageData.currentActivityName }
        return showList.map { packageName ->
            PackageModel(
                packageName = packageName,
                isCurrentApp = packageName == modelPackageData.currentActivityName,
                packageMetadata = getPackageMetadataModelFromCatch(modelPackageData.deviceModel?.id, packageName)
            )
        }
    }

    init {
        setModelChangeListener()
        setupViewListener()
    }

    private fun setupViewListener() {
        startFetchingData()
        view.addComponentListener(object : java.awt.event.ComponentAdapter() {

            override fun componentShown(e: java.awt.event.ComponentEvent?) {

                startFetchingData()
            }

            override fun componentHidden(e: java.awt.event.ComponentEvent?) {
                stopFetchingData()
            }
        })

        setupPopup(view.table)
        view.searchField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                modelPackageData.setSearchTextQuery(view.searchField.text)
            }

            override fun removeUpdate(e: DocumentEvent?) {
                modelPackageData.setSearchTextQuery(view.searchField.text)
            }

            override fun changedUpdate(e: DocumentEvent?) {
                modelPackageData.setSearchTextQuery(view.searchField.text)
            }
        })
        view.comboBoxAppType.addItemListener { event ->
            if (event.stateChange == ItemEvent.SELECTED) {
                val selectedItem = event.item as AppType
                modelPackageData.setAppType(selectedItem)
            }
        }

        setupVisibleRowListener(view.table)

    }
    fun getVisiblePackageNamesOneTime(table: JTable, packageNameColumnIndex: Int = 1): List<String> {
        val scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane::class.java, table) as? JScrollPane ?: return emptyList()
        val viewport = scrollPane.viewport
        val visibleRect = viewport.viewRect

        val firstRow = table.rowAtPoint(visibleRect.location)
        val lastRow = table.rowAtPoint(Point(visibleRect.x, visibleRect.y + visibleRect.height - 1))
        val adjustedLastRow = if (lastRow == -1) table.rowCount - 1 else lastRow

        if (firstRow == -1 || adjustedLastRow < firstRow) return emptyList()

        return (firstRow..adjustedLastRow).mapNotNull { row ->
            val value = table.getValueAt(row, packageNameColumnIndex)
            value as? String
        }
    }
    fun setupVisibleRowListener(table: JTable) {
        val scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane::class.java, table) as? JScrollPane ?: return
        val viewport = scrollPane.viewport
        val checkScrollability = {
            val viewHeight = viewport.extentSize.height
            val tableHeight = table.preferredSize.height

            val isVerticallyScrollable = tableHeight > viewHeight

            if (isVerticallyScrollable) {
               // println("YEs")
            } else {
                val listpack=getVisiblePackageNamesOneTime(table)
                listpack.forEach {
                    syncPackageMetaDataInfo(it)
                }
              //  println("No:"+listpack)
            }
        }




        // Add listener for when scroll position or size changes
        viewport.addChangeListener {
            checkScrollability()
        }
        viewport.addChangeListener {
            val rect = viewport.viewRect
            val firstRow = table.rowAtPoint(Point(0, rect.y))
            val lastRow = table.rowAtPoint(Point(0, rect.y + rect.height - 1))

            if (firstRow != -1 && lastRow != -1) {
                for (row in firstRow..lastRow) {
                    val modelRow = table.convertRowIndexToModel(row)
                    val model = table.model as PackageTableModel
                    val pkg = model.getPackageAt(modelRow)

                    syncPackageMetaDataInfo(pkg.packageName)
                }
            }
        }
    }

    private fun syncPackageMetaDataInfo(pkg: String) {
        if (modelPackageData.deviceModel != null) {
            //check here
            if (!isAlreadyFetched(
                    modelPackageData.deviceModel!!.id,
                    pkg
                )
            ) {
                getPackageDataInBackground(
                    modelPackageData.deviceModel!!.id,
                    pkg
                ) { packageDetails ->
                    updateAppsListUI()
                }
            }
        }
    }

    private fun setModelChangeListener() {
        modelPackageData.addPropertyChangeListener(eventListener)
    }


    private fun startFetchingData() {
        if (timer == null || !timer!!.isRunning) {
            timer = Timer(refreshInterval) {
                fetchData()
            }
            timer?.start()
        }
    }

    private fun stopFetchingData() {
        timer?.stop()
        timer = null
    }


    private fun fetchData() {
        if (modelPackageData.deviceModel == null) return
        if (isInProcessing) return
        fetchAppsList()
        refreshCurrentAppPackage()
    }

    private fun fetchAppsList() {
        isInProcessing = true
        object : SwingWorker<List<String>, Void>() {
            override fun doInBackground(): List<String> {
                val appsList: List<String> =
                    SADBApps.getApps(modelPackageData.deviceModel!!.id, modelPackageData.appType)
                return appsList // Return both values
            }

            override fun done() {
                try {
                    val result = get()

                    // Set values in the model
                    modelPackageData.setAppsList(result.toMutableList())
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                isInProcessing = false
            }
        }.execute()
    }

    private fun refreshCurrentAppPackage() {
        object : SwingWorker<String?, Void>() {
            override fun doInBackground(): String? {
                val activityName: String? = try {
                    ADBInspect.getCurrentActivityName(modelPackageData.deviceModel!!.id)?.split("/")?.firstOrNull()
                } catch (e: Exception) {
                    null
                }
                return activityName// Return both values
            }

            override fun done() {
                try {
                    val result = get()
                    modelPackageData.setCurrentActivityName(result)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.execute()
    }

    fun setupPopup(table: JTable) {
        val popupMenu = JPopupMenu()

        slistAppActions.forEach { modelAction ->
            val menuItem = JMenuItem(modelAction.title)
            menuItem.addActionListener {
                val selectedRow = table.selectedRow
                if (selectedRow != -1) {
                    val selectedPackage = (table.model as PackageTableModel).getPackageAt(selectedRow)
                    val pName = selectedPackage.packageName
                    modelPackageData.deviceModel?.let { selectedDevice ->
                        runAppAction(modelAction, pName, selectedDevice)
                    }
                }
            }
            popupMenu.add(menuItem)
            if (modelAction == SAppAction.copy) {
                popupMenu.add(JSeparator())
            }
        }

        table.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) = maybeShowPopup(e)
            override fun mouseReleased(e: MouseEvent) = maybeShowPopup(e)

            private fun maybeShowPopup(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    val row = table.rowAtPoint(e.point)
                    if (row != -1) {
                        table.setRowSelectionInterval(row, row)
                        popupMenu.show(table, e.x, e.y)
                    }
                }
            }
        })
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
                SAppAction.play_store -> ADBApps.openUrlInAndroidBrowser(buildPlayStoreUrl(pName), selectedDevice.id)
                SAppAction.view_in_desktop -> JopenLink(buildPlayStoreUrl(pName))
                SAppAction.find_online -> JopenLink(buildFindInMarketUrl(pName))
                SAppAction.copy -> ADBHelper.copyTextToClipboard(pName)
                SAppAction.download -> ADBApps.downloadAPK(pName, selectedDevice.id)
                SAppAction.showMore -> {
                    val frame = SwingUtilities.getWindowAncestor(view) as? JFrame
                    frame?.let {
                        showPackageDetails(selectedDevice,frame,"$pName" )
                    }
//                    plan is to on show more open dialog with bith 80% screen and dump show out put of package in single file and
//                    and do section based on above line is blank and key ends with ":" ;like "Receiver Resolver Table:"
//                        """
//
//                            Receiver Resolver Table:
//                        """.trimIndent()
//                    then hightlight the data as per need in text and allow action on this
//                    like """
//                    install permissions:
//      com.google.android.finsky.permission.BIND_GET_INSTALL_REFERRER_SERVICE: granted=true
//      com.google.android.c2dm.permission.RECEIVE: granted=true
//                    """
//                    show it will be good for user that they not miss any data and for dev to show all data
//
                }// selectedPackage = pName
                else -> {}
            }
        }.start()
    }
}