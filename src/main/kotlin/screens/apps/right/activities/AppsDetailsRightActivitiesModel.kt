package screens.apps.right.activities

class AppsDetailsRightActivitiesModel {
    private var allSections: Map<String, String> = emptyMap()
    private var filteredKeys: List<String> = emptyList()

    fun setSections(sections: Map<String, String>) {
        allSections = sections
        filteredKeys = sections.keys.toList()
    }

    fun filter(query: String) {
        val q = query.trim().lowercase()
        filteredKeys = if (q.isEmpty()) {
            allSections.keys.toList()
        } else {
            allSections.filter { (key, value) ->
                key.lowercase().contains(q) || value.lowercase().contains(q)
            }.keys.toList()
        }
    }

    fun getFilteredKeys(): List<String> = filteredKeys
    fun getSectionText(key: String): String = allSections[key] ?: ""
}


