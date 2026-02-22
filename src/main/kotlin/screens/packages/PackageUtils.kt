package screens.packages

fun String.extractPermission1(): String? {
    val regex = Regex("""\s*(android\.permission\.[^:]+):\s*granted=.*""")
    val matchResult = regex.find(this)
    return matchResult?.groupValues?.get(1)
}

fun String.extractPermission(): String? {
    // Match permission at the start of the line, optionally followed by ": granted=..."
    val regex = Regex("""\s*(android\.permission\.[^\s:]+)""")
    val matchResult = regex.find(this)
    return matchResult?.groupValues?.get(1)
}