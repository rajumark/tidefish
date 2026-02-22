package decoreui

import java.awt.Font
import javax.swing.UIManager
import javax.swing.plaf.FontUIResource

object FontUtil {

    /**
     * Applies a global font to all Swing components.
     * If the custom font cannot be loaded, falls back to system Dialog font.
     */
    fun applyInterFont() {
        try {
            // Use Dialog for system fallback (Gujarati supported if installed)
            val globalFont: Font = Font("Dialog", Font.PLAIN, 14)

            val defaults = UIManager.getDefaults()
            for (key in defaults.keys) {
                val value = defaults[key]
                if (value is FontUIResource) {
                    UIManager.put(key, FontUIResource(globalFont))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
