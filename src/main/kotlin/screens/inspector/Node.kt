package screens.inspector

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root


@Root(name = "node", strict = false)
data class Node(
    @field:Attribute(name = "bounds")
    var bounds: String = "",

    @field:Attribute(name = "checkable")
    var checkable: Boolean = false,

    @field:Attribute(name = "checked")
    var checked: Boolean = false,

    @field:Attribute(name = "CV")
    var className: String = "",

    @field:Attribute(name = "clickable")
    var clickable: Boolean = false,

    @field:Attribute(name = "content-desc")
    var contentDesc: String = "",

    @field:Attribute(name = "enabled")
    var enabled: Boolean = true,

    @field:Attribute(name = "focusable")
    var focusable: Boolean = false,

    @field:Attribute(name = "focused")
    var focused: Boolean = false,

    @field:Attribute(name = "index")
    var index: Int = 0,

    @field:Attribute(name = "long-clickable")
    var longClickable: Boolean = false,

    @field:Attribute(name = "package")
    var packageName: String = "",

    @field:Attribute(name = "password")
    var password: Boolean = false,

    @field:Attribute(name = "resource-id")
    var resourceId: String = "",

    @field:Attribute(name = "scrollable")
    var scrollable: Boolean = false,

    @field:Attribute(name = "selected")
    var selected: Boolean = false,

    @field:Attribute(name = "text")
    var text: String = "",

    @field:ElementList(inline = true, required = false)
    var childNodes: MutableList<Node> = mutableListOf()
) {
    fun toUnique(): String {
        return """
            $bounds + @
            $checkable + @
            $checked + @
            $className + @
            $clickable + @
            $contentDesc + @
            $enabled + @
            $focusable + @
            $focused + @
            $longClickable + @
            $packageName + @
            $password + @
            $resourceId + @
            $scrollable + @
            $selected + @
            $text + @
        """.trimIndent()
    }
    fun isMatch(textSearch: String): Boolean {
        if (textSearch.isBlank()) return false
        if (text.lowercase().contains(textSearch.lowercase())){
            return true
        }
        if (resourceId.lowercase().contains(textSearch.lowercase())){
            return true
        }
        if (className.lowercase().contains(textSearch.lowercase())){
            return true
        }


        return false

    }




}
