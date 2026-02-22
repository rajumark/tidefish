
import utils.getIconButton
import java.awt.FlowLayout
import java.awt.Image
import java.awt.Rectangle
import javax.swing.*

class RefreshButtonPanel : JPanel() {
    private val iconButton: JButton
    private var isLoading = false
    private val loadingCircle: JProgressBar

    init {
        layout = FlowLayout(FlowLayout.LEFT)
        iconButton = getIconButton("refresh", 18, 18)  // Create the button with the refresh icon
        iconButton.toolTipText = "Refresh"
        iconButton.addActionListener {
           // println("ADBCard button clicked!")
            setLoading(true)
            // Simulate a long-running task with SwingWorker
            BackgroundTask().execute()
        }
        iconButton.bounds = Rectangle(0, 0, 24, 24) // Set size and avoid layout issues
        add(iconButton)

        // Create the loading circle (JProgressBar)
        loadingCircle = JProgressBar()
        loadingCircle.isIndeterminate = true
        loadingCircle.bounds = Rectangle(0, 0, 24, 24)
        loadingCircle.border = BorderFactory.createEmptyBorder() // Make it circular by removing border
        loadingCircle.isVisible = false // Initially hidden
        add(loadingCircle)
    }

    // Method to toggle between loading and normal states
    private fun setLoading(loading: Boolean) {
        isLoading = loading
        if (isLoading) {
            iconButton.isEnabled = false // Disable the button while loading
            iconButton.isVisible = false // Disable the button while loading
            loadingCircle.isVisible = true // Show loading circle
            iconButton.icon = null // Clear the icon or optionally set a loading icon
        } else {
            iconButton.isEnabled = true // Enable button when loading is finished
            iconButton.isVisible = true // Enable button when loading is finished
            loadingCircle.isVisible = false // Hide loading circle
             iconButton.icon = getIcon("refresh", 18, 18) // Reset to the original icon
        }
    }

    // SwingWorker task to simulate a background task
    inner class BackgroundTask : SwingWorker<Void, Void>() {
        override fun doInBackground(): Void? {
            try {
                Thread.sleep(3000) // Simulate a long task (3 seconds)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            return null
        }

        override fun done() {
            SwingUtilities.invokeLater { setLoading(false) } // Set loading to false when done
        }
    }

    // Helper method to load icon (as per your original method)
    private fun getIcon(name: String, width: Int, height: Int): ImageIcon {
        val icon = ImageIcon(javaClass.getResource("/icons/$name.png"))
        val img = icon.image.getScaledInstance(width, height, Image.SCALE_SMOOTH)
        return ImageIcon(img)
    }
}


