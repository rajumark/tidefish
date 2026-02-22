package screens.inspector

import javax.swing.JComponent
import javax.swing.JOptionPane
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel


fun showNodeInMessages(node: Node, tree: JComponent) {

    val columnNames = arrayOf("Property", "Value")
    val data = arrayOf(
        arrayOf("Resource ID", node.resourceId.split("/").lastOrNull() ?: node.resourceId),
        arrayOf("Class Name", node.className),
        arrayOf("Text", node.text),
        arrayOf("Bounds", node.bounds),
        arrayOf("Content Desc", node.contentDesc),
        arrayOf("Checkable", node.checkable.toString()),
        arrayOf("Checked", node.checked.toString()),
        arrayOf("Clickable", node.clickable.toString()),
        arrayOf("Enabled", node.enabled.toString()),
        arrayOf("Focusable", node.focusable.toString()),
        arrayOf("Focused", node.focused.toString()),
        arrayOf("Index", node.index.toString()),
        arrayOf("Long Clickable", node.longClickable.toString()),
        arrayOf("Package Name", node.packageName),
        arrayOf("Password", node.password.toString()),
        arrayOf("Scrollable", node.scrollable.toString()),
        arrayOf("Selected", node.selected.toString()),
    )

    // Create a JTable with non-selectable keys and selectable values
    val tableModel = DefaultTableModel(data, columnNames)
    val table = JTable(tableModel).apply {
        getColumnModel().getColumn(0).cellRenderer =
            DefaultTableCellRenderer().apply { isOpaque = true }
        getColumnModel().getColumn(1).cellRenderer =
            DefaultTableCellRenderer().apply { isOpaque = true }
    }

    // Show the table in a JOptionPane dialog
    JOptionPane.showMessageDialog(tree, JScrollPane(table), "Node Properties", JOptionPane.INFORMATION_MESSAGE)
}