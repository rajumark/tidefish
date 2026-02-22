package screens.inspector

import adb.DeviceModel
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.beans.PropertyChangeListener
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath

class InspectorController(val model: InspectorModel, val view: InspectorView) {
    val eventListener = PropertyChangeListener { event ->
        when (event.propertyName) {
            InspectorModel.PROP_inspector_list -> {}
            InspectorModel.PROP_search_text_query_inspector -> {}
            InspectorModel.PROP_current_screen_name_inspector -> {
                updateACtivityFragmentsList()
                refreshFragments()
            }

            InspectorModel.PROP_screnshot_path_inspector -> {
                handleScreenshotImage()
            }

            InspectorModel.PROP_fragment_list_string_inspector -> {}
            InspectorModel.PROP_tree_data_inspector -> {}
            InspectorModel.PROP_click_node_inspector -> {
                handle_click_node_inspector()
            }

            InspectorModel.PROP_slider_node_inspector -> {
                drawAndHighlightThisLevelNodes(model.sliderNode)
            }


            else -> {}
        }
    }

    private fun handleScreenshotImage() {

        Thread {
            try {
                val sizeimage = model.screnshotPath?.getImageSize()
                val rationLocal = (sizeimage?.first ?: 0).toFloat() / (sizeimage?.second ?: 0).toFloat()
                model.setRationImage(rationLocal)
                if (model.screnshotPath != null) {
                    view.jInspectImagePan.setRationSize(model.rationImage, model.screnshotPath!!, sizeimage!!)
                }
            } catch (e: Exception) {

            }
        }.start()
    }

    private fun handle_click_node_inspector() {
        scrollToNode(view.treeUI, model.clickNodeRw)
        refreshTreeNodeForClicked()
    }

    fun setDeviceModel(deviceModel: DeviceModel?) {
        if (model.deviceModel != deviceModel) {
            model.setDeviceModel(deviceModel)
            fetchData()
        }
    }

    init {
        setModelChangeListener()
        setupViewListener()
    }

    private fun fetchData() {

        refreshCurrentActivity()
        view.refreshButton.isEnabled = false
        view.refreshButton.text = "Wait..."
        model.setTreeData(null)
        updateListUI()


        Thread {
            model.setTreeData(ADBInspect.getUITree(model.deviceModel!!.id))
            view.refreshButton.isEnabled = true
            view.refreshButton.text = "Refresh"
            updateListUI()

        }.start()
        Thread {
            model.setScrenshotPath(null)
            model.setScrenshotPath(ADBInspect.captureScreenshot(model.deviceModel!!.id))
        }.start()

    }

    private fun refreshCurrentActivity() {
        model.deviceModel?.id?.let { itId ->
            Thread {
                val currentScreenNameLocal = ADBInspect.getCurrentActivityName(itId)
                model.setCurrentScreenName(currentScreenNameLocal)
            }.start()
        }
    }

    private fun updateListUI() {
        //var rootNode = DefaultMutableTreeNode("Root")


        view.slider.setMinimum(0)
        view.slider.setMaximum(model.treeData?.getHierarchyDepth() ?: 100)
        if (view.slider.value !in 0..(model.treeData?.getHierarchyDepth() ?: 100)) {
            view.slider.value = 0
        }

        val rootNode = createTreeFromHierarchy(model.treeData, model.clickNodeRw)

        val treeModel = DefaultTreeModel(rootNode)
        treeModel.setRoot(rootNode)
        view.treeUI.model = (treeModel)

        expandAllNodes(view.treeUI)
        view.leftPanel.invalidate()
        view.leftPanel.repaint()
    }

