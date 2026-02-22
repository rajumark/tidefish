package screens.apps.right.providers

class AppsDetailsRightProvidersController(
    private val model: AppsDetailsRightProvidersModel,
    private val view: AppsDetailsRightProvidersView
) {
    private var currentDeviceId: String? = null
    private var currentPackageName: String? = null

    fun setTarget(deviceId: String, packageName: String) {
        currentDeviceId = deviceId
        currentPackageName = packageName
    }
}


