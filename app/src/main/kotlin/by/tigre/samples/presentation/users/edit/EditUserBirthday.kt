package by.tigre.samples.presentation.users.edit

import android.view.ViewGroup
import by.tigre.samples.R
import by.tigre.samples.domain.users.EditUserService
import by.tigre.samples.extensions.find
import by.tigre.samples.presentation.base.BasePresenter
import by.tigre.samples.presentation.base.DatePickerView
import by.tigre.samples.presentation.base.DestroyableView
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy

object EditUserBirthday {
    interface Presenter : BasePresenter {

        class Impl(
            view: View,
            service: EditUserService
        ) : Presenter, BasePresenter.Impl() {

            init {
                this += Disposable.fromAction { view.destroy() }

                this += service.birthday
                    .distinctUntilChanged()
                    .subscribeBy { view.datePickerView.date.accept(it) }

                this += view.datePickerView.onDateSelected
                    .distinctUntilChanged()
                    .subscribeBy { service.saveBirthday(it) }
            }

        }
    }

    interface View : DestroyableView {
        val datePickerView: DatePickerView

        class Impl(parent: ViewGroup) : View, DestroyableView.Impl(
            parent = parent,
            layoutId = R.layout.view_user_edit_birthday,
            inAnimation = DestroyableView.Animation.FadeAndTranslation,
            outAnimation = DestroyableView.Animation.FadeAndTranslation
        ) {
            override val datePickerView: DatePickerView = DatePickerView.Impl(view.find(R.id.date_picker))
        }
    }
}