    fun createTreeFromHierarchy(hierarchy: Hierarchy?, clickNode: Node?): DefaultMutableTreeNode {
        val rootNode = DefaultMutableTreeNode(
            Node(
                className =   "RootNode"
            )
        )

        // If hierarchy is not null, add its nodes
        hierarchy?.nodes?.forEach { node ->
            rootNode.add(convertToTreeNodes(node))
        }

        // Create and return the JTree using the root node
        return rootNode
    }
    fun convertToTreeNodes(node: Node): DefaultMutableTreeNode {
        // Create the tree node for the current Node
        val treeNode =
            //  DefaultMutableTreeNode(node.className) // Use text or className as node label
            DefaultMutableTreeNode(node) // Use text or className as node label

        // Recursively add child nodes
        for (child in node.childNodes) {
            treeNode.add(convertToTreeNodes(child))
        }

        return treeNode
    }
    private fun expandAllNodes(tree: JTree) {
        val root = tree.model.root as TreeNode
        expandAll(tree, TreePath(root))
    }

    private fun expandAll(tree: JTree, path: TreePath) {
        tree.expandPath(path) // Expand the current node
        val node = path.lastPathComponent as TreeNode

        // Recursively expand all child nodes
        for (i in 0 until node.childCount) {
            val childNode = node.getChildAt(i)
            val childPath = path.pathByAddingChild(childNode)
            expandAll(tree, childPath)
        }
    }


