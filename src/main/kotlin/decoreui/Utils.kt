package decoreui

import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

fun JTextField.setHint(hint: String) {
    this.putClientProperty("JTextField.placeholderText", hint)
    this.repaint()

    this.addFocusListener(object : java.awt.event.FocusAdapter() {
        override fun focusGained(e: java.awt.event.FocusEvent?) = repaint()
        override fun focusLost(e: java.awt.event.FocusEvent?) = repaint()
    })
}




fun JTextField.onTextChanged(onChange: (String) -> Unit) {
    this.document.addDocumentListener(object : DocumentListener {
        override fun insertUpdate(e: DocumentEvent?) = onChange(this@onTextChanged.text)
        override fun removeUpdate(e: DocumentEvent?) = onChange(this@onTextChanged.text)
        override fun changedUpdate(e: DocumentEvent?) = onChange(this@onTextChanged.text)
    })
}
