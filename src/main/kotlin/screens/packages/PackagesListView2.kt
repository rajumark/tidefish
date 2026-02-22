package screens.packages

import decoreui.applyRoundedCorners
import java.awt.*
import javax.swing.*

class PackagesListView2 : JPanel() {
    val searchField = JTextField(15).apply {
        applyRoundedCorners()
    }
    val countAppsLabel = JLabel("")

    val comboBoxAppType = JComboBox<AppType>(AppType.options()).apply {
        selectedIndex=1
        renderer = ListCellRenderer<AppType> { list, value, index, isSelected, cellHasFocus ->
            val label = JLabel(value?.displayName ?: "Select an option")
            if (isSelected) {
                label.background = Color.LIGHT_GRAY
                label.isOpaque = true
            }
            label
        }
    }

    private val tableModel = PackageTableModel().apply {

    }
    val table = JTable(tableModel).apply {
        rowHeight = 24
        autoResizeMode = JTable.AUTO_RESIZE_OFF
    }

    init {
        layout = BorderLayout()
        border = BorderFactory.createEmptyBorder(0, 0, 0, 0)

        val subbar = JPanel().apply {
            layout = FlowLayout(FlowLayout.LEFT, 4, 0)
            add(searchField)
            add(comboBoxAppType)
            add(countAppsLabel)
        }

        searchField.putClientProperty("JTextField.placeholderText", "Search packages...")
        comboBoxAppType.font = Font("SansSerif", Font.PLAIN, 12)
        searchField.font = Font("SansSerif", Font.PLAIN, 12)

        val scrollPane = JScrollPane(table).apply {
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        }
        scrollPane.preferredSize = Dimension(0, 0)



        add(subbar, BorderLayout.NORTH)
        add(scrollPane, BorderLayout.CENTER)
    }


    fun submitAppsList(originalList: List<PackageModel>) {
        tableModel.setData(originalList)
        countAppsLabel.text = "  ${tableModel.rowCount} items."
        setColumnWidths()

    }

    fun setColumnWidths() {
        table.model?.let {
            val columnCount = table.columnModel.columnCount
            for (i in packageColumnWidthConst.indices) {
                if (i < columnCount) {
                    table.columnModel.getColumn(i).minWidth  = packageColumnWidthConst[i]
                }
            }
        }
    }

}
