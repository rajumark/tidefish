package screens.packages

data class AppsBox(
    var packageName: String,
    var installTime: Long?,
    var isPined: Boolean = false
) {}