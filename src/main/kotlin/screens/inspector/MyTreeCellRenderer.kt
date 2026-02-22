package screens.inspector

import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer

val transperentColor = Color(255, 0, 0, 0);

val color_tree_node_background_clicked_selected = Color(0, 255, 0, 60)
val color_tree_node_background_clicked = Color(0, 255, 0, 128)
val color_tree_node_background_focused = Color(255, 255, 0, 128)
val color_tree_node_background_highlighted = Color(0, 0, 0, 90)

class MyTreeCellRenderer : DefaultTreeCellRenderer() {
    var levelnods: List<Node>?=null
    var clickNode: Node? = null
    override fun getTreeCellRendererComponent(
        tree: JTree,
        value: Any,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ): Component {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus)
        preferredSize = Dimension(preferredSize.width, 21)
        isOpaque = true
        if (value is DefaultMutableTreeNode) {
            val userObject = value.userObject
            if (userObject is Node) {
                text = getProperTextName(userObject)
                background = if (userObject == clickNode) {
                    if (selected){
                        color_tree_node_background_clicked_selected
                    }else{
                        color_tree_node_background_clicked
                    }
                } else {
                    if (userObject.focused) {
                        color_tree_node_background_focused
                    } else {
                        transperentColor
                    }
                }

                border = if (levelnods?.any { it==userObject } == true) {
                    BorderFactory.createLineBorder(color_tree_node_background_highlighted, 2) // Red frame
                }else{
                    null
                }
            }
        }
        return this
    }
}

fun getProperTextName(name: Node): String {
    if (name.className.lowercase().contains("TextView".lowercase())) {
        return name.text.getFirstTenAndLastFour().takeIf { it.isBlank().not() } ?: name.className.getSortTreeName()
    }
    return name.className.getSortTreeName()
}
fun String.getSortTreeName(): String {
    return this.split(".").lastOrNull()?.capitalizeFirstLetter() ?: this
}
fun String.getFirstTenAndLastFour(): String {
    return if (this.length >= 15) {
        this.take(10) + "..." + this.takeLast(4)
    } else {
        this
    }
}

fun String.capitalizeFirstLetter(): String {
    return this.replaceFirstChar { it.uppercase() }
}