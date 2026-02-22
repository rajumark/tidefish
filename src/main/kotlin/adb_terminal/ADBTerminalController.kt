package adb_terminal

import adb.DeviceModel
import screens.packages.KeyValueStore
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.JFileChooser
import javax.swing.SwingWorker

class ADBTerminalController(private val model: ADBTerminalModel, private val view: ADBTerminalView) {
    private val historyKey = "adb_terminal_history"
    private val maxHistory = 50

    init {
        model.addPropertyChangeListener { event ->
            when (event.propertyName) {
                ADBTerminalModel.PROP_HISTORY -> view.setHistoryItems(model.commandHistory)
                ADBTerminalModel.PROP_OUTPUT -> {
                    val text = event.newValue as String
                    view.clearOutput()
                    view.appendOutput(text, model.autoScrollEnabled)
                }
                ADBTerminalModel.PROP_IS_RUNNING -> view.setRunning(model.isRunning)
                ADBTerminalModel.PROP_AUTOSCROLL -> view.setAutoScroll(model.autoScrollEnabled)
            }
        }
        setupViewListeners()
        loadHistory()
        syncDeviceLabel()
    }

    fun setDeviceModel(deviceModel: DeviceModel?) {
        if (model.deviceModel != deviceModel) {
            model.setDeviceModel(deviceModel)
            syncDeviceLabel()
        }
    }

    private fun syncDeviceLabel() {
        view.setDeviceIdLabel(model.deviceModel?.id)
    }

    private fun setupViewListeners() {
        view.onSendClick = { executeCommandFromField() }
        view.onClearOutputClick = { model.clearOutput() }
        view.onHistorySelected = { cmd -> view.commandField.text = cmd }
        view.onToggleAutoscroll = { enabled -> model.setAutoScroll(enabled) }
        view.onSearchChanged = { /* highlighting could be implemented if needed */ }
        view.onSearchNext = { findNext() }
        view.onSearchPrev = { findPrev() }
        view.onSaveToFile = { saveOutputToFile() }
        view.onFilterChanged = { filter -> model.setFilter(filter) ; applyFilter() }
    }

    private fun executeCommandFromField() {
        val deviceId = model.deviceModel?.id ?: return
        val userCmd = view.commandField.text.trim()
        if (userCmd.isEmpty()) return
        addToHistory(userCmd)
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        object : SwingWorker<String, Void>() {
            override fun doInBackground(): String {
                model.setRunning(true)
                val res = ADBTerminalHelper.runUserCommand(deviceId, userCmd)
                val header = "[$timestamp] $ ${userCmd}\n"
                return header + res.output
            }

            override fun done() {
                val res = get()
                model.setRunning(false)
                model.appendOutput(res + "\n")
                applyFilter()
            }
        }.execute()
    }

    private fun addToHistory(cmd: String) {
        val list = model.commandHistory.toMutableList()
        if (list.firstOrNull() != cmd) {
            list.remove(cmd)
            list.add(0, cmd)
        }
        while (list.size > maxHistory) list.removeLast()
        model.setHistory(list)
        persistHistory(list)
    }

    private fun persistHistory(list: List<String>) {
        KeyValueStore.put(historyKey, list.joinToString("\u0001"))
    }

    private fun loadHistory() {
        val raw = KeyValueStore.get(historyKey)
        if (!raw.isNullOrEmpty()) {
            val list = raw.split("\u0001").filter { it.isNotBlank() }
            model.setHistory(list)
        }
    }

    private fun applyFilter() {
        val filter = model.filterText
        val source = model.outputText
        if (filter.isNullOrBlank()) {
            view.clearOutput()
            view.appendOutput(source, model.autoScrollEnabled)
        } else {
            val filtered = source.lines().filter { it.contains(filter, ignoreCase = true) }.joinToString("\n")
            view.clearOutput()
            view.appendOutput(filtered + if (filtered.endsWith("\n")) "" else "\n", model.autoScrollEnabled)
        }
    }

    private fun findNext() {
        val q = view.searchField.text
        if (q.isNullOrEmpty()) return
        val text = view.outputArea.text
        val start = view.outputArea.caretPosition
        val idx = text.indexOf(q, start, ignoreCase = true).takeIf { it >= 0 }
            ?: text.indexOf(q, 0, ignoreCase = true)
        if (idx >= 0) {
            view.outputArea.requestFocus()
            view.outputArea.selectionStart = idx
            view.outputArea.selectionEnd = idx + q.length
            view.outputArea.caretPosition = idx + q.length
        }
    }

    private fun findPrev() {
        val q = view.searchField.text
        if (q.isNullOrEmpty()) return
        val text = view.outputArea.text
        val start = (view.outputArea.selectionStart - 1).coerceAtLeast(0)
        val idx = text.lastIndexOf(q, start, ignoreCase = true).takeIf { it >= 0 }
            ?: text.lastIndexOf(q, text.length - 1, ignoreCase = true)
        if (idx >= 0) {
            view.outputArea.requestFocus()
            view.outputArea.selectionStart = idx
            view.outputArea.selectionEnd = idx + q.length
            view.outputArea.caretPosition = idx
        }
    }

    private fun saveOutputToFile() {
        val chooser = JFileChooser()
        chooser.selectedFile = File("adb_terminal_${System.currentTimeMillis()}.log")
        val result = chooser.showSaveDialog(view)
        if (result == JFileChooser.APPROVE_OPTION) {
            val file = chooser.selectedFile
            file.writeText(view.outputArea.text)
        }
    }
}


