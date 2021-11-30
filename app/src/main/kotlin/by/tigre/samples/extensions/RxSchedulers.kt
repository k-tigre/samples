package by.tigre.samples.extensions

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

interface RxSchedulers {
    val time: Scheduler
    val io: Scheduler
    val mainThread: Scheduler
    val single: Scheduler

    class Impl : RxSchedulers {
        override val time: Scheduler = Schedulers.computation()
        override val io: Scheduler = Schedulers.io()
        override val mainThread: Scheduler = AndroidSchedulers.mainThread()
        override val single: Scheduler = Schedulers.single()
    }
}
