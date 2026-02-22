package screens.apps.right.paths

class AppsDetailsRightPathsModel {
    private var fullLines: List<String> = emptyList()

    fun setFromRawOutput(rawOutput: String) {
        fullLines = rawOutput.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .map { line -> if (line.startsWith("package:")) line.removePrefix("package:") else line }
    }

    fun filter(query: String): List<String> {
        if (query.isBlank()) return fullLines
        val q = query.lowercase()
        return fullLines.filter { it.lowercase().contains(q) }
    }
}


