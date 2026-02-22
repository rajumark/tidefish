package decoreui

import colors.LightColorsConst
import java.awt.Graphics
import javax.swing.JSplitPane
import javax.swing.plaf.basic.BasicSplitPaneDivider
import javax.swing.plaf.basic.BasicSplitPaneUI

fun JSplitPane.applyStyleSplitPan(){
     setUI(object : BasicSplitPaneUI() {
        override fun createDefaultDivider(): BasicSplitPaneDivider {
            return object : BasicSplitPaneDivider(this) {
                override fun paint(g: Graphics) {
                    g.setColor(LightColorsConst.color_divider) // your thin line color
                    g.fillRect(0, 0, getWidth(), getHeight())
                }
            }
        }
    })


 setDividerSize(1); // 1-2 pixels for a thin line
}