package screens.apps.right.permissions

import screens.packages.PermissionInfo

class AppsDetailsRightPermissionsModel {
    var deviceId: String? = null
    var packageName: String? = null

    var requestedPermissions: List<PermissionInfo> = emptyList()
    var installPermissions: List<PermissionInfo> = emptyList()
    var runtimePermissions: List<PermissionInfo> = emptyList()

    var searchQuery: String = ""
}


