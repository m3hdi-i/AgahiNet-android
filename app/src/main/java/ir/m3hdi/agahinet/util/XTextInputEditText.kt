package ir.m3hdi.agahinet.util

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import com.google.android.material.textfield.TextInputEditText

/**
 * A custom [TextInputEditText] that when typing in it and closing the keyborad with system back key,
 * hides the keyboard and clears the focus of itself.
 * Used for designing a Search Box.
 *
 */
class XTextInputEditText : TextInputEditText
{
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // User has pressed Back key. So hide the keyboard and clear focus of editText
            val mgr = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            mgr.hideSoftInputFromWindow(this.windowToken, 0)
            clearFocus()
            return true
        }
        return false
    }
}