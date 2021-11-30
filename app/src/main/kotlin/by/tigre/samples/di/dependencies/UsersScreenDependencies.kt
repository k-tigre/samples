package by.tigre.samples.di.dependencies

import by.tigre.samples.data.bisness.UsersRepository
import by.tigre.samples.data.bisness.WightSystemTypeSettings
import by.tigre.samples.data.platform.Resources
import by.tigre.samples.extensions.RxSchedulers

interface UsersScreenDependencies {
    val wightSystemTypeSettings: WightSystemTypeSettings
    val resources: Resources
    val usersRepository: UsersRepository
    val schedulers: RxSchedulers
}
