package screens.apps.right.services

class AppsDetailsRightServicesController(
    private val model: AppsDetailsRightServicesModel,
    private val view: AppsDetailsRightServicesView
) {
    private var currentDeviceId: String? = null
    private var currentPackageName: String? = null

    fun setTarget(deviceId: String, packageName: String) {
        currentDeviceId = deviceId
        currentPackageName = packageName
    }
}


