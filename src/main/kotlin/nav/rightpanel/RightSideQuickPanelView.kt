package nav

import colors.LightColorsConst
import components.getIconJLabel
import components.horizontalDivider
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JPanel

class RightSideQuickPanelView : JPanel() {
    var onActionBackKeyClick: (() -> Unit)? = null
    var onActionHomeKeyClick: (() -> Unit)? = null
    var onActionRecentKeyClick: (() -> Unit)? = null
    var onActionVolumeUpClick: (() -> Unit)? = null
    var onActionVolumeDownClick: (() -> Unit)? = null
    var onActionSettingsClick: (() -> Unit)? = null
    var onActionPowerToggleClick: (() -> Unit)? = null
    var onActionPowerLongPressClick: (() -> Unit)? = null
    var onActionScreenshotClick: (() -> Unit)? = null
    var onActionMediaPlayClick: (() -> Unit)? = null
    var onActionMediaPauseClick: (() -> Unit)? = null
    var onActionVolumeMuteClick: (() -> Unit)? = null
    var onActionQuickSettingsClick: (() -> Unit)? = null
    var onActionNotificationsClick: (() -> Unit)? = null
    var onActionCollapseClick: (() -> Unit)? = null
    var onActionUnlockMenuClick: (() -> Unit)? = null
    var onActionDeveloperSettingsClick: (() -> Unit)? = null
    var onActionShowTapClick: (() -> Unit)? = null

    // Back button
    val back_button by lazy {
        getIconJLabel(icon = "ic_back.svg", onClick = {
            onActionBackKeyClick?.invoke()
        }).apply {
            toolTipText = "Back Key"
        }
    }

    // Home button
    val home_button by lazy {
        getIconJLabel(icon = "ic_home.svg", onClick = {
            onActionHomeKeyClick?.invoke()
        }).apply {
            toolTipText = "Home Key"
        }
    }

    // Recent button
    val recent_button by lazy {
        getIconJLabel(icon = "ic_recent.svg", onClick = {
            onActionRecentKeyClick?.invoke()
        }).apply {
            toolTipText = "Recent Apps Key"
        }
    }

    // Volume Up button
    val volume_up_button by lazy {
        getIconJLabel(icon = "ic_volume_up.svg", onClick = {
            onActionVolumeUpClick?.invoke()
        }).apply {
            toolTipText = "Volume Up"
        }
    }

    // Volume Down button
    val volume_down_button by lazy {
        getIconJLabel(icon = "ic_volume_down.svg", onClick = {
            onActionVolumeDownClick?.invoke()
        }).apply {
            toolTipText = "Volume Down"
        }
    }

    // Settings button
    val settings_button by lazy {
        getIconJLabel(icon = "ic_settings.svg", onClick = {
            onActionSettingsClick?.invoke()
        }).apply {
            toolTipText = "Open Settings"
        }
    }

    // Power toggle button
    val power_toggle_button by lazy {
        getIconJLabel(icon = "ic_power.svg", onClick = {
            onActionPowerToggleClick?.invoke()
        }).apply {
            toolTipText = "Screen Lock/Unlock (Power)"
        }
    }

    // Power long press button
    val power_long_press_button by lazy {
        getIconJLabel(icon = "ic_assist.svg", onClick = {
            onActionPowerLongPressClick?.invoke()
        }).apply {
            toolTipText = "Power Long Press"
        }
    }

    // Screenshot button
    val screenshot_button by lazy {
        getIconJLabel(icon = "ic_screenshot.svg", onClick = {
            onActionScreenshotClick?.invoke()
        }).apply {
            toolTipText = "Screenshot to Desktop"
        }
    }

    // Media Play button
    val media_play_button by lazy {
        getIconJLabel(icon = "ic_media_play.svg", onClick = {
            onActionMediaPlayClick?.invoke()
        }).apply {
            toolTipText = "Media Play"
        }
    }

    // Media Pause button
    val media_pause_button by lazy {
        getIconJLabel(icon = "ic_media_pause.svg", onClick = {
            onActionMediaPauseClick?.invoke()
        }).apply {
            toolTipText = "Media Pause"
        }
    }

    // Volume Mute button
    val volume_mute_button by lazy {
        getIconJLabel(icon = "ic_volume_mute.svg", onClick = {
            onActionVolumeMuteClick?.invoke()
        }).apply {
            toolTipText = "Volume Mute"
        }
    }

    // Quick Settings button
    val quick_settings_button by lazy {
        getIconJLabel(icon = "ic_notification_tiles.svg", onClick = {
            onActionQuickSettingsClick?.invoke()
        }).apply {
            toolTipText = "Quick Settings"
        }
    }

    // Notifications button
    val notifications_button by lazy {
        getIconJLabel(icon = "ic_notification_down.svg", onClick = {
            onActionNotificationsClick?.invoke()
        }).apply {
            toolTipText = "Notifications"
        }
    }

    // Collapse button
    val collapse_button by lazy {
        getIconJLabel(icon = "ic_notification_collapse.svg", onClick = {
            onActionCollapseClick?.invoke()
        }).apply {
            toolTipText = "Collapse All"
        }
    }

    // Unlock Menu button
    val unlock_menu_button by lazy {
        getIconJLabel(icon = "ic_popup_menu.svg", onClick = {
            onActionUnlockMenuClick?.invoke()
        }).apply {
            toolTipText = "Unlock Menu"
        }
    }

    // Developer Settings button
    val developer_settings_button by lazy {
        getIconJLabel(icon = "ic_developer_option.svg", onClick = {
            onActionDeveloperSettingsClick?.invoke()
        }).apply {
            toolTipText = "Developer Settings"
        }
    }

    // Show Tap button
    val show_tap_button by lazy {
        getIconJLabel(icon = "ic_show_tap.svg", onClick = {
            onActionShowTapClick?.invoke()
        }).apply {
            toolTipText = "Show Tap Options"
        }
    }

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = LightColorsConst.color_background_sidemenu
        preferredSize = Dimension(26, 0)

        add(back_button)
        add(home_button)
        add(recent_button)
        add(horizontalDivider())
        add(volume_up_button)
        add(volume_down_button)
        add(media_play_button)
        add(media_pause_button)
        add(volume_mute_button)
        add(horizontalDivider())
        add(settings_button)
        add(power_toggle_button)
        add(power_long_press_button)
        add(screenshot_button)
        add(horizontalDivider())
        add(quick_settings_button)
        add(notifications_button)
        add(collapse_button)
        add(horizontalDivider())
        add(developer_settings_button)
        add(show_tap_button)
    }
}
