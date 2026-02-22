package screens.packages

import javax.swing.table.AbstractTableModel

class PackageTableModel : AbstractTableModel() {
    private var data: List<PackageModel> = emptyList()
    fun getPackageAt(rowIndex: Int): PackageModel = data[rowIndex]

    fun setData(newData: List<PackageModel>) {
        if (data.size != newData.size) {
            data = newData
            fireTableDataChanged()
            return
        }

        for (row in newData.indices) {
            val oldModel = data[row]
            val newModel = newData[row]

            if (oldModel != newModel) {
                for (col in 0 until columnCount) {
                    val oldValue = getCellValues(col, oldModel)
                    val newValue = getCellValues(col, newModel)
                    if (oldValue != newValue) {
                        fireTableCellUpdated(row, col)
                    }
                }
            }
        }

        data = newData
    }

    override fun getRowCount(): Int = data.size

    override fun getColumnCount(): Int = packageColumnNamesConst.size

    override fun getColumnName(column: Int): String = packageColumnNamesConst[column]

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val model = data[rowIndex]
        return getCellValues(columnIndex, model)
    }

    private fun getCellValues(columnIndex: Int, model: PackageModel) = when (columnIndex) {
        0 -> if (model.isCurrentApp) "Current" else ""
        1 -> model.packageName
        2 -> model.packageMetadata?.versionCode ?: ""
        3 -> model.packageMetadata?.versionName ?: ""
        4 -> getSdkVersionMeaning(model.packageMetadata?.minSdk ?: "")
        5 -> getSdkVersionMeaning(model.packageMetadata?.targetSdk ?: "")
        6 -> model.packageMetadata?.appId ?: ""
        7 -> model.packageMetadata?.timeStamp ?: ""
        8 -> model.packageMetadata?.lastUpdateTime ?: ""
        9  -> model.packageMetadata?.extractNativeLibs ?: ""
        10 -> model.packageMetadata?.primaryCpuAbi ?: ""
        11 -> model.packageMetadata?.usesNonSdkApi ?: ""
        12 -> model.packageMetadata?.splits ?: ""
        13 -> getApkSigningVersionMeaning(model.packageMetadata?.apkSigningVersion ?: "")
        14 -> model.packageMetadata?.supportsScreens ?: ""
        15 -> model.packageMetadata?.installPermissionsFixed ?: ""
        else -> ""
    }
}