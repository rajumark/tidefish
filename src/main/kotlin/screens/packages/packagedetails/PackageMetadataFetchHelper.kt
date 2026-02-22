package screens.packages.packagedetails

import screens.packages.PackageMetadata
import javax.swing.SwingWorker

// Unique key for each package per device
data class PackageKey(val id: String, val packageName: String)

val packageMetadataCache = mutableMapOf<PackageKey, PackageMetadata?>()
val loadingStates = mutableSetOf<PackageKey>()

fun getPackageMetadataModelFromCatch(
    id: String?=null,
    packageName: String?=null,
): PackageMetadata? {
    if(id==null || packageName==null){
        return null
    }
    val key = PackageKey(id, packageName)
    return packageMetadataCache.getOrDefault(key, null)
}

fun isAlreadyFetched(
    id: String?=null,
    packageName: String?=null,
): Boolean {
    if(id==null || packageName==null){
        return false
    }
    val key = PackageKey(id, packageName)
    return packageMetadataCache.containsKey(key)
}

fun getPackageDataInBackground(
    id: String,
    packageName: String,
    forceRefresh: Boolean = false,
    onDone: (modelData: PackageMetadata?) -> Unit
) {
    val key = PackageKey(id, packageName)

    // If not forcing refresh, check and return cached result
    if (!forceRefresh) {
        packageMetadataCache[key]?.let {
            onDone(it)
            return
        }

        // Skip if already loading
        if (key in loadingStates) return
    }

    loadingStates.add(key)

    object : SwingWorker<PackageMetadata?, Void?>() {
        override fun doInBackground(): PackageMetadata? {
            return getPackageMetadataInfoByPackageName(id, packageName)
        }

        override fun done() {
            try {
                val modelData = get()
                if (modelData != null) {
                    packageMetadataCache[key] = modelData
                }
                onDone(modelData)
            } catch (e: Exception) {
                e.printStackTrace()
                onDone(null)
            } finally {
                loadingStates.remove(key)
            }
        }
    }.execute()
}
