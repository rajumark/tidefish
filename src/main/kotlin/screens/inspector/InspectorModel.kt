package screens.inspector


import adb.DeviceModel
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class InspectorModel {
    companion object {
        val PROP_inspector_list = "inspector_list"
        val PROP_search_text_query_inspector = "search_text_query_inspector"
        val PROP_click_node_inspector = "click_node_inspector"
        val PROP_slider_node_inspector = "slider_node_inspector"
        val PROP_tree_data_inspector = "tree_data_inspector"
        val PROP_current_screen_name_inspector = "current_screen_name"
        val PROP_screnshot_path_inspector = "screnshot_path"
        val PROP_fragment_list_string_inspector = "fragment_list_string_inspector"
        val PROP_ration_image_inspector = "ration_image_inspector"

    }
    private val support = PropertyChangeSupport(this)
    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        support.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        support.removePropertyChangeListener(listener)
    }

    var deviceModel: DeviceModel? = null
        private set

    fun setDeviceModel(deviceModelNew: DeviceModel?) {
        deviceModel = deviceModelNew
    }


    var inspector_list = mutableListOf<MutableMap<String, String?>>()
        private set

    fun setMediaList(list: MutableList<MutableMap<String, String?>>) {
        val oldScreen = inspector_list
        inspector_list = list
        support.firePropertyChange(PROP_inspector_list, oldScreen, list)
    }

    var searchTextQuery: String? = null
        private set

    fun setSearchTextQuery(name: String?) {
        val oldScreen = searchTextQuery
        searchTextQuery = name
        support.firePropertyChange(PROP_search_text_query_inspector, oldScreen, name)
    }
    var clickNodeRw: Node? = null
        private set

    fun setClickNode(name: Node?) {
        val oldScreen = clickNodeRw
        clickNodeRw = name
        support.firePropertyChange(PROP_click_node_inspector, oldScreen, name)
    }

    var sliderNode: Int = 0
        private set

    fun setSliderNode(name: Int) {
        val oldScreen = sliderNode
        sliderNode = name
        support.firePropertyChange(PROP_slider_node_inspector, oldScreen, name)
    }

      var treeData: Hierarchy? = null
          private set

    fun setTreeData(name: Hierarchy?) {
        val oldScreen = treeData
        treeData = name
        support.firePropertyChange(PROP_tree_data_inspector, oldScreen, name)
    }

    var currentScreenName: String? = null
          private set

    fun setCurrentScreenName(name: String?) {
        val oldScreen = currentScreenName
        currentScreenName = name
        support.firePropertyChange(PROP_current_screen_name_inspector, oldScreen, name)
    }


    var screnshotPath: String? = null
          private set

    fun setScrenshotPath(name: String?) {
        val oldScreen = screnshotPath
        screnshotPath = name
        support.firePropertyChange(PROP_screnshot_path_inspector, oldScreen, name)
    }


    var fragmentListSTring: List<String>? = null
          private set

    fun setFragmentListSTring(name: List<String>?) {
        val oldScreen = fragmentListSTring
        fragmentListSTring = name
        support.firePropertyChange(PROP_fragment_list_string_inspector, oldScreen, name)
    }

    var rationImage: Float = 1f
        private set

    fun setRationImage(name: Float) {
        val oldScreen = rationImage
        rationImage = name
        support.firePropertyChange(PROP_ration_image_inspector, oldScreen, name)
    }
}