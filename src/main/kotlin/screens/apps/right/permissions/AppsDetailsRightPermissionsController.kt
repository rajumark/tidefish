package screens.apps.right.permissions

import screens.packages.ADBApps
import screens.packages.PermissionInfo

import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

class AppsDetailsRightPermissionsController(
    private val model: AppsDetailsRightPermissionsModel,
    private val view: AppsDetailsRightPermissionsView
) {
    private var currentDeviceId: String? = null
    private var currentPackageName: String? = null

    fun setTarget(deviceId: String, packageName: String) {
        currentDeviceId = deviceId
        currentPackageName = packageName
        model.deviceId = deviceId
        model.packageName = packageName
        setupListenersOnce()
        refreshPermissions()
    }

    private var listenersSetup = false
    private fun setupListenersOnce() {
        if (listenersSetup) return
        listenersSetup = true

        // When filter selection changes via radios, update the list
        val radioListener = java.awt.event.ActionListener { submitActiveTab() }
        view.filterRequestedRadio.addActionListener(radioListener)
        view.filterInstallRadio.addActionListener(radioListener)
        view.filterRuntimeRadio.addActionListener(radioListener)

        view.onRefreshClick = { refreshPermissions() }
        view.restartButton.addActionListener { currentPackageName?.let { pkg -> currentDeviceId?.let { id -> ADBApps.restartApp(pkg, id) } } }
        view.appInfoButton.addActionListener { currentPackageName?.let { pkg -> currentDeviceId?.let { id -> ADBApps.openAppSettings(pkg, id) } } }
        view.grantAllButton.addActionListener {
            view.grantAll()
            val list = activeList().map { it.permission }
            currentPackageName?.let { pkg -> currentDeviceId?.let { id -> ADBApps.grantAllPermissions(pkg, list, id) } }
            refreshPermissions()
        }
        view.revokeAllButton.addActionListener {
            view.revokeAll()
            val list = activeList().map { it.permission }
            currentPackageName?.let { pkg -> currentDeviceId?.let { id -> ADBApps.revokeAllPermissions(pkg, list, id) } }
            refreshPermissions()
        }
        view.onTogglePermission = { permission, granted ->
            currentPackageName?.let { pkg -> currentDeviceId?.let { id ->
                if (granted) {
                    ADBApps.grantAllPermissions(pkg, listOf(permission), id)
                } else {
                    ADBApps.revokeAllPermissions(pkg, listOf(permission), id)
                }
                refreshPermissions()
            } }
        }

        // search
        view.searchField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = onChange()
            override fun removeUpdate(e: DocumentEvent) = onChange()
            override fun changedUpdate(e: DocumentEvent) = onChange()
            private fun onChange() {
                model.searchQuery = view.searchField.text.orEmpty().trim()
                submitActiveTab()
            }
        })

    }

    private fun refreshPermissions() {
        val id = currentDeviceId ?: return
        val pkg = currentPackageName ?: return
        val map = ADBApps.getAllRuntimePermissions(pkg, id)
        model.requestedPermissions = map["requested_permissions"].orEmpty()
        model.installPermissions = map["install_permissions"].orEmpty()
        model.runtimePermissions = map["runtime_permissions"].orEmpty()
        submitActiveTab()


    }

    private fun submitActiveTab() {
        val q = model.searchQuery.lowercase()
        when (view.selectedFilterIndex()) {
            0 -> {
                val list = model.requestedPermissions.filter { it.permission.contains(q, true) }
                view.submitPermissionsRows(list.map { it.granted to it.permission })
            }
            1 -> {
                val list = model.installPermissions.filter { it.permission.contains(q, true) }
                view.submitPermissionsRows(list.map { it.granted to it.permission })
            }
            2 -> {
                val list = model.runtimePermissions.filter { it.permission.contains(q, true) }
                view.submitPermissionsRows(list.map { it.granted to it.permission })
            }
        }
    }

    private fun activeList(): List<PermissionInfo> {
        return when (view.selectedFilterIndex()) {
            0 -> model.requestedPermissions
            1 -> model.installPermissions
            else -> model.runtimePermissions
        }
    }

    // Table-based listener removed; JList now emits toggle via view.onTogglePermission
}


