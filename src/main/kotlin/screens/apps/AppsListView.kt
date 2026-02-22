package screens.apps

import decoreui.applyStyleSplitPan
import screens.apps.left.AppsListLeftController
import screens.apps.left.AppsListLeftModel
import screens.apps.left.AppsListLeftView
import screens.apps.right.AppDetailsRightController
import screens.apps.right.AppDetailsRightModel
import screens.apps.right.AppDetailsRightView
import screens.apps.right.NoPackageSelected
import java.awt.BorderLayout
import java.awt.CardLayout
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.JSplitPane

class AppsListView : JPanel() {
      val appsListLeftModel by lazy { AppsListLeftModel() }
      val appsListLeftView by lazy { AppsListLeftView() }
      val appsListLeftController by lazy { AppsListLeftController(appsListLeftModel, appsListLeftView) }

      val appDetailsRightModel by lazy { AppDetailsRightModel() }
      val appDetailsRightView by lazy { AppDetailsRightView() }
      val appDetailsRightController by lazy { AppDetailsRightController(appDetailsRightModel, appDetailsRightView) }

      private val rightCardLayout = CardLayout()
      private val rightContainer = JPanel(rightCardLayout)
      private val noSelectionPanel = NoPackageSelected()

    init {
        layout = BorderLayout()
        border = BorderFactory.createEmptyBorder(0, 0, 0, 0)

        rightContainer.add(noSelectionPanel, "no_selection")
        rightContainer.add(appDetailsRightView, "details")
        rightCardLayout.show(rightContainer, "no_selection")

        // Create split pane
        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, appsListLeftView, rightContainer)
        splitPane.applyStyleSplitPan()
        splitPane.setDividerLocation(300) // initial left panel width = 200px

        add(splitPane, BorderLayout.CENTER)

        // Wire selection from left list to right details header

    }

    fun showDetailsPanel(show: Boolean) {
        if (show) {
            rightCardLayout.show(rightContainer, "details")
        } else {
            rightCardLayout.show(rightContainer, "no_selection")
        }
        rightContainer.revalidate()
        rightContainer.repaint()
    }
}
