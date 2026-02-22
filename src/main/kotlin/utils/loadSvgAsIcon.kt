package utils

import first.menu.isDarkTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.batik.transcoder.SVGAbstractTranscoder
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.ImageTranscoder
import java.awt.image.BufferedImage
import java.beans.PropertyChangeListener
import java.io.InputStream
import javax.swing.ImageIcon
import javax.swing.JButton

fun  loadSvgAsIconAsync(path: String, width: Int, height: Int, callback: (ImageIcon) -> Unit) {
    GlobalScope.launch(Dispatchers.IO) {
        val inputStream: InputStream = javaClass.getResourceAsStream(path)
            ?: error("SVG file not found at: $path")

        val input = TranscoderInput(inputStream)
        val image = arrayOfNulls<BufferedImage>(1)

        val transcoder = object : ImageTranscoder() {
            override fun createImage(w: Int, h: Int): BufferedImage {
                return BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
            }

            override fun writeImage(img: BufferedImage, output: TranscoderOutput?) {
                image[0] = img
            }
        }

        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, width.toFloat())
        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, height.toFloat())
        transcoder.transcode(input, null)

        // Return to the UI thread to update the button icon
        withContext(Dispatchers.Main) {
            callback(ImageIcon(image[0]))
        }
    }
}

fun  getIconButton(nameOfIconOnly: String, width: Int = 18, height: Int = 18): JButton {
    val extraName = if (isDarkTheme()) "_dark" else ""
    val button = JButton()

    loadSvgAsIconAsync("/$nameOfIconOnly$extraName.svg", width, height) { icon ->
        button.icon = icon
    }

    button.addPropertyChangeListener("background", PropertyChangeListener { event ->
        val oldColor = event.oldValue as? java.awt.Color
        val newColor = event.newValue as? java.awt.Color

        if (oldColor != newColor) {
            val updatedIconName = if (isDarkTheme()) "_dark" else ""
            loadSvgAsIconAsync("/$nameOfIconOnly$updatedIconName.svg", width, height) { icon ->
                button.icon = icon
            }
        }
    })

    return button
}

