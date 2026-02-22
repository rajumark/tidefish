package screens.packages.packagedetails

import adb.Device
import screens.packages.packagedetails.basic.BasicAppInfoPage
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener


class PackageDetailsPage : JPanel(BorderLayout()) {

    private val menuItems = PackageDetailType.values().map { it.label }
    private val list = JList(menuItems.toTypedArray())
    private val detailPanel = JPanel(BorderLayout())
    private val basicAppInfoPage by lazy {
        BasicAppInfoPage()
    }

    fun setThisData(selectedDevice: Device, selectedPackage: String){
        basicAppInfoPage.setSelectedPackage(selectedDevice,selectedPackage)
    }

    init {
        layout = BorderLayout()

        // Left-side list
        list.selectionMode = ListSelectionModel.SINGLE_SELECTION
        list.selectedIndex = 0 // Select the first item by default
        list.addListSelectionListener(MenuSelectionListener())

        val listScrollPane = JScrollPane(list)
        listScrollPane.preferredSize = Dimension(150, 0)
        add(listScrollPane, BorderLayout.WEST)

        // Right-side panel

        detailPanel.add(basicAppInfoPage, BorderLayout.CENTER)
        add(detailPanel, BorderLayout.CENTER)

        updateDetail(PackageDetailType.BASIC)
    }

    private fun updateDetail(type: PackageDetailType) {
        //switch views here
    }

    private inner class MenuSelectionListener : ListSelectionListener {
        override fun valueChanged(e: ListSelectionEvent) {
            if (!e.valueIsAdjusting) {
                val selected = list.selectedValue
                val type = PackageDetailType.values().first { it.label == selected }
                updateDetail(type)
            }
        }
    }
}