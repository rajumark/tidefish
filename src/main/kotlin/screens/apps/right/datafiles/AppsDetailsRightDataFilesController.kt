package screens.apps.right.datafiles

class AppsDetailsRightDataFilesController(
    private val model: AppsDetailsRightDataFilesModel,
    private val view: AppsDetailsRightDataFilesView
) {
    private var currentDeviceId: String? = null
    private var currentPackageName: String? = null

    fun setTarget(deviceId: String, packageName: String) {
        currentDeviceId = deviceId
        currentPackageName = packageName
    }
}


