package first.navigation

enum class TypeOfScreens(val title: String) {
    apps("Apps"),
    settings("Settings"),
    inspector("Inspector"),
    nodevice("No Device"),
    calllogs("Call Logs"),
    messages("Messages"),
    media("Media"),
    services("Services"),
    lifecycle("Lifecycle"),
    contacts("Contacts"),
    calender("Calendar"),
    properties("Properties"),
    adbterminal("Terminal"),
}


val sideMenuList = listOf(
    TypeOfScreens.apps,
    TypeOfScreens.settings,
//    TypeOfScreens.inspector,
    TypeOfScreens.calllogs,
    TypeOfScreens.messages,
    TypeOfScreens.media,
    TypeOfScreens.services,
    TypeOfScreens.lifecycle,
    TypeOfScreens.contacts,
    TypeOfScreens.calender,
    TypeOfScreens.properties,
    TypeOfScreens.adbterminal,
)