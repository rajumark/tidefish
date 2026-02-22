package screens.apps.right

import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class AppDetailsRightModel {
    companion object {
        const val PROP_selected_package_name = "selected_package_name"
    }

    private val support = PropertyChangeSupport(this)

    var selectedPackageName: String? = null
        private set

    fun setSelectedPackageName(name: String?) {
        val old = selectedPackageName
        selectedPackageName = name
        support.firePropertyChange(PROP_selected_package_name, old, name)
    }

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        support.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        support.removePropertyChangeListener(listener)
    }
}