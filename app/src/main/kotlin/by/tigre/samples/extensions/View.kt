package by.tigre.samples.extensions

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes

typealias AndroidView = View
typealias AndroidImageView = ImageView
typealias AndroidTextView = TextView

// /
// / Helpful extension functions for Views manipulation
// /

@Suppress("UNCHECKED_CAST")
fun <T : View> View.find(@IdRes id: Int): T = this.findViewById(id)

@Suppress("UNCHECKED_CAST")
fun <T : View> Activity.find(@IdRes id: Int): T = this.findViewById(id)

fun ViewGroup.inflate(@LayoutRes resource: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(this.context).inflate(resource, this, attachToRoot)

/** Make `View` VISIBLE if it's not visible yet */
fun View.show() {
    if (this.visibility != View.VISIBLE) {
        this.visibility = View.VISIBLE
    }
}

/** Make `View` GONE if it's visible */
fun View.hide() {
    if (this.visibility != View.GONE) {
        this.visibility = View.GONE
    }
}

/** Set View visibility */
fun View.setVisible(visible: Boolean) {
    if (visible) show() else hide()
}

fun View.isVisible(): Boolean = visibility == View.VISIBLE

fun EditText.text() = text.toString()

fun View.showKeyboard() {
    requestFocus()
    inputMethodManager().showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    inputMethodManager().hideSoftInputFromWindow(windowToken, 0)
    clearFocus()
}

private fun View.inputMethodManager() =
    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
