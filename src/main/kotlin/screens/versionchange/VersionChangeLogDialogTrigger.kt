package screens.versionchange

import adb.ADBHelper.getCurrentVersion
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.Timer


fun JFrame.showWhatsNewDialogIfNotSeenAfterShown(delayMillis: Int = 2000) {
    val currentVersion = getCurrentVersion()
    if (currentVersion.isBlank()) return
    if (VersionSeenStore.shouldShowForVersion(currentVersion) ) {   // ✅ Check first
        SwingUtilities.invokeLater {
            Timer(delayMillis) {
                VersionChangeLogDialog(this@showWhatsNewDialogIfNotSeenAfterShown, currentVersion).apply {
                    isVisible = true
                }
            }.apply {
                isRepeats = false
                start()
            }
        }
    }
}
