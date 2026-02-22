package screens.inspector

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import java.awt.Color
import java.awt.Graphics
import java.awt.Image
import java.awt.RenderingHints
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*

class AspectRatioBoxPanel : JPanel() {
    private var canvasRation: Pair<Float, Float>? = null
    private var rationImage: Float = 1f // Aspect ratio (width/height)
    private var screnshotPath: String? = null// Aspect ratio (width/height)
    private var sizeimage: Pair<Int, Int>? = null// Aspect ratio (width/height)
    private val slidedelay = 300
    private var nodesList: List<Node>? = null
    private var rootBound: Rect? = null
    private var clickNode: Node? = null
    var clickNodeListener: ((Node?) -> Unit)? = null


    fun Pair<Pair<Float, Float>, Pair<Float, Float>>.toRect(): Rect {
        return Rect(
            first.first, first.second,
            second.first + first.first, second.second + first.second
        )
    }

    private var listCanvaNodes: List<Pair<Pair<Pair<Float, Float>, Pair<Float, Float>>, Node>>? = null
    private val jpan = JPanel().apply {
        layout = null
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                e?.let {
                    val x: Int = e.x
                    val y: Int = e.y
                    val foundnode = findNodeForClickCoordinate(x, y)
                    clickNode = foundnode
                    clickNodeListener?.invoke(foundnode)
                    when (e.button) {
                        MouseEvent.BUTTON1 -> {

                        }

                        MouseEvent.BUTTON3 -> {
                            foundnode?.let {
                                showNodeInMessages(foundnode, this@apply)
                            }
                        }

                        else -> {}
                    }

                }

            }
        })


    }

    private fun findNodeForClickCoordinate(x: Int, y: Int): Node? {
        val foundnode = listCanvaNodes?.firstOrNull { node ->
            val isFound = node.first.toRect().contains(Offset(x.toFloat(), y.toFloat()))
            isFound
        }
        return foundnode?.second
    }


    // Label to display the image
    // private val imageLabel = JLabel()
    private val imageLabel = object : JLabel() {
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            listCanvaNodes.orEmpty().forEach { canrect ->
                drawRectOn(
                    x = canrect.first.first.first.toInt(),
                    y = canrect.first.first.second.toInt(),
                    width = canrect.first.second.first.toInt() - 1,
                    height = canrect.first.second.second.toInt() - 1,
                    g, Color.WHITE
                )
                drawRectOn(
                    x = canrect.first.first.first.toInt() + 1,
                    y = canrect.first.first.second.toInt() + 1,
                    width = canrect.first.second.first.toInt(),
                    height = canrect.first.second.second.toInt(),
                    g, Color.BLACK
                )
                if (clickNode != null && clickNode == canrect.second) {
                    drawFillRectOn(
                        x = canrect.first.first.first.toInt() + 1,
                        y = canrect.first.first.second.toInt() + 1,
                        width = canrect.first.second.first.toInt(),
                        height = canrect.first.second.second.toInt(),
                        g, color_tree_node_background_clicked
                    )
                }
                if (canrect.second.focused) {
                    drawFillRectOn(
                        x = canrect.first.first.first.toInt() + 1,
                        y = canrect.first.first.second.toInt() + 1,
                        width = canrect.first.second.first.toInt(),
                        height = canrect.first.second.second.toInt(),
                        g, color_tree_node_background_focused
                    )
                }
            }

            /*  nodesList.orEmpty().forEach { node ->
                  //drawRectOn(0, 0, 50, 50, g, Color.GREEN)
                  node.bounds.stringToRectOriginal()?.let { rect ->
                      val canvasecalculate = mapScreenToCanvas(
                          rect.left.toInt(),
                          rect.top.toInt(),
                          rect.width.toInt(),
                          rect.height.toInt(),
                      )
                      canvasecalculate?.let { canrect ->
                          drawRectOn(
                              x = canrect.first.first.toInt(),
                              y = canrect.first.second.toInt(),
                              width = canrect.second.first.toInt() - 1,
                              height = canrect.second.second.toInt() - 1,
                              g, Color.WHITE
                          )
                          drawRectOn(
                              x = canrect.first.first.toInt() + 1,
                              y = canrect.first.second.toInt() + 1,
                              width = canrect.second.first.toInt(),
                              height = canrect.second.second.toInt(),
                              g, Color.BLACK
                          )
                      }
                  }
              }*/
        }
    }

    private fun drawRectOn(x: Int, y: Int, width: Int, height: Int, g: Graphics, black: Color) {
        g.color = black
        g.drawRect(x, y, width, height)
    }

    private fun drawFillRectOn(x: Int, y: Int, width: Int, height: Int, g: Graphics, black: Color) {

        g.color = black
        g.fillRect(x, y, width, height)
    }

    private var resizeTimer: Timer? = null

    init {
        layout = null // Absolute layout for the parent panel
        add(jpan)
        imageLabel.background = Color.CYAN
        jpan.add(imageLabel) // Add the image label to the panel

        // Listen for size changes
        /* addComponentListener(object : ComponentAdapter() {
             override fun componentResized(e: ComponentEvent) {
                 onSizeChanged() // Handle size changes
             }
         })*/
        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                // Cancel the previous timer if it exists
                resizeTimer?.stop()

                // Create a new timer that calls onSizeChanged after 100 ms
                resizeTimer = Timer(slidedelay) {
                    onSizeChanged() // Handle size changes
                }
                resizeTimer?.start()
            }
        })
    }

    private fun onSizeChanged() {
        val parentWidth = width
        val parentHeight = height

        if (parentWidth <= 0 || parentHeight <= 0) return // Ignore invalid sizes

        // Calculate the size of `jpan` based on the aspect ratio
        val targetWidth: Int
        val targetHeight: Int

        if (parentWidth / parentHeight.toFloat() > rationImage) {
            // Parent is wider than the aspect ratio, fit to height
            targetHeight = parentHeight
            targetWidth = (targetHeight * rationImage).toInt()
        } else {
            // Parent is taller than the aspect ratio, fit to width
            targetWidth = parentWidth
            targetHeight = (targetWidth / rationImage).toInt()
        }

        // Center the child panel within the parent
        val x = (parentWidth - targetWidth) / 2
        val y = (parentHeight - targetHeight) / 2

        // Update bounds for `jpan`
        jpan.setBounds(x, y, targetWidth, targetHeight)

        // Resize the image label to fill `jpan`
        imageLabel.setBounds(0, 0, jpan.width, jpan.height)
        //loadImage()
        loadImageSafely()
        revalidate()
        repaint()
    }

    // Function to set the aspect ratio
    fun setBoxAspectRatio(ratio: Float, screnshotPath: String, sizeimage: Pair<Int, Int>) {
        this.rationImage = ratio
        this.screnshotPath = screnshotPath
        this.sizeimage = sizeimage
        onSizeChanged()
        revalidate() // Recalculate layout if necessary
        repaint() // Repaint to update the display
    }

    fun loadImageSafely() {
        val imagePath = screnshotPath ?: return
        val imgSize = sizeimage ?: return
        val panelWidth = jpan.width
        val panelHeight = jpan.height

        if (panelWidth <= 0 || panelHeight <= 0) return

        object : SwingWorker<ImageIcon?, Unit>() {
            override fun doInBackground(): ImageIcon? {
                val originalFile = File(imagePath)
                if (!originalFile.exists()) return null

                val originalImage = ImageIO.read(originalFile) ?: return null

                val imgWidth = imgSize.first
                val imgHeight = imgSize.second

                if (imgWidth <= 0 || imgHeight <= 0) return null

                val imgAspect = imgWidth.toFloat() / imgHeight
                val panelAspect = panelWidth.toFloat() / panelHeight

                val (newWidth, newHeight) = if (panelAspect > imgAspect) {
                    val h = panelHeight
                    val w = (h * imgAspect).toInt()
                    Pair(w, h)
                } else {
                    val w = panelWidth
                    val h = (w / imgAspect).toInt()
                    Pair(w, h)
                }

                val resizedImage = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
                val g2 = resizedImage.createGraphics()
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                g2.drawImage(originalImage, 0, 0, newWidth, newHeight, null)
                g2.dispose()

                return ImageIcon(resizedImage)
            }

            override fun done() {
                try {
                    val scaledIcon = get()
                    if (scaledIcon != null) {
                        imageLabel.icon = scaledIcon

                        val w = scaledIcon.iconWidth
                        val h = scaledIcon.iconHeight

                        imageLabel.setBounds(
                            (panelWidth - w) / 2,
                            (panelHeight - h) / 2,
                            w,
                            h
                        )

                        updateCanvaseration()
                        jpan.repaint()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.execute()
    }


    // Function to load and display an image
    fun loadImage() {


        val imagePath = screnshotPath ?: run {
            //  println("No screenshot path provided.")
            return
        }


        val originalImage = ImageIcon(imagePath).image

        val panelWidth = jpan.width
        val panelHeight = jpan.height


        if (panelWidth > 0 && panelHeight > 0 && sizeimage != null) { // Ensure panel dimensions are valid
            // val imgWidth = originalImage.getWidth(null)
            // val imgHeight = originalImage.getHeight(null)
            val imgWidth = sizeimage!!.first
            val imgHeight = sizeimage!!.second

            // Log image dimensions

            if (imgWidth <= 0 || imgHeight <= 0) {
                return
            }

            // Calculate aspect ratios
            val imgAspectRatio = imgWidth.toFloat() / imgHeight
            val panelAspectRatio = panelWidth.toFloat() / panelHeight


            // Calculate scaled image dimensions while maintaining aspect ratio
            val scaledImage = if (panelAspectRatio > imgAspectRatio) {
                // Panel is wider than image aspect ratio, fit to height
                val newWidth = (panelHeight * imgAspectRatio).toInt()
                originalImage.getScaledInstance(newWidth, panelHeight, Image.SCALE_SMOOTH)
            } else {
                // Panel is taller than image aspect ratio, fit to width
                val newHeight = (panelWidth / imgAspectRatio).toInt()
                originalImage.getScaledInstance(panelWidth, newHeight, Image.SCALE_SMOOTH)
            }

            // Set the icon for the label and center it in the panel
            imageLabel.icon = ImageIcon(scaledImage)

            // Get scaled dimensions for centering
            val scaledWidth = scaledImage.getWidth(null)
            val scaledHeight = scaledImage.getHeight(null)


            // Centering the image label in the panel
            imageLabel.setBounds(
                (panelWidth - scaledWidth) / 2,
                (panelHeight - scaledHeight) / 2,
                scaledWidth,
                scaledHeight
            )

            updateCanvaseration()
            jpan.repaint() // Repaint the panel to show updated content
        } else {
        }

    }

    fun setDrawRect(nodesList: List<Node>, bounds: String) {
        clickNode = null
        rootBound = bounds.stringToRectOriginal()
        this.nodesList = nodesList
        updateCanvaseration()

        imageLabel.repaint()
    }

    private fun buildCanvaseRect() {
        listCanvaNodes = nodesList.orEmpty().mapNotNull { node ->
            val bounrd = node.bounds.stringToRectOriginal()
            if (bounrd != null) {
                val nodecan = mapScreenToCanvas(
                    bounrd.left.toInt(),
                    bounrd.top.toInt(),
                    bounrd.width.toInt(),
                    bounrd.height.toInt(),
                )
                if (nodecan != null) {
                    nodecan to node
                } else {
                    null
                }
            } else {
                null
            }
        }
    }

    private fun updateCanvaseration() {
        if (rootBound == null) return
        canvasRation = getCanvasScreenRatio(jpan.width, jpan.height, rootBound!!.width, rootBound!!.height)
        buildCanvaseRect()
    }

    fun getCanvasScreenRatio(
        canvasWidth: Int,
        canvasHeight: Int,
        screenWidth: Float,
        screenHeight: Float
    ): Pair<Float, Float> {
        val widthRatio = canvasWidth / screenWidth.toFloat()  // Width ratio (canvas to screen)
        val heightRatio = canvasHeight / screenHeight.toFloat()  // Height ratio (canvas to screen)
        return Pair(widthRatio, heightRatio)
    }

    fun mapScreenToCanvasOne(
        screenX: Int,
        screenY: Int,
    ): Pair<Float, Float>? {
        canvasRation?.let { itcanvasRation ->
            // Get the canvas-to-screen ratio
            val (canvasWidthRatio, canvasHeightRatio) = itcanvasRation

            // Map the screen coordinates to canvas coordinates
            val mappedX = screenX * canvasWidthRatio
            val mappedY = screenY * canvasHeightRatio

            return Pair(mappedX, mappedY)
        }
        return null
    }

    fun mapScreenToCanvas(
        screenX1: Int, screenY1: Int,  // First point
        screenX2: Int, screenY2: Int   // Second point
    ): Pair<Pair<Float, Float>, Pair<Float, Float>>? {
        canvasRation?.let { itcanvasRation ->
            // Get the canvas-to-screen ratio
            val (canvasWidthRatio, canvasHeightRatio) = itcanvasRation

            // Map the first point (screenX1, screenY1) to canvas coordinates
            val mappedX1 = screenX1 * canvasWidthRatio
            val mappedY1 = screenY1 * canvasHeightRatio

            // Map the second point (screenX2, screenY2) to canvas coordinates
            val mappedX2 = screenX2 * canvasWidthRatio
            val mappedY2 = screenY2 * canvasHeightRatio

            // Return both pairs of coordinates as a pair of pairs
            return Pair(Pair(mappedX1, mappedY1), Pair(mappedX2, mappedY2))
        }
        return null
    }
}


fun String.stringToRectOriginal(): Rect? {
    val pattern = Regex("""\[(\d+),(\d+)\]\[(\d+),(\d+)\]""")
    val matchResult = pattern.find(this)

    if (matchResult != null) {
        val groups = matchResult.groups
        val left = groups[1]?.value?.toInt() ?: 0
        val top = groups[2]?.value?.toInt() ?: 0
        val right = groups[3]?.value?.toInt() ?: 0
        val bottom = groups[4]?.value?.toInt() ?: 0
        return Rect(
            left.toFloat(),
            top.toFloat(),
            right.toFloat(),
            bottom.toFloat()
        )
    } else {
        // Handle invalid format (e.g., return default Rect)
        return null
    }
}
