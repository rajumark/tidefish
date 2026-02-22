package screens.inspector

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Panel
import javax.swing.*

class InspectorView : Panel() {
    val refreshButton by lazy {
        JButton("Refresh").apply {
            preferredSize = Dimension(100, 28)

        }
    }
    val textviewComp = JTextArea("").apply {
        setEditable(false);
        setLineWrap(false);
        setWrapStyleWord(false);

    }

    val jpanbox by lazy {
        JPanel().apply {
            layout = BorderLayout()
            add(refreshButton, BorderLayout.WEST)
            add(slider, BorderLayout.EAST)
            val scrollPane = JScrollPane(textviewComp)
            scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_NEVER
            add(scrollPane, BorderLayout.CENTER)
        }
    }

    val slider by lazy {
        JSlider(
            JSlider.HORIZONTAL,
            0,
            20,
            0
        ).apply {
            preferredSize = Dimension(300, 0)
            // min = 0, max = 100, initial value = 50
            majorTickSpacing = 1  // Major ticks every 10
            minorTickSpacing = 1  // Minor ticks every 1
            paintTicks = false      // Show ticks
            paintLabels = false    // Show labels
        }

    }
    val renderecell = MyTreeCellRenderer()
    val treeUI by lazy {
        JTree().apply {
            cellRenderer = renderecell
        }
    }
    val jScrollPane by lazy {
        JScrollPane(treeUI)
    }


    val leftPanel by lazy {
        JPanel().apply {
            layout = BorderLayout()
            add(jScrollPane, java.awt.BorderLayout.CENTER)
        }
    }
    val jInspectImagePan by lazy {
        JInspectImagePan().apply {

        }
    }
    val rightPanel by lazy {
        JPanel().apply {
            layout = BorderLayout()

            add(jInspectImagePan, BorderLayout.CENTER)

        }
    }


    private val splitPane by lazy {
        JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel).apply {
            dividerSize = 3
            resizeWeight = 0.65
            isContinuousLayout = true
        }
    }

    init {
        layout = BorderLayout()
        add(jpanbox, BorderLayout.NORTH)
        add(splitPane, BorderLayout.CENTER)

    }
}