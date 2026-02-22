package screens.apps.right.basic_info

import screens.packages.PackageMetadata
import screens.packages.packagedetails.getPackageMetadataInfoByPackageName
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.SwingUtilities

class AppsDetailsRightBasicInfoController(
    private val model: AppsDetailsRightBasicInfoModel,
    private val view: AppsDetailsRightBasicInfoView
) {
    private var currentDeviceId: String? = null
    private var currentPackageName: String? = null

    init {
        view.searchField.addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent) {
                model.filter(view.searchField.text)
                view.submitRows(model.getFilteredRows())
            }
        })

        view.onRefreshClick = {
            reload()
        }
    }

    fun setTarget(deviceId: String, packageName: String) {
        currentDeviceId = deviceId
        currentPackageName = packageName
        reload()
    }

    fun reload() {
        val id = currentDeviceId ?: return
        val pkg = currentPackageName ?: return

        // Load in background to keep UI responsive
        Thread {
            val metadata = getPackageMetadataInfoByPackageName(id, pkg)
            val rows = buildRowsFromMetadata(metadata)
            model.setRows(rows)
            model.filter(view.searchField.text)
            SwingUtilities.invokeLater {
                view.submitRows(model.getFilteredRows())
            }
        }.start()
    }

    private fun buildRowsFromMetadata(metadata: PackageMetadata?): List<Triple<String, String, String>> {
        if (metadata == null) return emptyList()
        val list = mutableListOf<Triple<String, String, String>>()

        fun add(key: String, value: String?) {
            if (value == null) return
            val desc = KeyDescriptions.getDescription(key)
            list.add(Triple(key, value, desc))
        }

        add("appId", metadata.appId)
        add("pkg", metadata.pkg)
        add("versionName", metadata.versionName)
        add("versionCode", metadata.versionCode)
        add("minSdk", metadata.minSdk)
        add("targetSdk", metadata.targetSdk)

        add("installerPackageName", metadata.installerPackageName)
        add("installerPackageUid", metadata.installerPackageUid)
        add("initiatingPackageName", metadata.initiatingPackageName)
        add("originatingPackageName", metadata.originatingPackageName)
        add("updateOwnerPackageName", metadata.updateOwnerPackageName)
        add("packageSource", metadata.packageSource)
        add("timeStamp", metadata.timeStamp)
        add("lastUpdateTime", metadata.lastUpdateTime)

        add("codePath", metadata.codePath)
        add("resourcePath", metadata.resourcePath)
        add("legacyNativeLibraryDir", metadata.legacyNativeLibraryDir)
        add("extractNativeLibs", metadata.extractNativeLibs)
        add("primaryCpuAbi", metadata.primaryCpuAbi)
        add("usesNonSdkApi", metadata.usesNonSdkApi)
        add("isMiuiPreinstall", metadata.isMiuiPreinstall)
        add("splits", metadata.splits)
        add("apkSigningVersion", metadata.apkSigningVersion)
        add("flags", metadata.flags)
        add("privateFlags", metadata.privateFlags)
        add("forceQueryable", metadata.forceQueryable)
        add("queriesPackages", metadata.queriesPackages)
        add("queriesIntents", metadata.queriesIntents)
        add("dataDir", metadata.dataDir)
        add("supportsScreens", metadata.supportsScreens)
        add("appMetadataFilePath", metadata.appMetadataFilePath)
        add("installPermissionsFixed", metadata.installPermissionsFixed)

        return list
    }
}


