package by.tigre.samples.domain.users

import by.tigre.samples.data.bisness.UsersRepository
import by.tigre.samples.data.models.NewUserEntity
import by.tigre.samples.data.models.UserEntity
import by.tigre.samples.domain.users.EditUserService.Impl
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.Singles
import io.reactivex.rxjava3.subjects.BehaviorSubject

interface EditUserService {
    val wight: Observable<Float>
    val birthday: Observable<Long>
    val photoUrl: Observable<String>

    fun saveWight(wight: Float)
    fun saveBirthday(date: Long)
    fun savePhoto(url: String)

    fun saveUser(): Single<Result<Unit>>

    class Impl(
        private val initialUser: UserEntity?,
        private val userRepository: UsersRepository
    ) : EditUserService {

        override val wight = BehaviorSubject.createDefault(initialUser?.wight ?: -1f)
        override val birthday = BehaviorSubject.createDefault(initialUser?.birthday ?: -1L)
        override val photoUrl = BehaviorSubject.createDefault(initialUser?.photoUrl ?: "")

        override fun saveWight(wight: Float) {
            this.wight.onNext(wight)
        }

        override fun saveBirthday(date: Long) {
            this.birthday.onNext(date)
        }

        override fun savePhoto(url: String) {
            photoUrl.onNext(url)
        }

        override fun saveUser(): Single<Result<Unit>> = Singles
            .zip(
                wight.take(1).singleOrError(),
                birthday.take(1).singleOrError(),
                photoUrl.take(1).singleOrError()
            ).flatMap { (wight, birthday, photoUrl) ->
                if (initialUser != null) {
                    userRepository.updateUser(
                        initialUser.copy(
                            wight = wight,
                            birthday = birthday,
                            photoUrl = photoUrl
                        )
                    )
                } else {
                    userRepository.addUser(NewUserEntity(wight, birthday, photoUrl))
                }
            }
    }

    interface Provider {
        fun provide(initialUser: UserEntity?): EditUserService

        class Impl(private val userRepository: UsersRepository) : Provider {
            override fun provide(initialUser: UserEntity?): EditUserService = Impl(
                initialUser = initialUser,
                userRepository = userRepository
            )
        }
    }
}
