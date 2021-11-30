package by.tigre.samples.presentation.users

import android.view.ViewGroup
import by.tigre.samples.di.dependencies.UsersScreenDependencies
import by.tigre.samples.extensions.RxSchedulers
import by.tigre.samples.extensions.newSwapDisposable
import by.tigre.samples.presentation.ActivityHolder
import by.tigre.samples.presentation.base.BaseList
import by.tigre.samples.presentation.base.BasePresenter
import by.tigre.samples.presentation.users.edit.UserEditorScreen
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

object UsersScreen {
    interface Presenter : BasePresenter {
        class Impl(
            private val view: View,
            schedulers: RxSchedulers,
            private val factory: PresenterFactory,
            private val activityHolder: ActivityHolder
        ) : Presenter, BasePresenter.Impl() {

            private sealed class Screen {
                object List : Screen()
                data class Details(val selectedUser: UserViewData?) : Screen()
            }

            private val currentScreen = PublishSubject.create<Screen>()
            private val child = newSwapDisposable()

            init {
                this += currentScreen.startWithItem(Screen.List)
                    .distinctUntilChanged()
                    .observeOn(schedulers.mainThread)
                    .subscribeBy { screen: Screen ->
                        child.set {
                            when (screen) {
                                Screen.List -> showUsersList()
                                is Screen.Details -> showUserEditor(screen.selectedUser)
                            }
                        }
                    }
            }

            private fun showUsersList(): BasePresenter =
                factory.createUsersList(view = view.createUsersList()).apply {
                    this += onBackClicked.subscribeBy { activityHolder.close() }
                    this += onUserClicked.subscribeBy { user ->
                        currentScreen.onNext(
                            Screen.Details(
                                user
                            )
                        )
                    }
                    this += onAddUserClicked.subscribeBy { currentScreen.onNext(Screen.Details(null)) }
                }

            private fun showUserEditor(selectedUser: UserViewData?): BasePresenter =
                factory.createUserEditor(view.createUserEditor(), selectedUser).apply {
                    this += onBackClicked.subscribeBy { currentScreen.onNext(Screen.List) }
                }
        }
    }

    interface View {
        fun createUsersList(): UsersList.View
        fun createUserEditor(): UserEditorScreen.View

        class Impl(private val parent: ViewGroup) : View {
            override fun createUsersList(): UsersList.View = UsersList.View.Impl(parent)
            override fun createUserEditor(): UserEditorScreen.View =
                UserEditorScreen.View.Impl(parent)
        }
    }

    interface PresenterFactory {
        fun createScreen(view: View): Presenter
        fun createUsersList(view: UsersList.View): UsersList.Presenter
        fun createUserEditor(
            view: UserEditorScreen.View,
            selectedUser: UserViewData?
        ): UserEditorScreen.Presenter

        class Impl(
            private val dependencies: UsersScreenDependencies,
            private val activityHolder: ActivityHolder
        ) : PresenterFactory {
            private val listFactory by lazy { BaseList.PresenterFactory.Impl(dependencies.schedulers) }
            private val editorFactory by lazy {
                UserEditorScreen.PresenterFactory.Impl(dependencies, activityHolder)
            }

            override fun createScreen(
                view: View
            ): Presenter = Presenter.Impl(
                view = view,
                schedulers = dependencies.schedulers,
                factory = this,
                activityHolder = activityHolder
            )

            override fun createUsersList(
                view: UsersList.View
            ): UsersList.Presenter = UsersList.Presenter.Impl(
                view = view,
                activityHolder = activityHolder,
                listFactory = listFactory,
                resources = dependencies.resources,
                usersRepository = dependencies.usersRepository,
                wightSystemTypeSettings = dependencies.wightSystemTypeSettings
            )

            override fun createUserEditor(
                view: UserEditorScreen.View,
                selectedUser: UserViewData?
            ): UserEditorScreen.Presenter = editorFactory.createUserEditor(view, selectedUser)
        }
    }
}
