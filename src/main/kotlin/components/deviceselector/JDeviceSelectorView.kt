package components.deviceselector

import adb.DeviceModel
import adb.DeviceNameShow
import adb.SADBDevices
import colors.LightColorsConst
import decoreui.applyStyleForComboBox
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ItemEvent
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingWorker
import javax.swing.Timer

class JDeviceSelectorView : JPanel() {
    var isDeviceRefreshing = false
    private var currentItems: List<DeviceModel> = listOf()
    private val comboBox: JComboBox<String> = JComboBox()
    private val noDevicesLabel = JLabel("No devices")
    var onItemChange: ((DeviceModel?) -> Unit)? = null

    init {
        layout = FlowLayout(FlowLayout.LEFT)

        // Initial state: no devices
        add(noDevicesLabel)

        comboBox.addItemListener { event ->
            if (event.stateChange == ItemEvent.SELECTED) {
                val selectedItem: String = event.item.toString()
                val selectedDevice = currentItems.firstOrNull {
                    DeviceNameShow.getHumanNameByID(it.id) == selectedItem
                }
                onItemChange?.invoke(selectedDevice?.copy(id = selectedDevice.id))
            }
        }
        comboBox.applyStyleForComboBox()

        setListeners()
        background = LightColorsConst.color_background_sidemenu
    }

    private fun setListeners() {
        refreshDevices()
        startRepeatingTask {
            refreshDevices()
        }
    }

    private fun updateView(items: Array<DeviceModel>) {
        removeAll()
        if (items.isEmpty()) {
            add(noDevicesLabel)
            onItemChange?.invoke(null)
        } else {
            comboBox.removeAllItems()
            for (item in items) {
                val name = DeviceNameShow.getHumanNameByID(item.id)
                comboBox.addItem(name)
            }
            add(comboBox)
        }
        revalidate()
        repaint()
    }

    private fun startRepeatingTask(task: () -> Unit) {
        val timer = Timer(2000) {
            task()
        }
        timer.start()
    }

    fun refreshDevices() {
        if (isDeviceRefreshing) return
        isDeviceRefreshing = true
        val worker = object : SwingWorker<List<DeviceModel>, Void>() {
            override fun doInBackground(): List<DeviceModel> {
                return SADBDevices.getAvailableDevices()
            }

            override fun done() {
                isDeviceRefreshing = false
                try {
                    val newList = get().orEmpty()
                    if (!areSame(newList, currentItems)) {
                        currentItems = newList
                        updateView(currentItems.toTypedArray())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        worker.execute()
    }

    private fun areSame(newList: List<DeviceModel>, currentItems: List<DeviceModel>): Boolean {
        return newList.map { it.id }.toSet() == currentItems.map { it.id }.toSet()
    }
}
