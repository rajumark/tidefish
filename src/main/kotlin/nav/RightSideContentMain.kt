package nav

import adb.DeviceModel
import first.navigation.TypeOfScreens
import screens.apps.AppsListController
import screens.apps.AppsListModel
import screens.apps.AppsListView
import screens.calendar.JCalendarController
import screens.calendar.JCalendarModel
import screens.calendar.JCalendarView
import screens.calllogs.JCallLogsController
import screens.calllogs.JCallLogsModel
import screens.calllogs.JCallLogsView
import screens.contacts.JContactsController
import screens.contacts.JContactsModel
import screens.contacts.JContactsView
import screens.deviceproperties.DevicePropertiesController
import screens.deviceproperties.DevicePropertiesModel
import screens.deviceproperties.DevicePropertiesView
import screens.feedback.JFeedbackPane
import screens.inspector.InspectorController
import screens.inspector.InspectorModel
import screens.inspector.InspectorView
import screens.lifecycle.JLifeCycleController
import screens.lifecycle.JLifeCycleModel
import screens.lifecycle.JLifeCycleView
import screens.media.JMediaController
import screens.media.JMediaModel
import screens.media.JMediaView
import screens.messages.JMessagesController
import screens.messages.JMessagesModel
import screens.messages.JMessagesView
import screens.nodevice.NoDeviceView
import screens.packages.PackagesListController
import screens.packages.PackagesListModel
import screens.packages.PackagesListView2
import screens.processes.JProcessesController
import screens.processes.JProcessesModel
import screens.processes.JProcessesView
import screens.settings.JSettingsListPage
import adb_terminal.ADBTerminalController
import adb_terminal.ADBTerminalModel
import adb_terminal.ADBTerminalView
import java.awt.BorderLayout
import java.awt.CardLayout
import javax.swing.JPanel

class RightSideContentMain: JPanel() {

    private val packagesListModel by lazy { PackagesListModel() }
    private val packagesListView by lazy { PackagesListView2() }
    private val packagesListController by lazy { PackagesListController(packagesListModel,packagesListView) }

    private val jFeedbackPane by lazy { JFeedbackPane( ) }
    private val noDeviceView by lazy { NoDeviceView() }
    private val jSettingsListPage by lazy { JSettingsListPage() }

    private val jCallLogsModel by lazy { JCallLogsModel() }
    private val jCallLogsView by lazy { JCallLogsView() }
    private val jCallLogsController by lazy { JCallLogsController(jCallLogsModel,jCallLogsView) }

    private val jMessagesModel by lazy { JMessagesModel() }
    private val jMessagesView by lazy { JMessagesView() }
    private val jMessagesController by lazy { JMessagesController(jMessagesModel,jMessagesView) }

    private val mediaModel by lazy { JMediaModel() }
    private val mediaView by lazy { JMediaView() }
    private val mediaController by lazy { JMediaController(mediaModel,mediaView) }

    private val inspectorModel by lazy { InspectorModel() }
    private val inspectorView by lazy { InspectorView() }
    private val inspectorController by lazy { InspectorController(inspectorModel,inspectorView) }

    private val processesModel by lazy { JProcessesModel() }
    private val processesView by lazy { JProcessesView() }
    private val processesController by lazy { JProcessesController(processesModel, processesView) }

    private val lifeCycleModel by lazy { JLifeCycleModel() }
    private val lifeCycleView by lazy { JLifeCycleView() }
    private val lifeCycleController by lazy { JLifeCycleController(lifeCycleModel, lifeCycleView) }

    private val contactsModel by lazy { JContactsModel() }
    private val contactsView by lazy { JContactsView() }
    private val contactsController by lazy { JContactsController(contactsModel, contactsView) }

    private val jCalendarModel by lazy { JCalendarModel() }
    private val jCalendarView by lazy { JCalendarView() }
    private val jCalendarController by lazy { JCalendarController(jCalendarModel, jCalendarView) }


    private val devicePropertiesModel by lazy { DevicePropertiesModel() }
    private val devicePropertiesView by lazy { DevicePropertiesView() }
    private val devicePropertiesController by lazy { DevicePropertiesController(devicePropertiesModel,devicePropertiesView) }


    private val appsListModel by lazy { AppsListModel() }
    private val appsListView by lazy { AppsListView() }
    private val appsListController by lazy { AppsListController(appsListModel, appsListView) }

    private val adbTerminalModel by lazy { ADBTerminalModel() }
    private val adbTerminalView by lazy { ADBTerminalView() }
    private val adbTerminalController by lazy { ADBTerminalController(adbTerminalModel, adbTerminalView) }


    private val cardLayout = CardLayout()
    private val bodyPanel = JPanel(cardLayout)



    init {
        layout = BorderLayout()


        bodyPanel.add(appsListView, TypeOfScreens.apps.name)
//        bodyPanel.add(jFeedbackPane, TypeOfScreens.feedback.name)
        bodyPanel.add(noDeviceView, TypeOfScreens.nodevice.name)
        bodyPanel.add(jSettingsListPage, TypeOfScreens.settings.name)
        bodyPanel.add(jCallLogsView, TypeOfScreens.calllogs.name)
        bodyPanel.add(jMessagesView, TypeOfScreens.messages.name)
        bodyPanel.add(mediaView, TypeOfScreens.media.name)
        bodyPanel.add(inspectorView, TypeOfScreens.inspector.name)
        bodyPanel.add(processesView, TypeOfScreens.services.name)
        bodyPanel.add(lifeCycleView, TypeOfScreens.lifecycle.name)
        bodyPanel.add(contactsView, TypeOfScreens.contacts.name)
        bodyPanel.add(jCalendarView, TypeOfScreens.calender.name)
        bodyPanel.add(devicePropertiesView, TypeOfScreens.properties.name)
        bodyPanel.add(adbTerminalView, TypeOfScreens.adbterminal.name)

        // Add components to the frame
        add(bodyPanel, BorderLayout.CENTER)


    }



    fun setThisScreen(deviceModel: DeviceModel?, currentScreen: TypeOfScreens) {
        showScreen(if(deviceModel==null) TypeOfScreens.nodevice else currentScreen)
        updateControllerData(deviceModel)
    }

    fun showScreen(screenName: TypeOfScreens) {
        cardLayout.show(bodyPanel, screenName.name)
    }


    fun updateControllerData(deviceModel: DeviceModel?) {
        appsListController.setDeviceModel(deviceModel)
        jSettingsListPage.setDeviceModel(deviceModel)
        jCallLogsController.setDeviceModel(deviceModel)
        jMessagesController.setDeviceModel(deviceModel)
        mediaController.setDeviceModel(deviceModel)
        inspectorController.setDeviceModel(deviceModel)
        processesController.setDeviceModel(deviceModel)
        lifeCycleController.setDeviceModel(deviceModel)
        contactsController.setDeviceModel(deviceModel)
        jCalendarController.setDeviceModel(deviceModel)
        devicePropertiesController.setDeviceModel(deviceModel)
        adbTerminalController.setDeviceModel(deviceModel)
    }

}