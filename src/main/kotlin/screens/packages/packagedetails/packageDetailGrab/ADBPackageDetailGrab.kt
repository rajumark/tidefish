package screens.packages.packagedetails.packageDetailGrab

import adb.ADBConst

object ADBPackageDetailGrab {

    fun dumpPackageInfo(id:String,packageName: String): String {
        return try {
            // Merge the adb command directly inside the function
            val command = "${ADBConst.path} -s $id shell dumpsys package $packageName"

            // Execute the command and capture the output
            val process = Runtime.getRuntime().exec(command)
            val output = process.inputStream.bufferedReader().use { it.readText() }

            // Wait for the process to finish before returning the result
            process.waitFor()

            // Return the command output
            output
        } catch (e: Exception) {
            // Handle the exception, e.g., print the stack trace or return an error message
            "Error executing ADB command: ${e.message}"
        }
    }


    fun getDetailsOfPackage(id:String,targetAPckage: String): List<PackageTitleContentModel> {
        val output = dumpPackageInfo(id,targetAPckage)
        val allLines = output.lines()

// Find the section titles by filtering lines that end with a colon and do not start with a space
        val sectionLines = allLines.filter { it.endsWith(":") && !it.startsWith(" ") }

// Create a regex pattern that matches any of the section titles
        val regex = Regex("(${sectionLines.joinToString("|")})")

// Split the output by the defined section titles (this also removes the section titles)
        val sections: List<String> = output.split(regex).filter { it.isNotBlank() }

       return  sections.mapIndexed { index, section ->
            val sectionTitle = sectionLines.getOrNull(index) ?: "Unknown Section"
            PackageTitleContentModel(
                title = sectionTitle,
                content = section.trim()
            )
        }


    }
}