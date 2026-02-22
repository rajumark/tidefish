package nav

import adb.ADBHelper.getCurrentVersion
import adb.DeviceModel
import colors.LightColorsConst
import colors.LightColorsConst.color_background_sidemenu
import components.topbar.TopbarView
import components.topbar.TopbarViewController
import components.topbar.TopbarViewModel
import decoreui.applyRoundedSelection
import decoreui.applyStyleSplitPan
import first.navigation.TypeOfScreens
import first.navigation.sideMenuList
import nav.rightpanel.RightSideQuickPanelController
import screens.packages.KeyValueStore
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*
import javax.swing.border.MatteBorder


class NavPageView : JFrame("Tidefish ${getCurrentVersion()}") {
    var hiddenLeftMenu = false
    var hiddenRightQuickPanel = KeyValueStore.get("hiddenRightQuickPanel")?.toBoolean() ?: true
    var lastDividerLocation = 100
    val topbarViewModel: TopbarViewModel by lazy { TopbarViewModel() }
    val topbarView: TopbarView by lazy { TopbarView() }
    val topbarViewController: TopbarViewController by lazy { TopbarViewController(topbarViewModel, topbarView) }

    val rightSideQuickPanelModel: RightSideQuickPanelModel by lazy { RightSideQuickPanelModel() }
    val rightSideQuickPanelView: RightSideQuickPanelView by lazy { RightSideQuickPanelView().apply {
        border = MatteBorder(0, 1, 0, 0, LightColorsConst.color_divider)
    } }
    val rightSideQuickPanelController: RightSideQuickPanelController by lazy {
        RightSideQuickPanelController(
            rightSideQuickPanelModel,
            rightSideQuickPanelView
        )
    }
    val items = sideMenuList.map { it.title }.toList().toTypedArray()
    val list = JList(items).apply {
        border = null
        putClientProperty("JList.isFileList", true)
        background = color_background_sidemenu
        applyRoundedSelection()
    }
    val rightPanel by lazy {
        RightSideContentMain()
    }

    init {
        layout = BorderLayout()
        setSize(1200, 800)
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)

        //topbar view

        val divider = JPanel()
        divider.setBackground(LightColorsConst.color_divider) // your SING color
        divider.setPreferredSize(Dimension(0, 1))

        add(topbarView, BorderLayout.NORTH) // Add TopbarView to the top


        //center part
        // Left panel with JList


        val paddedPanelList = JPanel(BorderLayout()).apply {
            border = BorderFactory.createEmptyBorder(8, 8, 8, 8) // top, left, bottom, right
            add(list, BorderLayout.CENTER)
            background = color_background_sidemenu
        }
        val leftPanel = JScrollPane(paddedPanelList)
        leftPanel.border = null
        leftPanel.background = color_background_sidemenu
        // Right panel with centered text
        // ✅ Add listener for selection



        // SplitPane
        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel)
        splitPane.dividerLocation = lastDividerLocation
        topbarView.onMenuClick = {
            if (hiddenLeftMenu) {
                splitPane.setLeftComponent(leftPanel);
                splitPane.setDividerLocation(lastDividerLocation);
                hiddenLeftMenu = false
            } else {
                lastDividerLocation = splitPane.getDividerLocation();
                splitPane.setLeftComponent(null)
                hiddenLeftMenu = true;
            }
        }
        topbarView.onQuickTilesClick = {
            if (hiddenRightQuickPanel) {
                add(rightSideQuickPanelView, BorderLayout.EAST)
                hiddenRightQuickPanel = false
                KeyValueStore.put("hiddenRightQuickPanel", "false")
            } else {
                remove(rightSideQuickPanelView)
                hiddenRightQuickPanel = true
                KeyValueStore.put("hiddenRightQuickPanel", "true")
            }
            revalidate()
            repaint()
        }
     splitPane.applyStyleSplitPan()

//// Optional: set divider color
//        splitPane.setBackground(Color.GRAY);      // for areas around the divider
//        splitPane.setForeground(Color.DARK_GRAY); // sometimes affects the line
//
//// Optional: remove the default border
//        splitPane.setBorder(null);


        add(splitPane, BorderLayout.CENTER) // Add TopbarView to the top

        // Initialize right panel visibility based on saved state
        if (!hiddenRightQuickPanel) {
            add(rightSideQuickPanelView, BorderLayout.EAST)
        }

        // Restore last selected menu if stored; otherwise select first
        val saved = KeyValueStore.get("last_selected_menu_title")
        val idx = if (saved != null) items.indexOf(saved) else 0
        if (idx >= 0 && list.model.size > idx) {
            list.selectedIndex = idx
            list.ensureIndexIsVisible(idx)
        }

    }

    fun setThisScreen(deviceModel: DeviceModel?, currentScreen: TypeOfScreens) {
        rightPanel.setThisScreen(deviceModel = deviceModel, currentScreen = currentScreen )
        rightSideQuickPanelController.setThisDeviceModel(deviceModel)

    }
}