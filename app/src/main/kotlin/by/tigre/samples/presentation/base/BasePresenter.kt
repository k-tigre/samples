package by.tigre.samples.presentation.base

import by.tigre.samples.extensions.AddDisposable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.plusAssign


interface BasePresenter : Disposable, AddDisposable {
    abstract class Impl : BasePresenter {

        private val disposable = CompositeDisposable()

        override fun isDisposed(): Boolean = disposable.isDisposed

        override fun dispose() {
            disposable.dispose()
        }

        override operator fun plusAssign(d: Disposable) {
            disposable += d
        }

        protected fun clear() {
            disposable.clear()
        }
    }
}
