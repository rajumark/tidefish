//package screens.packages
//
//import screens.packages.packagedetails.PackageDetailsPage
//import java.awt.*
//import javax.swing.*
//
//class PackagesListView : JPanel() {
//    val searchField = JTextField(15)
//    val countAppsLabel = JLabel("")
//
//    val comboBoxAppType = JComboBox<AppType>(AppType.options()).apply {
//        renderer = ListCellRenderer<AppType> { list, value, index, isSelected, cellHasFocus ->
//            val label = JLabel(value?.displayName ?: "Select an option")
//            if (isSelected) {
//                label.background = Color.LIGHT_GRAY
//                label.isOpaque = true
//            }
//            label
//        }
//    }
//
//    val rightPanel by lazy {
//        PackageDetailsPage()
//    }
//
//    private val listModel = DefaultListModel<PackageModel>()
//    val itemList = JList<PackageModel>(listModel).apply {
//        selectionMode = ListSelectionModel.SINGLE_SELECTION
//        fixedCellHeight = 24
//    }
//
//    init {
//        layout = BorderLayout()
//        border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
//
//        // Top search and filter bar
//        val subbar = JPanel().apply {
//            layout = FlowLayout(FlowLayout.LEFT, 4, 0)
//            add(searchField)
//            add(comboBoxAppType)
//            add(countAppsLabel)
//        }
//
//        searchField.putClientProperty("JTextField.placeholderText", "Search packages...")
//        comboBoxAppType.font = Font("SansSerif", Font.PLAIN, 12)
//        searchField.font = Font("SansSerif", Font.PLAIN, 12)
//
//        // Left panel for list view
//        val scrollPane = JScrollPane(itemList)
//        scrollPane.preferredSize = Dimension(0, 0)
//
//        val leftPanel = JPanel().apply {
//            layout = BorderLayout()
//            add(subbar, BorderLayout.NORTH)
//            add(scrollPane, BorderLayout.CENTER)
//        }
//
//        // Split pane layout
//        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel).apply {
//            dividerLocation = 400
//        }
//
//        add(splitPane, BorderLayout.CENTER)
//    }
//
//    fun submitAppsList(originalList: List<PackageModel>) {
//        listModel.clear()
//        originalList.forEach { model ->
//            listModel.addElement(model)
//        }
//        countAppsLabel.text = "  ${listModel.size()} items."
//    }
//
//
//}
