package by.tigre.samples.presentation.base

import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.tigre.samples.R
import by.tigre.samples.extensions.*
import com.jakewharton.rxbinding4.swiperefreshlayout.refreshes
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlin.properties.Delegates

object BaseList {

    interface Presenter<S : Any> : BasePresenter {
        val onItemSelected: Observable<S>

        fun refresh()

        class Impl<T : Any, S : Any>(
            rxSchedulers: RxSchedulers,
            view: View<T, S>,
            processRequest: () -> Single<Result<List<T>>>,
        ) : Presenter<S>, BasePresenter.Impl() {

            private val refreshSignal = PublishSubject.create<Unit>()

            init {
                val dataSource =
                    Observable.merge(view.retryAction, refreshSignal, view.refreshAction)
                        .doOnNext { view.showProgress() }
                        .switchMapSingle { processRequest() }
                        .share()

                this += dataSource.filter { it.isFailure }
                    .observeOn(rxSchedulers.mainThread)
                    .subscribeBy {
                        view.showError()
                    }

                this += dataSource.filter { it.isSuccess }.map { it.getOrNull()!! }
                    .observeOn(rxSchedulers.mainThread)
                    .subscribeBy { data ->
                        if (data.isNotEmpty()) {
                            view.showData(data)
                        } else {
                            view.showEmpty()
                        }
                    }
            }

            override val onItemSelected: Observable<S> = view.selectedItem

            override fun refresh() = refreshSignal.onNext(Unit)
        }
    }

    interface View<in T : Any, S : Any> {

        val selectedItem: Observable<S>
        val retryAction: Observable<Unit>
        val refreshAction: Observable<Unit>

        fun showData(data: List<T>)
        fun showEmpty()
        fun showProgress(overContent: Boolean = false)
        fun showError()

        class Impl<in T : Any, S : Any>(
            container: AndroidView,
            noItemsMessage: String,
            errorMessage: String,
            private val listAdapter: Adapter<T, S>
        ) : View<T, S> {

            private val swipe = container.find<SwipeRefreshLayout>(R.id.swipe_container).apply {
                setColorSchemeResources(
                    R.color.primaryColor,
                    R.color.primaryDarkColor,
                    R.color.primaryLightColor
                )
            }
            private val empty = container.find<AndroidView>(R.id.no_items_container).apply {
                find<TextView>(R.id.no_items_message).text = noItemsMessage
            }

            private val error = container.find<AndroidView>(R.id.view_error_state).apply {
                find<TextView>(R.id.message).text = errorMessage
            }

            private val list = container.find<RecyclerView>(R.id.list).apply {
                itemAnimator = DefaultItemAnimator()
                layoutManager = LinearLayoutManager(context)
                adapter = listAdapter
            }

            override val selectedItem: Observable<S> = listAdapter.selectedItem
            override val retryAction: Observable<Unit> =
                error.find<AndroidView>(R.id.reload).clicks()
            override val refreshAction: Observable<Unit> = swipe.refreshes()

            override fun showData(data: List<T>) {
                listAdapter.data = data
                list.show()
                swipe.isRefreshing = false
                empty.hide()
                error.hide()
            }

            override fun showEmpty() {
                list.hide()
                swipe.isRefreshing = false
                empty.show()
                error.hide()
            }

            override fun showProgress(overContent: Boolean) {
                list.setVisible(overContent)
                swipe.isRefreshing = true
                empty.hide()
                error.hide()
            }

            override fun showError() {
                list.hide()
                swipe.isRefreshing = false
                empty.hide()
                error.show()
            }
        }
    }

    class Adapter<T : Any, S : Any>(
        private val createViewHolder: (ViewGroup) -> ViewHolder<T, S>
    ) : RecyclerView.Adapter<ViewHolder<T, S>>() {

        var data: List<T> by Delegates.observable(emptyList()) { _, _, _ ->
            notifyDataSetChanged()
        }

        private val selectedItemEmitter = PublishSubject.create<S>()
        val selectedItem: Observable<S> = selectedItemEmitter

        override fun onBindViewHolder(holder: ViewHolder<T, S>, position: Int) = with(holder) {
            bind(item = data[position]) { itemSelectedResult ->
                selectedItemEmitter.onNext(itemSelectedResult)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T, S> =
            createViewHolder(parent)

        override fun onViewRecycled(holder: ViewHolder<T, S>) = holder.unbind()
        override fun getItemCount(): Int = data.size
    }

    abstract class ViewHolder<in T : Any, out S : Any>(
        parent: ViewGroup,
        @LayoutRes itemLayoutId: Int
    ) : RecyclerView.ViewHolder(parent.inflate(itemLayoutId)) {
        abstract fun bind(item: T, onItemSelected: (S) -> Unit)
        abstract fun unbind()
    }

    interface PresenterFactory {
        fun <T : Any, S : Any> createList(
            view: View<T, S>,
            processRequest: () -> Single<Result<List<T>>>,
        ): Presenter<S>

        class Impl(private val rxSchedulers: RxSchedulers) : PresenterFactory {

            override fun <T : Any, S : Any> createList(
                view: View<T, S>,
                processRequest: () -> Single<Result<List<T>>>,
            ): Presenter<S> = Presenter.Impl(
                rxSchedulers = rxSchedulers,
                view = view,
                processRequest = processRequest,
            )
        }
    }
}
