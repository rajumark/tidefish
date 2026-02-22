package first.menu

import first.navigation.TypeOfScreens
import javax.swing.ButtonGroup
import javax.swing.JMenu
import javax.swing.JRadioButtonMenuItem

fun getNavigationMenuHelper(onNavigationClick: (TypeOfScreens) -> Unit): JMenu {
    val buttonGroup = ButtonGroup()
    return navigationMenu("Navigation") {
        val firstItem = navRadioItem("Apps List", TypeOfScreens.apps, buttonGroup, onNavigationClick)
        navRadioItem("Inspector", TypeOfScreens.inspector, buttonGroup, onNavigationClick)
        navRadioItem("Settings", TypeOfScreens.settings, buttonGroup, onNavigationClick)
        addSeparator()
        navRadioItem("Call Logs", TypeOfScreens.calllogs, buttonGroup, onNavigationClick)
        navRadioItem("Messages", TypeOfScreens.messages, buttonGroup, onNavigationClick)
        navRadioItem("Contacts", TypeOfScreens.contacts, buttonGroup, onNavigationClick)
        navRadioItem("Calender", TypeOfScreens.calender, buttonGroup, onNavigationClick)
        navRadioItem("Media", TypeOfScreens.media, buttonGroup, onNavigationClick)
        navRadioItem("Services", TypeOfScreens.services, buttonGroup, onNavigationClick)
        navRadioItem("Lifecycle", TypeOfScreens.lifecycle, buttonGroup, onNavigationClick)
        navRadioItem("Properties", TypeOfScreens.properties, buttonGroup, onNavigationClick)
        navRadioItem("ADB Terminal", TypeOfScreens.adbterminal, buttonGroup, onNavigationClick)
        navRadioItem("Notifications", TypeOfScreens.notifications, buttonGroup, onNavigationClick)
        addSeparator()
//        navRadioItem("Feedback", TypeOfScreens.feedback, buttonGroup, onNavigationClick)
    }
}
fun navigationMenu(title: String, init: JMenu.() -> Unit): JMenu {
    return JMenu(title).apply(init)
}
// Updated DSL Helper for radio items
fun JMenu.navRadioItem(
    title: String,
    screen: TypeOfScreens,
    group: ButtonGroup,
    onClick: (TypeOfScreens) -> Unit
) {
    val item = JRadioButtonMenuItem(title).apply {
        addActionListener {
            isSelected = true
            onClick(screen)
        }
    }
    item.isSelected = title=="Apps List"
    group.add(item)
    this.add(item)
}
