package screens.inspector


fun Hierarchy.getHierarchyDepth(): Int {
    return getNodeDepth( nodes)
}



private fun getNodeDepth(nodes: List<Node>): Int {
    if (nodes.isEmpty()) {
        return 0
    }

    var maxDepth = 0
    for (node in nodes) {
        val childDepth = getNodeDepth(node.childNodes)
        maxDepth = maxOf(maxDepth, childDepth)
    }

    return maxDepth + 1
}
