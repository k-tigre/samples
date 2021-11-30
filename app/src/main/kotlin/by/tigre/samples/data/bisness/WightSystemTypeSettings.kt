package by.tigre.samples.data.bisness

import by.tigre.samples.R
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

interface WightSystemTypeSettings {
    enum class Type(val factor: Float, val title: Int) {
        Metric(1f, R.string.wight_system_type_metric_title),
        Imperial(2.20462f, R.string.wight_system_type_imperial_title)
    }

    val type: Observable<Type>
    fun saveType(type: Type)

    class Impl : WightSystemTypeSettings {
        override val type: BehaviorSubject<Type> = BehaviorSubject.createDefault(Type.Metric)

        override fun saveType(type: Type) {
            this.type.onNext(type)
        }
    }
}
