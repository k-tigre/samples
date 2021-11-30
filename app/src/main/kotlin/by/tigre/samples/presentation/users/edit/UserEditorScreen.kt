package by.tigre.samples.presentation.users.edit

import android.view.ViewGroup
import by.tigre.samples.R
import by.tigre.samples.data.platform.Resources
import by.tigre.samples.di.dependencies.UsersScreenDependencies
import by.tigre.samples.domain.users.EditUserService
import by.tigre.samples.extensions.find
import by.tigre.samples.extensions.newSwapDisposable
import by.tigre.samples.presentation.ActivityHolder
import by.tigre.samples.presentation.base.*
import by.tigre.samples.presentation.users.UserViewData
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

object UserEditorScreen {
    interface Presenter : BasePresenter {
        val onBackClicked: Observable<Unit>

        class Impl(
            view: View,
            activityHolder: ActivityHolder,
            user: UserViewData?,
            factory: PresenterFactory,
            provider: EditUserService.Provider,
            resources: Resources
        ) : Presenter, BasePresenter.Impl() {
            private enum class Screen { Wight, Birthday, Photo }

            private val currentScreen = PublishSubject.create<Screen>()
            private val childPresenter = newSwapDisposable()
            private val onCancelSignal = PublishSubject.create<Unit>()
            private val editUserService = provider.provide(user?.entity)
            private val saveDisposable = newSwapDisposable()

            override val onBackClicked = PublishSubject.create<Unit>()

            init {
                this += Disposable.fromAction { view.destroy() }

                this += activityHolder.processBackButton(view, canHandleSource = Observable.just(true))
                    .subscribeBy { onCancelSignal.onNext(Unit) }

                this += currentScreen
                    .distinctUntilChanged()
                    .subscribeBy { screen: Screen ->
                        childPresenter.set {
                            when (screen) {
                                Screen.Wight -> factory.createEditUserWight(
                                    view = view.createEditUserWight(),
                                    service = editUserService
                                )
                                Screen.Birthday -> factory.createEditUserBirthday(
                                    view = view.createEditUserBirthday(),
                                    service = editUserService
                                )
                                Screen.Photo -> factory.createEditUserPhoto(
                                    view = view.createEditUserPhoto(),
                                    service = editUserService
                                )
                            }
                        }
                    }

                this += view.nextButton.action
                    .withLatestFrom(currentScreen) { _, screen -> screen }
                    .subscribeBy { screen: Screen ->
                        when (screen) {
                            Screen.Wight -> currentScreen.onNext(Screen.Birthday)
                            Screen.Birthday -> currentScreen.onNext(Screen.Photo)
                            Screen.Photo -> saveUser()

                        }
                    }

                this += view.prevButton.action
                    .withLatestFrom(currentScreen) { _, screen -> screen }
                    .subscribeBy { screen: Screen ->
                        when (screen) {
                            Screen.Wight -> onCancelSignal.onNext(Unit)
                            Screen.Birthday -> currentScreen.onNext(Screen.Wight)
                            Screen.Photo -> currentScreen.onNext(Screen.Birthday)
                        }
                    }

                this += onCancelSignal
                    .subscribeBy {
                        // TODO show confirm
                        onBackClicked.onNext(Unit)
                    }

                this += currentScreen
                    .distinctUntilChanged()
                    .subscribeBy { screen: Screen ->
                        when (screen) {
                            Screen.Wight -> {
                                view.nextButton.setText(resources.string(R.string.button_next))
                                view.prevButton.setText(resources.string(R.string.button_cancel))
                                view.toolbarView.setTitle(
                                    resources.string(if (user == null) R.string.screen_title_user_enter_body_weight else R.string.screen_title_user_edit_body_weight)
                                )
                            }
                            Screen.Birthday -> {
                                view.nextButton.setText(resources.string(R.string.button_next))
                                view.prevButton.setText(resources.string(R.string.button_prev))
                                view.toolbarView.setTitle(
                                    resources.string(if (user == null) R.string.screen_title_user_enter_birthday else R.string.screen_title_user_edit_birthday)
                                )
                            }
                            Screen.Photo -> {
                                view.nextButton.setText(resources.string(R.string.button_save))
                                view.prevButton.setText(resources.string(R.string.button_prev))
                                view.toolbarView.setTitle(
                                    resources.string(if (user == null) R.string.screen_title_user_enter_photo else R.string.screen_title_user_edit_photo)
                                )
                            }
                        }
                    }

                currentScreen.onNext(Screen.Wight)
            }

            private fun saveUser() = saveDisposable.set {
                editUserService.saveUser().subscribeBy { result ->
                    if (result.isSuccess) {
                        onBackClicked.onNext(Unit)
                    } else {
                        // TODO show error
                    }
                }

            }
        }
    }

