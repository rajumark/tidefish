package screens.packages.packagedetails

import adb.DeviceModel
import screens.packages.packagedetails.packageDetailGrab.ADBPackageDetailGrab.getDetailsOfPackage
import screens.packages.packagedetails.packageDetailGrab.PackageTitleContentModel
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Toolkit
import javax.swing.*

class PackageFullDetailsPage(val selectedDevice: DeviceModel, parent: JFrame, val packageName: String) :
    JDialog(parent, "$packageName", false) {

    private var content: List<PackageTitleContentModel>? = null
    private val menuItems = listOf<String>()
    private val list = JList(menuItems.toTypedArray())
    private val codeViewer = JTextArea()
    private val jscrosll = JScrollPane(codeViewer)
    fun refreshData() {
        if (content.isNullOrEmpty()) return

        val titles = content!!.map { it.title.replace(":","") }
        list.setListData(titles.toTypedArray())

        list.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                val selectedIndex = list.selectedIndex
                seteIndexContent(selectedIndex)
            }
        }
        if (content.orEmpty().isNotEmpty()) {
            list.selectedIndex = 0
            seteIndexContent(0)
        }
    }

    private fun seteIndexContent(selectedIndex: Int) {
        if (selectedIndex != -1 && selectedIndex < content!!.size) {
            val selectedModel = content!![selectedIndex]
            codeViewer.text = selectedModel.content
            SwingUtilities.invokeLater {
                jscrosll.verticalScrollBar.value = 0
            }
        }
    }

    fun fetchData() {
        object : SwingWorker<List<PackageTitleContentModel>, Void>() {
            override fun doInBackground(): List<PackageTitleContentModel> {
                return getDetailsOfPackage(selectedDevice.id, packageName)
            }

            override fun done() {
                try {
                    val content1 = get()
                    content = content1
                    refreshData()
                    // Now you can use 'content' safely here on the Event Dispatch Thread (EDT)
                    // For example: update your UI with the content
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    // Handle errors here
                }
            }
        }.execute()

    }

    init {
        layout = BorderLayout()
        defaultCloseOperation = DISPOSE_ON_CLOSE
        // Get screen size
        val screenSize = Toolkit.getDefaultToolkit().screenSize

        // Set the dialog size to 90% of the screen size
        val width = (screenSize.width * 0.9).toInt()
        val height = (screenSize.height * 0.9).toInt()

        size = Dimension(width, height)
        isModal = false


        // Set up the left list
        list.selectionMode = ListSelectionModel.SINGLE_SELECTION


        // Set up the right text area
        codeViewer.isEditable = false


        // Split pane to divide left and right
        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, list, jscrosll)
        splitPane.dividerLocation = 200

        add(splitPane, BorderLayout.CENTER)
        // Center the dialog on the screen
        setLocationRelativeTo(null)
        isVisible = true

        fetchData()

        codeViewer.text = "Loading..."
    }


    companion object {
        fun showPackageDetails(selectedDevice: DeviceModel, parent: JFrame, title: String) {
            SwingUtilities.invokeLater {
                val page = PackageFullDetailsPage(selectedDevice, parent, title)
                page.isVisible = true
            }
        }
    }
}
