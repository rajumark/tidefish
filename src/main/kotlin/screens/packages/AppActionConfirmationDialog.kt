package screens.packages

import javax.swing.JOptionPane

fun confirmUnInstall(pName: String, onPositiveAction: () -> Unit) {
    val message =
        "Are you sure you want to remove all data for the application '$pName'? This action cannot be undone."
    val options = arrayOf("Uninstall", "Cancel")

    val response = JOptionPane.showOptionDialog(
        null,
        message,
        "Uninstall $pName",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        options,
        options[0]
    )

    if (response == JOptionPane.YES_OPTION) {
        onPositiveAction()
    }
}

fun confirmClearData(pName: String, onPositiveAction: () -> Unit) {
    val message =
        "Are you sure you want to clear all data for the application '$pName'? This will reset the app to its original state and all saved information will be lost."
    val options = arrayOf("Clear Storage", "Cancel")

    val response = JOptionPane.showOptionDialog(
        null,
        message,
        "Clear Storage $pName",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        options,
        options[0]
    )

    if (response == JOptionPane.YES_OPTION) {
        onPositiveAction()
    }
}

