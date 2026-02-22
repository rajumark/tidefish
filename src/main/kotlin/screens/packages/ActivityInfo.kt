package screens.packages

data class ActivityInfo(val name: String, val actions: MutableList<String>, val categories: MutableList<String>){
    fun isDefaultActivity(): Boolean {
        return categories.contains("android.intent.category.DEFAULT") && categories.contains("android.intent.category.LAUNCHER")
    }

    fun isAppActivity(packageName: String): Boolean {
        return name.contains(packageName)
    }
}