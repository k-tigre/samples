package by.tigre.samples.di

import by.tigre.samples.extensions.GlobalDisposable
import by.tigre.samples.extensions.RxSchedulers

interface ReactiveModule {

    val globalDisposable: GlobalDisposable
    val schedulers: RxSchedulers

    class Impl(override val globalDisposable: GlobalDisposable) : ReactiveModule {

        override val schedulers by lazy { RxSchedulers.Impl() }
    }
}