    private fun setupViewListener() {
        view.refreshButton.addActionListener {
            fetchData()
        }
        view.textviewComp.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (e.isPopupTrigger) {
                    showPopup(e.x, e.y, getACtivityFragmentsList())
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                if (e.isPopupTrigger) {
                    showPopup(e.x, e.y, getACtivityFragmentsList())
                }
            }
        })
        view.treeUI.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (e.isPopupTrigger) {
                     showPopupMenu(view.treeUI , e)
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                if (e.isPopupTrigger) {
                    showPopupMenu(view.treeUI, e)
                }
            }
        })

        view.jInspectImagePan.setClickListenerNode { clickNodeRw ->
            model.setClickNode(clickNodeRw)

        }
        view.rightPanel.addMouseWheelListener { e ->
            if (e.wheelRotation < 0) {
                if ((view.slider.value - 1) in view.slider.minimum..view.slider.maximum) {
                    view.slider.value -= 1
                }
            } else {
                if ((view.slider.value + 1) in view.slider.minimum..view.slider.maximum) {
                    view.slider.value += 1
                }
            }
        }
        view.slider.addChangeListener(object : ChangeListener {
            override fun stateChanged(e: ChangeEvent?) {
                model.setSliderNode(view.slider.value)
            }
        })
    }

    private fun setModelChangeListener() {

        model.addPropertyChangeListener(eventListener)
    }

    private fun drawAndHighlightThisLevelNodes(level: Int) {
        if (model.treeData == null) return
        val nodesList = model.treeData?.nodes.orEmpty()
        val levelnods = getNodesAtLevel(level, currentLevel = 0, nodesList)
        if (level > 0) {
            view.renderecell.levelnods = levelnods
        } else {
            view.renderecell.levelnods = null
        }
        nodesList.orEmpty().firstOrNull()?.bounds?.let { rootBound ->
            view.jInspectImagePan.setDrawRect(levelnods, rootBound)
        }
        refreshTreeNodeForClicked()
    }
    fun showPopupMenu(tree: JTree, e: MouseEvent) {
        val selectedRow = tree.getRowForLocation(e.x, e.y)
        val selectedPath = tree.getPathForLocation(e.x, e.y)

        if (selectedRow != -1 && selectedPath != null) {
            // Get the selected node
            val selectedNode = selectedPath.lastPathComponent as? DefaultMutableTreeNode
            if (selectedNode != null && selectedNode.userObject is Node) {
                val node = selectedNode.userObject as Node

                // Create a table model with key-value pairs
                showNodeInMessages(node, tree)
            }
        }
    }
    private fun getNodesAtLevel(
        level: Int,
        currentLevel: Int = 0,
        nodes: List<Node> = model.treeData?.nodes.orEmpty()
    ): List<Node> {
        // Base case: if no nodes or level is less than 0, return an empty list
        if (level < 0 || nodes.isEmpty()) return emptyList()

        // If we're at the desired level, return the nodes at this level
        if (currentLevel == level) {
            return nodes
        }

        // Otherwise, recursively get nodes at the next level
        val childNodesAtNextLevel = nodes.flatMap { getNodesAtLevel(level, currentLevel + 1, it.childNodes) }
        return childNodesAtNextLevel
    }

    fun scrollToNode(tree: JTree, targetNode: Node?) {
        if (targetNode == null) return
        val root = tree.model.root as DefaultMutableTreeNode
        val path = findNodePath(root, targetNode)

        if (path != null) {
            tree.setSelectionPath(path) // Select the node
            tree.scrollPathToVisible(path) // Scroll to make it visible
        }
    }

    fun findNodePath(node: DefaultMutableTreeNode, targetNode: Node): TreePath? {
        for (i in 0 until node.childCount) {
            val child = node.getChildAt(i) as DefaultMutableTreeNode
            if (child.userObject == targetNode) {
                return TreePath(child.path) // Return the path if found
            }
            val foundPath = findNodePath(child, targetNode)
            if (foundPath != null) {
                return foundPath // Return found path from child
            }
        }
        return null
    }

    private fun refreshTreeNodeForClicked() {
        view.renderecell.clickNode = model.clickNodeRw
        view.treeUI.invalidate()
        view.treeUI.repaint()
    }

    private fun getACtivityFragmentsList(): String {


        val sb = StringBuilder()
        model.currentScreenName?.let { actiName ->
            sb.append(actiName)
            sb.append("\n")
        }
        model.fragmentListSTring?.forEachIndexed { index, fname ->
            //listModel.addElement(fname)
            sb.append(fname)
            if (index != model.fragmentListSTring?.lastIndex) {
                sb.append(", ")
            }
        }
        return sb.toString()

    }

    private fun showPopup(x: Int, y: Int, text: String) {

        // Create a JDialog to show the text in a popup
        val dialog = JDialog()
        dialog.title = "Components"
        dialog.setSize(600, 150)


        // Create a JTextArea for displaying the text in the dialog
        val dialogTextArea = JTextArea(text)
        dialogTextArea.isEditable = false // Make it non-editable
        dialogTextArea.lineWrap = true
        dialogTextArea.wrapStyleWord = true


        // Layout for the dialog
        val panel = JPanel(BorderLayout())
        panel.add(JScrollPane(dialogTextArea), BorderLayout.CENTER)

        dialog.add(panel)


        // Set location of the dialog relative to the main frame
        dialog.setLocationRelativeTo(null)
        dialog.isModal = true // Make it modal so user must close it before returning to main window
        dialog.isVisible = true // Show the dialog
    }

    private fun updateACtivityFragmentsList() {

        view.textviewComp.text = ""
        val sb = StringBuilder()
        model.currentScreenName?.let { actiName ->
            val acName = actiName.split(".").lastOrNull()?.trim() ?: "No activity found."
            sb.append(acName)
            sb.append(", ")

        }
        model.fragmentListSTring?.forEachIndexed { index, fname ->
            //listModel.addElement(fname)
            sb.append(fname)
            if (index != model.fragmentListSTring?.lastIndex) {
                sb.append(", ")
            }
        }
        view.textviewComp.text = sb.toString()

    }

    private fun refreshFragments() {
        Thread {
            if (model.currentScreenName.isNullOrBlank().not()) {
                model.setFragmentListSTring(
                    ADBInspect.getFragmentsList(model.currentScreenName!!).filter { it.isBlank().not() })
            }
            updateACtivityFragmentsList()
        }.start()
    }
}

fun String.getImageSize(): Pair<Int, Int>? {
    return try {
        // Read the image file
        val image: BufferedImage = ImageIO.read(File(this))
        // Get the width and height
        val width = image.width
        val height = image.height
        Pair(width, height)
    } catch (e: Exception) {
        // Handle exceptions, such as file not found or unsupported format
       // println("Error reading image: ${e.message}")
        null
    }
}