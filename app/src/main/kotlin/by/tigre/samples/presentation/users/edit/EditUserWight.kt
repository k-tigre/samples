package by.tigre.samples.presentation.users.edit

import android.view.ViewGroup
import by.tigre.samples.R
import by.tigre.samples.data.bisness.WightSystemTypeSettings
import by.tigre.samples.domain.users.EditUserService
import by.tigre.samples.extensions.RxSchedulers
import by.tigre.samples.extensions.find
import by.tigre.samples.presentation.base.BasePresenter
import by.tigre.samples.presentation.base.DestroyableView
import by.tigre.samples.presentation.base.InputFieldView
import by.tigre.samples.presentation.base.RadioGroupView
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.lang.Float
import java.util.concurrent.TimeUnit

object EditUserWight {
    interface Presenter : BasePresenter {

        class Impl(
            view: View,
            service: EditUserService,
            schedulers: RxSchedulers,
            wightSystemTypeSettings: WightSystemTypeSettings
        ) : Presenter, BasePresenter.Impl() {
            private val format = "%.1f"

            init {
                this += Disposable.fromAction {
                    view.inputFieldView.hideKeyboard()
                    view.destroy()
                }

                this += Observable
                    .combineLatest(service.wight, wightSystemTypeSettings.type)
                    { wight, systemType ->
                        if (wight > 0f) format.format(wight * systemType.factor) else ""
                    }
                    .distinctUntilChanged()
                    .observeOn(schedulers.mainThread)
                    .subscribeBy { view.inputFieldView.setText(it) }

                this += view.inputFieldView.textChanges
                    .debounce(1, TimeUnit.SECONDS, schedulers.time)
                    .withLatestFrom(wightSystemTypeSettings.type)
                    { value, type -> kotlin.runCatching { Float.parseFloat(value) / type.factor } }
                    .subscribeBy { result ->
                        result.onSuccess(service::saveWight)
                    }

                this += view.inputFieldView.editorActions
                    .subscribeBy { view.inputFieldView.hideKeyboard() }

                this += wightSystemTypeSettings.type
                    .map { type: WightSystemTypeSettings.Type ->
                        when (type) {
                            WightSystemTypeSettings.Type.Metric -> R.id.type_kg
                            WightSystemTypeSettings.Type.Imperial -> R.id.type_lb
                        }
                    }
                    .distinctUntilChanged()
                    .subscribe(view.metricSelector.checked)

                this += view.metricSelector.checkedChanges
                    .distinctUntilChanged()
                    .subscribeBy { id ->
                        when (id) {
                            R.id.type_kg -> wightSystemTypeSettings.saveType(WightSystemTypeSettings.Type.Metric)
                            R.id.type_lb -> wightSystemTypeSettings.saveType(WightSystemTypeSettings.Type.Imperial)
                        }
                    }
            }
        }
    }

    interface View : DestroyableView {
        val inputFieldView: InputFieldView
        val metricSelector: RadioGroupView

        class Impl(parent: ViewGroup) : View, DestroyableView.Impl(
            parent = parent,
            layoutId = R.layout.view_user_edit_wight,
            inAnimation = DestroyableView.Animation.FadeAndTranslation,
            outAnimation = DestroyableView.Animation.FadeAndTranslation
        ) {
            override val inputFieldView: InputFieldView = InputFieldView.Impl(
                view.find(R.id.text_input_layout)
            )

            override val metricSelector: RadioGroupView =
                RadioGroupView.Impl(view.find(R.id.metric_selector))
        }
    }
}
