package by.tigre.samples.presentation.users.edit

import android.view.ViewGroup
import by.tigre.samples.R
import by.tigre.samples.domain.users.EditUserService
import by.tigre.samples.extensions.RxSchedulers
import by.tigre.samples.extensions.find
import by.tigre.samples.presentation.base.BasePresenter
import by.tigre.samples.presentation.base.DestroyableView
import by.tigre.samples.presentation.base.ImageView
import by.tigre.samples.presentation.base.InputFieldView
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

object EditUserPhoto {
    interface Presenter : BasePresenter {

        class Impl(
            view: View,
            service: EditUserService,
            schedulers: RxSchedulers
        ) : Presenter, BasePresenter.Impl() {

            init {
                this += Disposable.fromAction {
                    view.inputFieldView.hideKeyboard()
                    view.destroy()
                }

                this += service.photoUrl
                    .observeOn(schedulers.mainThread)
                    .subscribeBy { url ->
                        view.imageView.loadImage(url)
                    }

                this += view.inputFieldView.textChanges
                    .skip(1)
                    .debounce(1, TimeUnit.SECONDS, schedulers.time)
                    .map(::generateAvatarUrl)
                    .subscribeBy { result ->
                        result.onSuccess(service::savePhoto)
                    }

                this += view.inputFieldView.editorActions
                    .subscribeBy { view.inputFieldView.hideKeyboard() }
            }

            private fun generateAvatarUrl(text: String): Result<String> =
                kotlin.runCatching { BASE_URL.format(URLEncoder.encode(text, "utf-8")) }

            private companion object {
                const val BASE_URL = "https://eu.ui-avatars.com/api/?name=%s"
            }
        }
    }

    interface View : DestroyableView {
        val inputFieldView: InputFieldView
        val imageView: ImageView

        class Impl(parent: ViewGroup) : View, DestroyableView.Impl(
            parent = parent,
            layoutId = R.layout.view_user_edit_photo,
            inAnimation = DestroyableView.Animation.FadeAndTranslation,
            outAnimation = DestroyableView.Animation.FadeAndTranslation
        ) {
            override val inputFieldView: InputFieldView =
                InputFieldView.Impl(view.find(R.id.text_input_layout))
            override val imageView: ImageView = ImageView.Impl(view.find(R.id.image))
        }
    }
}
