package screens.inspector

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root


@Root(name = "hierarchy", strict = false)
data class Hierarchy(
    @field:Attribute(name = "rotation")
    var rotation: Int = 0,

    @field:ElementList(inline = true)
    var nodes: MutableList<Node> = mutableListOf()
){

}