package utils.table

import javax.swing.JTable

fun JTable.applyTableColumnsWidth(columnWidths: Map<String, Int>) {
    for (i in 0 until columnModel.columnCount) {
        val column = columnModel.getColumn(i)
        val width = columnWidths[column.headerValue] ?: column.preferredWidth // keep existing if not in map
        column.preferredWidth = width
    }
}