    interface View : HandleBackView, DestroyableView {
        val toolbarView: ToolbarView
        val nextButton: ButtonView
        val prevButton: ButtonView

        fun createEditUserBirthday(): EditUserBirthday.View
        fun createEditUserPhoto(): EditUserPhoto.View
        fun createEditUserWight(): EditUserWight.View

        class Impl(parent: ViewGroup) : View, DestroyableView.Impl(
            parent = parent,
            layoutId = R.layout.screen_user_editor,
            inAnimation = DestroyableView.Animation.FadeAndTranslation,
            outAnimation = DestroyableView.Animation.FadeAndTranslation
        ) {
            private val childContainer = view.find<ViewGroup>(R.id.inner_container)

            override val toolbarView = ToolbarView.Impl(view.find(R.id.toolbar))
            override val nextButton = ButtonView.Impl(view.find(R.id.button_next))
            override val prevButton = ButtonView.Impl(view.find(R.id.button_prev))
            override val onBackClicked: Observable<Unit> = toolbarView.navigationActionClicked

            override fun createEditUserBirthday(): EditUserBirthday.View = EditUserBirthday.View.Impl(childContainer)
            override fun createEditUserPhoto(): EditUserPhoto.View = EditUserPhoto.View.Impl(childContainer)
            override fun createEditUserWight(): EditUserWight.View = EditUserWight.View.Impl(childContainer)
        }
    }

    interface PresenterFactory {
        fun createUserEditor(view: View, selectedUser: UserViewData?): Presenter
        fun createEditUserBirthday(view: EditUserBirthday.View, service: EditUserService): EditUserBirthday.Presenter
        fun createEditUserPhoto(view: EditUserPhoto.View, service: EditUserService): EditUserPhoto.Presenter
        fun createEditUserWight(view: EditUserWight.View, service: EditUserService): EditUserWight.Presenter

        class Impl(
            private val dependencies: UsersScreenDependencies,
            private val activityHolder: ActivityHolder
        ) : PresenterFactory {
            private val provider: EditUserService.Provider = EditUserService.Provider.Impl(dependencies.usersRepository)

            override fun createUserEditor(
                view: View,
                selectedUser: UserViewData?
            ): Presenter = Presenter.Impl(
                view = view,
                activityHolder = activityHolder,
                user = selectedUser,
                factory = this,
                provider = provider,
                resources = dependencies.resources
            )

            override fun createEditUserBirthday(
                view: EditUserBirthday.View,
                service: EditUserService
            ): EditUserBirthday.Presenter = EditUserBirthday.Presenter.Impl(
                view = view,
                service = service
            )

            override fun createEditUserPhoto(
                view: EditUserPhoto.View,
                service: EditUserService
            ): EditUserPhoto.Presenter = EditUserPhoto.Presenter.Impl(
                view = view,
                service = service,
                schedulers = dependencies.schedulers
            )

            override fun createEditUserWight(
                view: EditUserWight.View,
                service: EditUserService
            ): EditUserWight.Presenter = EditUserWight.Presenter.Impl(
                view = view,
                service = service,
                schedulers = dependencies.schedulers,
                wightSystemTypeSettings = dependencies.wightSystemTypeSettings
            )
        }
    }
}
