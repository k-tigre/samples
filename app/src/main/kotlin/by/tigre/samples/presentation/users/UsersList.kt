package by.tigre.samples.presentation.users

import android.view.ViewGroup
import by.tigre.samples.R
import by.tigre.samples.data.bisness.UsersRepository
import by.tigre.samples.data.bisness.WightSystemTypeSettings
import by.tigre.samples.data.platform.Resources
import by.tigre.samples.extensions.AndroidTextView
import by.tigre.samples.extensions.AndroidView
import by.tigre.samples.extensions.find
import by.tigre.samples.presentation.ActivityHolder
import by.tigre.samples.presentation.base.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.zipWith

object UsersList {
    interface Presenter : BasePresenter {
        val onBackClicked: Observable<Unit>
        val onUserClicked: Observable<UserViewData>
        val onAddUserClicked: Observable<Unit>

        class Impl(
            view: View,
            activityHolder: ActivityHolder,
            listFactory: BaseList.PresenterFactory,
            usersRepository: UsersRepository,
            resources: Resources,
            wightSystemTypeSettings: WightSystemTypeSettings
        ) : Presenter, BasePresenter.Impl() {

            private val listPresenter = listFactory.createList(
                view = view.createListView(),
                processRequest = {
                    usersRepository.users()
                        .zipWith(wightSystemTypeSettings.type.take(1).singleOrError())
                        .map { (result, type) ->
                            result.map { entities ->
                                entities.map { entity ->
                                    UserViewData.from(entity, resources, type)
                                }
                            }
                        }
                }
            )

            override val onBackClicked: Observable<Unit> =
                activityHolder.processBackButton(view, canHandleSource = Observable.just(true))

            override val onUserClicked: Observable<UserViewData> = listPresenter.onItemSelected
            override val onAddUserClicked: Observable<Unit> = view.userButton.action

            init {
                this += Disposable.fromAction { view.destroy() }

                this += listPresenter
                listPresenter.refresh()
            }
        }
    }

    interface View : HandleBackView, DestroyableView {
        val userButton: FloatingButtonView
        fun createListView(): BaseList.View<UserViewData, UserViewData>

        class Impl(parent: ViewGroup) : View, DestroyableView.Impl(
            parent = parent,
            layoutId = R.layout.screen_users_list,
            inAnimation = DestroyableView.Animation.None,
            outAnimation = DestroyableView.Animation.Fade
        ) {
            private val toolbarView = ToolbarView.Impl(view.find(R.id.toolbar))

            override val onBackClicked: Observable<Unit> = toolbarView.navigationActionClicked
            override val userButton: FloatingButtonView = FloatingButtonView.Impl(view.find(R.id.add_user))

            override fun createListView(): BaseList.View<UserViewData, UserViewData> =
                BaseList.View.Impl(
                    container = view,
                    noItemsMessage = view.resources.getString(R.string.screen_users_list_empty),
                    errorMessage = view.resources.getString(R.string.screen_users_list_error),
                    listAdapter = adapter,
                )

            private val adapter = BaseList.Adapter(::ViewHolder)

            private class ViewHolder(parent: ViewGroup) :
                BaseList.ViewHolder<UserViewData, UserViewData>(
                    parent, R.layout.list_item_user
                ) {
                private val birthdayView = itemView.find<AndroidTextView>(R.id.birthday)
                private val wightView = itemView.find<AndroidTextView>(R.id.wight)
                private val editView = itemView.find<AndroidView>(R.id.edit_button)
                private val photoView = ImageView.Impl(itemView.find(R.id.photo_view))

                override fun bind(item: UserViewData, onItemSelected: (UserViewData) -> Unit) {
                    photoView.loadImage(item.photoUrl)
                    birthdayView.text = item.birthdayLabel
                    wightView.text = item.wightLabel
                    editView.setOnClickListener { onItemSelected(item) }
                }

                override fun unbind() {
                    editView.setOnClickListener(null)
                }
            }
        }
    }
}
