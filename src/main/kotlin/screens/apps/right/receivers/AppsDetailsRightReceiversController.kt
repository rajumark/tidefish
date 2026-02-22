package screens.apps.right.receivers

class AppsDetailsRightReceiversController(
    private val model: AppsDetailsRightReceiversModel,
    private val view: AppsDetailsRightReceiversView
) {
    private var currentDeviceId: String? = null
    private var currentPackageName: String? = null

    fun setTarget(deviceId: String, packageName: String) {
        currentDeviceId = deviceId
        currentPackageName = packageName
    }
}


