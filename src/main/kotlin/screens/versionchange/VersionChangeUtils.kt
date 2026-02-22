package screens.versionchange


fun versionLogs(vararg logs: Pair<String, List<String>>): List<VersionChangeLog> =
    logs.map { VersionChangeLog(it.first, it.second) }

fun changes(vararg items: String): List<String> = items.toList()
