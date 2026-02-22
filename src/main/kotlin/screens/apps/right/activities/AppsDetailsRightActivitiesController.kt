package screens.apps.right.activities

import screens.packages.getTopLevelSections
import javax.swing.SwingUtilities

class AppsDetailsRightActivitiesController(
    private val model: AppsDetailsRightActivitiesModel,
    private val view: AppsDetailsRightActivitiesView
) {
    private var currentDeviceId: String? = null
    private var currentPackageName: String? = null

    fun setTarget(deviceId: String, packageName: String) {
        currentDeviceId = deviceId
        currentPackageName = packageName
        reload()
    }

    init {
        view.searchField.addKeyListener(object : java.awt.event.KeyAdapter() {
            override fun keyReleased(e: java.awt.event.KeyEvent?) {
                model.filter(view.searchField.text)
                view.setTabs(model.getFilteredKeys())
                selectFirstTabAndUpdate()
            }
        })

        view.onRefreshClick = {
            reload()
        }

        view.setOnTabChange { _, title ->
            val text = model.getSectionText(title)
            view.setText(text)
        }
    }

    fun reload() {
        val id = currentDeviceId ?: return
        val pkg = currentPackageName ?: return

        Thread {
            val sections = getTopLevelSections(id, pkg)
            model.setSections(sections)
            model.filter(view.searchField.text)
            SwingUtilities.invokeLater {
                view.setTabs(model.getFilteredKeys())
                selectFirstTabAndUpdate()
            }
        }.start()
    }

    private fun selectFirstTabAndUpdate() {
        val keys = model.getFilteredKeys()
        if (keys.isNotEmpty()) {
            val first = keys.first()
            val text = model.getSectionText(first)
            view.setText(text)
        } else {
            view.setText("")
        }
    }
}


