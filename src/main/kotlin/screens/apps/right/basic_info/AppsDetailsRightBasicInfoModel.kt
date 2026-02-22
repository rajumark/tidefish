package screens.apps.right.basic_info

class AppsDetailsRightBasicInfoModel {
    private var allRows: List<Triple<String, String, String>> = emptyList()
    private var filteredRows: List<Triple<String, String, String>> = emptyList()

    fun setRows(rows: List<Triple<String, String, String>>) {
        allRows = rows
        filteredRows = rows
    }

    fun filter(query: String) {
        val q = query.trim().lowercase()
        filteredRows = if (q.isEmpty()) {
            allRows
        } else {
            allRows.filter { (k, v, d) ->
                k.lowercase().contains(q) || v.lowercase().contains(q) || d.lowercase().contains(q)
            }
        }
    }

    fun getFilteredRows(): List<Triple<String, String, String>> = filteredRows
    fun getAllRowsCount(): Int = allRows.size
}


