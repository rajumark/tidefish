package screens.apps.right.dex

class AppsDetailsRightDexController(
    private val model: AppsDetailsRightDexModel,
    private val view: AppsDetailsRightDexView
) {
    private var currentDeviceId: String? = null
    private var currentPackageName: String? = null

    fun setTarget(deviceId: String, packageName: String) {
        currentDeviceId = deviceId
        currentPackageName = packageName
    }
}


