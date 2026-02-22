package screens.media

import adb.DeviceModel
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class JMediaModel {
    companion object {
        val PROP_media_list = "media_list"
        val PROP_search_text_query_media = "search_text_query_media"
        val PROP_show_original_media = "show_original_media"
        val PROP_sourcetype_media = "sourcetype_media"
        val PROP_contenttype_media = "contenttype_media"


    }
    var deviceModel: DeviceModel? = null
        private set

    fun setDeviceModel(deviceModelNew: DeviceModel?) {
        deviceModel = deviceModelNew
    }

    private val support = PropertyChangeSupport(this)

    var media_list = mutableListOf<MutableMap<String, String?>>()
        private set

    fun setMediaList(list: MutableList<MutableMap<String, String?>>) {
        val oldScreen = media_list
        media_list = list
        support.firePropertyChange(PROP_media_list, oldScreen, list)
    }

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        support.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        support.removePropertyChangeListener(listener)
    }


    var searchTextQuery: String? = null
        private set

    fun setSearchTextQuery(name: String?) {
        val oldScreen = searchTextQuery
        searchTextQuery = name
        support.firePropertyChange(PROP_search_text_query_media, oldScreen, name)
    }

    var showOriginal: Boolean = false
        private set

    fun setShowOriginal(name:Boolean) {
        val oldScreen = showOriginal
        showOriginal = name
        support.firePropertyChange( PROP_show_original_media, oldScreen, name)
    }

    var sourceType: MediaSourceType = MediaSourceType.External
        private set

    fun setSourceType(name:MediaSourceType) {
        val oldScreen = sourceType
        sourceType = name
        support.firePropertyChange( PROP_sourcetype_media, oldScreen, name)
    }

    var contentType: MediaContentType = MediaContentType.Images
        private set

    fun setContentType(name:MediaContentType) {
        val oldScreen = contentType
        contentType = name
        support.firePropertyChange( PROP_contenttype_media, oldScreen, name)
    }

}