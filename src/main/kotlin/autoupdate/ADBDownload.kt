package autoupdate


import adb.ZipHelper.unzipFile
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.swing.*
import kotlin.concurrent.thread


object ADBDownload {
    fun showDownloadDialog(parent: JFrame, destinationFile: File) {
        val dialog = JDialog(parent, "Download ADB", true)
        dialog.setSize(450, 230)  // Slightly increased size for better spacing
        dialog.layout = GridBagLayout()

        val gbc = GridBagConstraints()
        gbc.insets = Insets(10, 10, 10, 10) // Uniform spacing around components

        val label = JLabel("One-time process", JLabel.CENTER)
        label.font = Font("Arial", Font.BOLD, 14) // More modern font
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.gridwidth = 2
        dialog.add(label, gbc)

        val progressBar = JProgressBar(0, 20)
        progressBar.isStringPainted = true
        progressBar.preferredSize = Dimension(350, 25) // Wider progress bar for visibility
        gbc.gridy = 1
        dialog.add(progressBar, gbc)
        progressBar.addChangeListener {
            if (progressBar.value == 100) {
                label.text = "Installing..."
            }
        }
        val downloadButton = JButton("Download & Setup")
        downloadButton.preferredSize = Dimension(200, 30) // More prominent button

        downloadButton.addActionListener {
            downloadButton.isEnabled = false
            thread {
                downloadFile(
                    ADBSource.getDownloadSourceURL(),
                    destinationFile,
                    progressBar, dialog
                )
            }
        }

        val panel = JPanel()
        panel.add(downloadButton)
        gbc.gridy = 2
        gbc.gridwidth = 2
        dialog.add(panel, gbc)

        dialog.setLocationRelativeTo(parent)
        dialog.isVisible = true

        dialog.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
               // println("Dialog is closing")
            }
        })
    }


    fun downloadFile(urlString: String, destinationFile: File, progressBar: JProgressBar, dialog: JDialog) {
        try {
            val url = URL(urlString)
            val connection = url.openConnection()
            val totalSize = connection.contentLengthLong

            BufferedInputStream(url.openStream()).use { input ->
                FileOutputStream(destinationFile).use { output ->
                    val dataBuffer = ByteArray(1024)
                    var bytesRead: Int
                    var downloadedSize = 0L

                    while (input.read(dataBuffer, 0, 1024).also { bytesRead = it } != -1) {
                        output.write(dataBuffer, 0, bytesRead)
                        downloadedSize += bytesRead
                        val progress = ((downloadedSize * 100) / totalSize).toInt()
                        SwingUtilities.invokeLater {
                            progressBar.value = progress
                        }
                    }
                }
            }

            unzipFile(destinationFile, destinationFile.parentFile)
            
            // Grant execute permission for ADB on macOS
            if (adb.ADBOS.getOperatingSystem() == adb.OperatingSystem.MAC) {
                try {
                    val adbFile = File(destinationFile.parentFile, "platform-tools/adb")
                    if (adbFile.exists()) {
                        val processBuilder = ProcessBuilder("chmod", "+x", adbFile.absolutePath)
                        val process = processBuilder.start()
                        val exitCode = process.waitFor()
                        if (exitCode != 0) {
                            //println("Warning: Failed to set execute permission on ADB file")
                        }
                    }
                } catch (e: Exception) {
                    //println("Warning: Error setting execute permission: ${e.message}")
                }
            }
            
            SwingUtilities.invokeLater {
                dialog.dispose()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            SwingUtilities.invokeLater {
                JOptionPane.showMessageDialog(null, "Download Failed: ${e.message}", "Error", JOptionPane.ERROR_MESSAGE)
                dialog.dispose()
            }
        }
    }
}
