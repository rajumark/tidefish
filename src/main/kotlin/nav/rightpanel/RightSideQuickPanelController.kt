package nav.rightpanel

import adb.ADBKeySimulator
import adb.DeviceModel
import nav.RightSideQuickPanelModel
import nav.RightSideQuickPanelView
import javax.swing.JPopupMenu
import javax.swing.JMenuItem
import javax.swing.SwingWorker

class RightSideQuickPanelController(private val model: RightSideQuickPanelModel, private val view: RightSideQuickPanelView) {


    init {
        view.onActionHomeKeyClick = { triggerKeyEvent { id -> ADBKeySimulator.pressHome(id) } }
        view.onActionBackKeyClick = { triggerKeyEvent { id -> ADBKeySimulator.pressBack(id) } }
        view.onActionRecentKeyClick = { triggerKeyEvent { id -> ADBKeySimulator.pressRecent(id) } }
        view.onActionVolumeUpClick = { triggerKeyEvent { id -> ADBKeySimulator.pressVolumeUp(id) } }
        view.onActionVolumeDownClick = { triggerKeyEvent { id -> ADBKeySimulator.pressVolumeDown(id) } }
        view.onActionSettingsClick = { triggerKeyEvent { id -> ADBKeySimulator.openSettings(id) } }
        view.onActionPowerToggleClick = { triggerKeyEvent { id -> ADBKeySimulator.pressPower(id) } }
        view.onActionPowerLongPressClick = { triggerKeyEvent { id -> ADBKeySimulator.longPressPower(id) } }
        view.onActionScreenshotClick = { triggerKeyEvent { id -> ADBKeySimulator.captureScreenshotToDesktop(id) } }
        view.onActionMediaPlayClick = { triggerKeyEvent { id -> ADBKeySimulator.mediaPlay(id) } }
        view.onActionMediaPauseClick = { triggerKeyEvent { id -> ADBKeySimulator.mediaPause(id) } }
        view.onActionVolumeMuteClick = { triggerKeyEvent { id -> ADBKeySimulator.volumeMute(id) } }
        view.onActionQuickSettingsClick = { triggerKeyEvent { id -> ADBKeySimulator.expandQuickSettings(id) } }
        view.onActionNotificationsClick = { triggerKeyEvent { id -> ADBKeySimulator.expandNotifications(id) } }
        view.onActionCollapseClick = { triggerKeyEvent { id -> ADBKeySimulator.collapseAll(id) } }
        view.onActionUnlockMenuClick = { triggerKeyEvent { id -> ADBKeySimulator.unlockMenu(id) } }
        view.onActionDeveloperSettingsClick = { triggerKeyEvent { id -> ADBKeySimulator.openDeveloperSettings(id) } }
        view.onActionShowTapClick = { showTapMenu() }
    }

    fun setThisDeviceModel(deviceModel: DeviceModel?) {
        model.setThisDeviceModel(deviceModel)
    }

    private fun triggerKeyEvent(action: (String) -> Unit) {
        val id = model.deviceModel?.id ?: return
        object : SwingWorker<Void, Void>() {
            override fun doInBackground(): Void? {
                action(id)
                return null
            }
        }.execute()
    }

    private fun showTapMenu() {
        val id = model.deviceModel?.id ?: return
        
        val popup = JPopupMenu()
        
        val showTapItem = JMenuItem("Show tap dot")
        showTapItem.addActionListener {
            triggerKeyEvent { deviceId -> ADBKeySimulator.showTaps(deviceId) }
        }
        
        val hideTapItem = JMenuItem("Hide tap dot")
        hideTapItem.addActionListener {
            triggerKeyEvent { deviceId -> ADBKeySimulator.hideTaps(deviceId) }
        }
        
        popup.add(showTapItem)
        popup.add(hideTapItem)
        
        popup.show(view.show_tap_button, 0, view.show_tap_button.height)
    }
}


