package screens.inspector


import java.awt.BorderLayout
import javax.swing.JPanel

class JInspectImagePan : JPanel() {

    val aspectRatioBoxPanel = AspectRatioBoxPanel()
    init {
        layout = BorderLayout() // Set layout manager


        add(aspectRatioBoxPanel, BorderLayout.CENTER) // Add the aspect ratio panel to the center


    }
fun setClickListenerNode(listener:((Node?)->Unit)?){
    aspectRatioBoxPanel.clickNodeListener=listener
}
    fun setRationSize(rationImage: Float, screnshotPath: String, sizeimage: Pair<Int, Int>) {
        aspectRatioBoxPanel.setBoxAspectRatio(rationImage,screnshotPath,sizeimage)
    }

    fun setDrawRect(nodesList: List<Node>, bounds: String) {
        aspectRatioBoxPanel.setDrawRect(nodesList,bounds)

    }
}


