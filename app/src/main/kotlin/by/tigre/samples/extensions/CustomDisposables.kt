package by.tigre.samples.extensions

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.plusAssign

class MainDisposable : Disposable, GlobalDisposable {

    // Creating CompositeDisposable explicitly is only allowed in framework classes
    private val disposable = CompositeDisposable()

    override fun isDisposed(): Boolean = disposable.isDisposed

    override fun dispose() {
        disposable.dispose()
    }

    override operator fun plusAssign(d: Disposable) {
        disposable += d
    }
}

interface AddDisposable {
    operator fun plusAssign(d: Disposable)
}

interface GlobalDisposable : AddDisposable

interface RemoveDisposable {
    operator fun minusAssign(d: Disposable)
}

interface ClearDisposable {
    fun clear()
}

interface SwappableDisposable : Disposable {
    fun set(disposable: () -> Disposable): Disposable
}

interface Disposables : Disposable, AddDisposable, RemoveDisposable, ClearDisposable {

    class Impl @Deprecated("Use 'newChildDisposable' instead") constructor() : Disposables {

        // Creating CompositeDisposable explicitly is only allowed in framework classes
        private val disposable = CompositeDisposable()

        override fun isDisposed(): Boolean = disposable.isDisposed

        override fun dispose() {
            disposable.dispose()
        }

        override operator fun plusAssign(d: Disposable) {
            disposable += d
        }

        override fun minusAssign(d: Disposable) {
            disposable.remove(d)
        }

        override fun clear() {
            disposable.clear()
        }
    }
}

class SwapDisposable @Deprecated("Use 'newSwapDisposable' instead") constructor() :
    SwappableDisposable {

    // Creating CompositeDisposable explicitly is only allowed in framework classes
    private val innerDisposable = CompositeDisposable()

    override fun isDisposed(): Boolean = innerDisposable.isDisposed

    override fun dispose() {
        innerDisposable.dispose()
    }

    override fun set(disposable: () -> Disposable): Disposable {
        innerDisposable.clear()
        return disposable().also { innerDisposable.add(it) }
    }
}


/**
 * The only legitimate way to create SwapDisposable.
 */
@Suppress("DEPRECATION")
fun AddDisposable.newSwapDisposable(): SwapDisposable = SwapDisposable().also { new -> this += new }

/**
 * The only legitimate way to create Disposables class.
 */
@Suppress("DEPRECATION")
fun AddDisposable.newDisposables(): Disposables = Disposables.Impl().also { new -> this += new }
