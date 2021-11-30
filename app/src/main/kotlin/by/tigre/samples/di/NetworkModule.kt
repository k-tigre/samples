package by.tigre.samples.di

import by.tigre.samples.data.network.NetworkService

interface NetworkModule {

    val networkService: NetworkService

    class Impl : NetworkModule {

        override val networkService: NetworkService by lazy {
            NetworkService.Impl()
        }
    }
}
