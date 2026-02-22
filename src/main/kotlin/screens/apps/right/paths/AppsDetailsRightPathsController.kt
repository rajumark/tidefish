package screens.apps.right.paths

class AppsDetailsRightPathsController(
    private val model: AppsDetailsRightPathsModel,
    private val view: AppsDetailsRightPathsView
) {
    private var currentDeviceId: String? = null
    private var currentPackageName: String? = null

    fun setTarget(deviceId: String, packageName: String) {
        currentDeviceId = deviceId
        currentPackageName = packageName
        attachListeners()
        refresh()
    }

    private fun attachListeners() {
        view.onRefreshClick = {
            refresh()
        }
        view.onDownloadAllClick = {
            downloadAll()
        }
        view.searchField.document.addDocumentListener(object : javax.swing.event.DocumentListener {
            override fun insertUpdate(e: javax.swing.event.DocumentEvent?) = onQueryChanged()
            override fun removeUpdate(e: javax.swing.event.DocumentEvent?) = onQueryChanged()
            override fun changedUpdate(e: javax.swing.event.DocumentEvent?) = onQueryChanged()
        })
    }

    private fun onQueryChanged() {
        val filtered = model.filter(view.searchField.text ?: "")
        view.submitLines(filtered)
    }

    fun refresh() {
        val id = currentDeviceId
        val pkg = currentPackageName
        if (id.isNullOrBlank() || pkg.isNullOrBlank()) return

        Thread {
            val output = ADBPaths.getPackagePaths(id, pkg)
            model.setFromRawOutput(output)
            val filtered = model.filter("")
            view.submitLines(filtered)
        }.start()
    }

    private fun downloadAll() {
        val id = currentDeviceId ?: return
        val pkg = currentPackageName ?: return
        val lines = model.filter(view.searchField.text ?: "")
        if (lines.isEmpty()) return
        Thread {
            view.showLoading("Downloading APKs…")
            val downloadsDir = PlatformPaths.getDownloadsDir()
            val targetDir = PlatformPaths.prepareCleanDir(downloadsDir, pkg)
            lines.forEach { remotePath ->
                try {
                    ADBPaths.pullFile(id, remotePath, targetDir)
                } catch (_: Exception) {}
            }
            try { PlatformPaths.openInFileManager(targetDir) } catch (_: Exception) {}
            view.hideLoading()
        }.start()
    }
}


