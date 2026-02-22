package screens.packages.packagedetails.basic

import adb.Device
import screens.packages.PackageMetadata
import screens.packages.packagedetails.getPackageMetadataInfoByPackageName

import java.awt.BorderLayout
import java.awt.Color
import javax.swing.*
import javax.swing.border.EmptyBorder

class BasicAppInfoPage : JPanel(BorderLayout()) {

    private var selectedDevice: Device? = null
    private var selectedPackage: String? = null
    private var packageMetadata: PackageMetadata? = null
    private var isLoading = true
    private val noDataLabel: JLabel = JLabel("No any data found yet").apply {
        horizontalAlignment = SwingConstants.CENTER
        verticalAlignment = SwingConstants.CENTER
    }

    init {
        // Initially display the no data found message
        displayNoDataMessage()
    }



    // Function to set selected package
    fun setSelectedPackage(   device: Device,  packageName: String) {
        selectedDevice = device
        selectedPackage = packageName
        fetchData()
    }

    // Function to fetch metadata
    private fun fetchData() {
        // Check if both selectedDevice and selectedPackage are set
        if (selectedDevice == null || selectedPackage.isNullOrEmpty()) {
            return
        }

        try {
            packageMetadata = getPackageMetadataInfoByPackageName(selectedDevice!!.id, selectedPackage!!)
            SwingUtilities.invokeLater {
                // Update UI once data is fetched
                isLoading = false
                removeAll()
                if (packageMetadata != null) {
                    add(createAppInfoPanel(), BorderLayout.CENTER)
                } else {
                    add(createErrorPanel(), BorderLayout.CENTER)
                }
                revalidate()
                repaint()
            }
        } catch (e: Exception) {
            SwingUtilities.invokeLater {
                removeAll()
                add(createErrorPanel(), BorderLayout.CENTER)
                revalidate()
                repaint()
            }
        }
    }

    // Display message when no data is found
    private fun displayNoDataMessage() {
        removeAll()
        add(noDataLabel, BorderLayout.CENTER)
        revalidate()
        repaint()
    }

    private fun createAppInfoPanel(): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.border = EmptyBorder(20, 20, 20, 20)

        val coreInfo = listOf(
            "App ID" to (packageMetadata?.appId ?: "N/A"),
            "Package" to (packageMetadata?.pkg ?: "N/A"),
            "Version Name" to (packageMetadata?.versionName ?: "N/A"),
            "Version Code" to (packageMetadata?.versionCode ?: "N/A"),
            "Min SDK" to (packageMetadata?.minSdk ?: "N/A"),
            "Target SDK" to (packageMetadata?.targetSdk ?: "N/A"),
            "Timestamp" to (packageMetadata?.timeStamp ?: "N/A"),
            "Last Update Time" to (packageMetadata?.lastUpdateTime ?: "N/A"),
        )

        val timeInfo = listOf(
            "Installer Package Name" to (packageMetadata?.installerPackageName ?: "N/A"),
            "Installer Package UID" to (packageMetadata?.installerPackageUid ?: "N/A"),
            "Initiating Package Name" to (packageMetadata?.initiatingPackageName ?: "N/A"),
            "Originating Package Name" to (packageMetadata?.originatingPackageName ?: "N/A"),
            "Update Owner Package Name" to (packageMetadata?.updateOwnerPackageName ?: "N/A"),
            "Package Source" to (packageMetadata?.packageSource ?: "N/A")
        )

        // Add core app info section
        panel.add(createSection("Core App Information", coreInfo))
        // Add installation and update info section
        panel.add(createSection("Installation and Update Details", timeInfo))

        // Add technical details section
        panel.add(createTechnicalDetailsSection())

        return panel
    }

    private fun createSection(title: String, infoItems: List<Pair<String, String>>): JPanel {
        val sectionPanel = JPanel()
        sectionPanel.layout = BoxLayout(sectionPanel, BoxLayout.Y_AXIS)
        sectionPanel.border = EmptyBorder(10, 0, 10, 0)

        val sectionTitle = JLabel(title)
        sectionPanel.add(sectionTitle)

        infoItems.forEach { (label, value) ->
            val rowPanel = JPanel()
            rowPanel.layout = BoxLayout(rowPanel, BoxLayout.X_AXIS)

            val labelComponent = JLabel(label)
            rowPanel.add(labelComponent)

            val valueComponent = JLabel(value)
            rowPanel.add(valueComponent)

            sectionPanel.add(rowPanel)
        }
        return sectionPanel
    }

    private fun createTechnicalDetailsSection(): JPanel {
        val technicalDetailsPanel = JPanel()
        technicalDetailsPanel.layout = BoxLayout(technicalDetailsPanel, BoxLayout.Y_AXIS)

        val properties = listOf(
            "Data Directory" to (packageMetadata?.dataDir ?: "N/A"),
            "Supports Screens" to (packageMetadata?.supportsScreens ?: "N/A"),
            "App Metadata File Path" to (packageMetadata?.appMetadataFilePath ?: "N/A"),
            "Install Permissions Fixed" to (packageMetadata?.installPermissionsFixed ?: "N/A"),
            "Force Queryable" to (packageMetadata?.forceQueryable ?: "N/A"),
            "Extract Native Libs" to (packageMetadata?.extractNativeLibs ?: "N/A"),
            "Primary CPU ABI" to (packageMetadata?.primaryCpuAbi ?: "N/A"),
            "Uses Non-SDK API" to (packageMetadata?.usesNonSdkApi ?: "N/A"),
            "Is MIUI Preinstall" to (packageMetadata?.isMiuiPreinstall ?: "N/A"),
            "Splits" to (packageMetadata?.splits ?: "N/A"),
            "APK Signing Version" to (packageMetadata?.apkSigningVersion ?: "N/A"),
            "Flags" to (packageMetadata?.flags ?: "N/A"),
            "Private Flags" to (packageMetadata?.privateFlags ?: "N/A"),
            "Code Path" to (packageMetadata?.codePath ?: "N/A"),
            "Resource Path" to (packageMetadata?.resourcePath ?: "N/A"),
            "Legacy Native Library Dir" to (packageMetadata?.legacyNativeLibraryDir ?: "N/A"),
            "Queries Packages" to (packageMetadata?.queriesPackages ?: "N/A"),
            "Queries Intents" to (packageMetadata?.queriesIntents ?: "N/A")
        )

        properties.forEach { (label, value) ->
            val rowPanel = JPanel()
            rowPanel.layout = BoxLayout(rowPanel, BoxLayout.X_AXIS)

            val labelComponent = JLabel(label)
            rowPanel.add(labelComponent)

            val valueComponent = JLabel(value)
            rowPanel.add(valueComponent)

            technicalDetailsPanel.add(rowPanel)
        }

        return technicalDetailsPanel
    }

    private fun createErrorPanel(): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        val errorLabel = JLabel("Failed to fetch app metadata.")
        errorLabel.foreground = Color.RED
        panel.add(errorLabel)
        return panel
    }
}
