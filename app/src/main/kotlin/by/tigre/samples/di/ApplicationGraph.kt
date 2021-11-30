package by.tigre.samples.di

import android.content.Context
import by.tigre.samples.di.dependencies.UsersScreenDependencies
import by.tigre.samples.extensions.MainDisposable
import by.tigre.samples.extensions.RxSchedulers

interface ApplicationGraph : UsersScreenDependencies,
    NetworkModule,
    PlatformModule,
    BusinessModule {

    override val schedulers: RxSchedulers

    class Impl(
        networkModule: NetworkModule,
        platformModule: PlatformModule,
        reactiveModule: ReactiveModule,
        businessModule: BusinessModule
    ) : ApplicationGraph,
        NetworkModule by networkModule,
        PlatformModule by platformModule,
        BusinessModule by businessModule {

        override val schedulers: RxSchedulers = reactiveModule.schedulers
    }

    companion object {
        fun create(context: Context, mainDisposable: MainDisposable): ApplicationGraph {
            val reactiveModule = ReactiveModule.Impl(mainDisposable)
            val platformModule = PlatformModule.Impl(context)
            val networkModule = NetworkModule.Impl()
            val businessModule = BusinessModule.Impl()

            return Impl(
                networkModule,
                platformModule,
                reactiveModule,
                businessModule
            )
        }
    }
}
