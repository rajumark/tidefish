package screens.apps.right

import adb.DeviceModel
import screens.apps.right.activities.AppsDetailsRightActivitiesController
import screens.apps.right.activities.AppsDetailsRightActivitiesModel
import screens.apps.right.activities.AppsDetailsRightActivitiesView
import screens.apps.right.basic_info.AppsDetailsRightBasicInfoController
import screens.apps.right.basic_info.AppsDetailsRightBasicInfoModel
import screens.apps.right.basic_info.AppsDetailsRightBasicInfoView
import screens.apps.right.dex.AppsDetailsRightDexController
import screens.apps.right.dex.AppsDetailsRightDexModel
import screens.apps.right.dex.AppsDetailsRightDexView
import screens.apps.right.paths.AppsDetailsRightPathsController
import screens.apps.right.paths.AppsDetailsRightPathsModel
import screens.apps.right.paths.AppsDetailsRightPathsView
import screens.apps.right.permissions.AppsDetailsRightPermissionsController
import screens.apps.right.permissions.AppsDetailsRightPermissionsModel
import screens.apps.right.permissions.AppsDetailsRightPermissionsView
import screens.apps.right.providers.AppsDetailsRightProvidersController
import screens.apps.right.providers.AppsDetailsRightProvidersModel
import screens.apps.right.providers.AppsDetailsRightProvidersView
import screens.apps.right.receivers.AppsDetailsRightReceiversController
import screens.apps.right.receivers.AppsDetailsRightReceiversModel
import screens.apps.right.receivers.AppsDetailsRightReceiversView
import java.beans.PropertyChangeListener

class AppDetailsRightController(
    private val model: AppDetailsRightModel,
    private val view: AppDetailsRightView
) {

    private val jCallLogsModel by lazy { AppsDetailsRightBasicInfoModel() }
    private val jCallLogsView by lazy { AppsDetailsRightBasicInfoView() }
    private val jCallLogsController by lazy { AppsDetailsRightBasicInfoController(jCallLogsModel,jCallLogsView) }

    private val permissionsModel by lazy { AppsDetailsRightPermissionsModel() }
    private val permissionsView by lazy { AppsDetailsRightPermissionsView() }
    private val permissionsController by lazy { AppsDetailsRightPermissionsController(permissionsModel, permissionsView) }

    private val activitiesModel by lazy { AppsDetailsRightActivitiesModel() }
    private val activitiesView by lazy { AppsDetailsRightActivitiesView() }
    private val activitiesController by lazy { AppsDetailsRightActivitiesController(activitiesModel, activitiesView) }

    private val servicesModel by lazy { screens.apps.right.services.AppsDetailsRightServicesModel() }
    private val servicesView by lazy { screens.apps.right.services.AppsDetailsRightServicesView() }
    private val servicesController by lazy { screens.apps.right.services.AppsDetailsRightServicesController(servicesModel, servicesView) }

    private val providersModel by lazy { AppsDetailsRightProvidersModel() }
    private val providersView by lazy { AppsDetailsRightProvidersView() }
    private val providersController by lazy { AppsDetailsRightProvidersController(providersModel, providersView) }

    private val receiversModel by lazy { AppsDetailsRightReceiversModel() }
    private val receiversView by lazy { AppsDetailsRightReceiversView() }
    private val receiversController by lazy { AppsDetailsRightReceiversController(receiversModel, receiversView) }

    private val dexModel by lazy { AppsDetailsRightDexModel() }
    private val dexView by lazy { AppsDetailsRightDexView() }
    private val dexController by lazy { AppsDetailsRightDexController(dexModel, dexView) }

    private val pathsModel by lazy { AppsDetailsRightPathsModel() }
    private val pathsView by lazy { AppsDetailsRightPathsView() }
    private val pathsController by lazy { AppsDetailsRightPathsController(pathsModel, pathsView) }

    


    init {
        model.addPropertyChangeListener(PropertyChangeListener { event ->
            when (event.propertyName) {
                AppDetailsRightModel.PROP_selected_package_name -> {
                    view.setPackageName(model.selectedPackageName)
                }
            }
        })

        setupTabs()
    }

    private fun setupTabs() {
        val tabs = listOf(
            "Basic info" to jCallLogsView,
            "Permissions" to permissionsView,
            "Full Info" to activitiesView,
            "Paths & APKs" to pathsView
        )
        view.setupTabs(tabs)
    }

    fun setSelectedPackageName(device: DeviceModel?, name: String?) {
        model.setSelectedPackageName(name)
        device?.id?.let { id ->
            name?.let { packageName ->
                jCallLogsController.setTarget(id, packageName)
                permissionsController.setTarget(id, packageName)
                activitiesController.setTarget(id, packageName)
                servicesController.setTarget(id, packageName)
                providersController.setTarget(id, packageName)
                receiversController.setTarget(id, packageName)
                dexController.setTarget(id, packageName)
                pathsController.setTarget(id, packageName)
            }
        }
    }
}