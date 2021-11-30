package by.tigre.samples.presentation.base

import android.widget.DatePicker
import android.widget.EditText
import android.widget.RadioGroup
import androidx.annotation.ColorRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import by.tigre.samples.R
import by.tigre.samples.extensions.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding4.appcompat.itemClicks
import com.jakewharton.rxbinding4.appcompat.navigationClicks
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.checked
import com.jakewharton.rxbinding4.widget.checkedChanges
import com.jakewharton.rxbinding4.widget.editorActions
import com.jakewharton.rxbinding4.widget.textChanges
import com.squareup.picasso.Picasso
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer
import java.util.*

interface ButtonAction {
    val action: Observable<Unit>
}

interface View {
    fun setVisibility(visible: Boolean)

    class Impl(private val view: AndroidView) : View {
        override fun setVisibility(visible: Boolean) = view.setVisible(visible)
    }
}

interface ButtonView : TextView, ButtonAction {
    class Impl(private val view: AndroidTextView) : ButtonView, TextView by TextView.Impl(view) {
        override val action: Observable<Unit> = view.clicks()
    }
}

interface FloatingButtonView : View, ButtonAction {
    class Impl(private val view: FloatingActionButton) : FloatingButtonView, View by View.Impl(view) {
        override val action: Observable<Unit> = view.clicks()
    }
}

interface TextView : View {
    fun setText(text: String?)
    fun setColor(@ColorRes color: Int)

    class Impl(private val view: AndroidTextView) : TextView,
        View by View.Impl(view) {
        override fun setText(text: String?) {
            view.text = text
        }

        override fun setColor(@ColorRes color: Int) {
            view.setTextColor(view.resources.getColor(color, null))
        }
    }
}

interface ImageView {
    fun loadImage(url: String?)

    class Impl(private val view: AndroidImageView) : ImageView {
        override fun loadImage(url: String?) {
            Picasso.get()
                .load(if (url.isNullOrBlank()) null else url)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(view)
        }
    }
}

interface InputFieldView : EditTextView {

    class Impl(private val view: TextInputLayout) : InputFieldView,
        EditTextView by EditTextView.Impl(view.editText!!) {

        override fun setVisibility(visible: Boolean) = view.setVisible(visible)

        override fun showKeyboard() = view.showKeyboard()
        override fun hideKeyboard() = view.hideKeyboard()
    }
}

interface EditTextView : View {
    val textChanges: Observable<String>
    val editorActions: Observable<Int>

    fun setText(text: String?)
    fun getText(): String
    fun showKeyboard()
    fun hideKeyboard()
    fun clearText()

    class Impl(private val view: EditText) : EditTextView,
        View by View.Impl(view) {

        override val textChanges: Observable<String> =
            view.textChanges().map(CharSequence::toString).share()

        override val editorActions: Observable<Int> = view.editorActions()

        override fun setText(text: String?) {
            view.setText(text)
            view.setSelection(view.text.length)
        }

        override fun getText(): String = view.text()
        override fun clearText() = view.setText("")

        override fun showKeyboard() = view.showKeyboard()
        override fun hideKeyboard() = view.hideKeyboard()
    }
}

interface RadioGroupView : View {
    val checkedChanges: Observable<Int>
    val checked: Consumer<in Int>

    class Impl(private val view: RadioGroup) : RadioGroupView, View by View.Impl(view) {
        override val checkedChanges: Observable<Int> = view.checkedChanges()
        override val checked: Consumer<in Int> = view.checked()

    }
}

interface ToolbarView : View {
    val navigationActionClicked: Observable<Unit>
    val menuItemSelected: Observable<Int>
    fun setTitle(title: String?)
    fun showMenu(@MenuRes menuId: Int)

    class Impl(private val view: Toolbar) : ToolbarView, View by View.Impl(view) {

        override val menuItemSelected: Observable<Int> = view.itemClicks().map { it.itemId }.share()
        override val navigationActionClicked: Observable<Unit> = view.navigationClicks()

        override fun showMenu(@MenuRes menuId: Int) {
            view.inflateMenu(menuId)
        }

        override fun setTitle(title: String?) {
            view.title = title
        }
    }
}

interface DatePickerView : View {

    val onDateSelected: Observable<Long>
    val date: Consumer<Long>

    class Impl(
        private val view: DatePicker,
    ) : DatePickerView, View by View.Impl(view) {

        init {
        }

        override val onDateSelected: Observable<Long> = Observable.create { emitter ->

            view.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
                Calendar.getInstance().apply {
                    set(year, monthOfYear, dayOfMonth)
                    emitter.onNext(timeInMillis)
                }
            }

            emitter.setCancellable { view.setOnDateChangedListener(null) }
        }

        override val date: Consumer<Long> = Consumer { timestamp ->
            Calendar.getInstance().apply {
                timeInMillis = timestamp
                view.updateDate(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH))
            }
        }
    }
}
