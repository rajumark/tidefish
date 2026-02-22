package theme

import com.formdev.flatlaf.FlatIntelliJLaf
import com.formdev.flatlaf.FlatLightLaf
import com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightIJTheme
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterIJTheme
import com.formdev.flatlaf.themes.FlatMacDarkLaf
import com.formdev.flatlaf.themes.FlatMacLightLaf
import decoreui.FontUtil.applyInterFont
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.UIManager
import javax.swing.UnsupportedLookAndFeelException

object FlatLafHelper {
    fun initTheme(isLight:Boolean=true){
        try {
            if (isLight) {
                UIManager.setLookAndFeel(FlatLightLaf())
                // Override background after LAF is set
                UIManager.put("Panel.background", java.awt.Color.WHITE)
                UIManager.put("SplitPane.background", java.awt.Color.WHITE)
                UIManager.put("ComboBox.buttonBackground", java.awt.Color(0, 0, 0, 0))          // transparent
                UIManager.put("ComboBox.buttonEditableBackground", java.awt.Color(0, 0, 0, 0))  // transparent when editable
            } else {
                UIManager.setLookAndFeel(FlatMacDarkLaf())
                UIManager.put("Panel.background", java.awt.Color.BLACK)
                UIManager.put("SplitPane.background", java.awt.Color.BLACK)
            }
            applyInterFont()
        } catch (ex: ClassNotFoundException) {
            Logger.getLogger(FlatLafHelper::class.java.getName()).log(Level.SEVERE, null, ex)
        } catch (ex: InstantiationException) {
            Logger.getLogger(FlatLafHelper::class.java.getName()).log(Level.SEVERE, null, ex)
        } catch (ex: IllegalAccessException) {
            Logger.getLogger(FlatLafHelper::class.java.getName()).log(Level.SEVERE, null, ex)
        } catch (ex: UnsupportedLookAndFeelException) {
            Logger.getLogger(FlatLafHelper::class.java.getName()).log(Level.SEVERE, null, ex)
        }
    }
}