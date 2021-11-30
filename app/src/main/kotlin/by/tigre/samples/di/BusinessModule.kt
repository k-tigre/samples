package by.tigre.samples.di

import by.tigre.samples.data.bisness.IdGenerator
import by.tigre.samples.data.bisness.UsersRepository
import by.tigre.samples.data.bisness.UsersStorage
import by.tigre.samples.data.bisness.WightSystemTypeSettings

interface BusinessModule {
    val usersRepository: UsersRepository
    val usersStorage: UsersStorage
    val idGenerator: IdGenerator
    val wightSystemTypeSettings: WightSystemTypeSettings

    class Impl : BusinessModule {
        override val usersRepository: UsersRepository by lazy { UsersRepository.Impl(usersStorage) }

        override val usersStorage: UsersStorage by lazy { UsersStorage.Impl(idGenerator) }

        override val idGenerator: IdGenerator by lazy { IdGenerator.Impl() }

        override val wightSystemTypeSettings: WightSystemTypeSettings by lazy { WightSystemTypeSettings.Impl() }
    }
}
