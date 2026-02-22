package screens.packages


enum class SAppAction(val title: String) {
    start("Open"),
    force_stop("Force Stop"),
    restart("Restart"),
    uninstall("Uninstall"),
    clearData("Clear Data"),
    enable("Enable"),
    disable("Disable"),
    app_info("Open App Info"),
    home("Home"),
    play_store("View at Playstore"),
    view_in_desktop("View at Desktop"),
    find_online("Find online"),
    copy("Copy"),
    download("Download APK"),
    showMore("Show More"),
    pin("Pin"),
}

val slistAppActions= listOf(
    SAppAction.start,
    SAppAction.force_stop,
    SAppAction.restart,
    SAppAction.uninstall,
    SAppAction.clearData,
    SAppAction.enable,
    SAppAction.disable,
    SAppAction.home,
    SAppAction.copy,
    //SAppAction.download,
    SAppAction.app_info,
    SAppAction.play_store,
    SAppAction.view_in_desktop,
    SAppAction.find_online,
    //AppAction.pin,
     //SAppAction.showMore,
)