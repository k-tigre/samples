package by.tigre.samples.data.bisness

import by.tigre.samples.data.models.NewUserEntity
import by.tigre.samples.data.models.UserEntity
import io.reactivex.rxjava3.core.Single

interface UsersRepository {
    fun users(): Single<Result<List<UserEntity>>>
    fun addUser(userEntity: NewUserEntity): Single<Result<Unit>>
    fun updateUser(userEntity: UserEntity): Single<Result<Unit>>

    class Impl(
        private val storage: UsersStorage
    ) : UsersRepository {

        override fun users(): Single<Result<List<UserEntity>>> = storage.users()

        override fun addUser(userEntity: NewUserEntity): Single<Result<Unit>> =
            storage.addUser(userEntity)

        override fun updateUser(userEntity: UserEntity): Single<Result<Unit>> = storage
            .findUser(userEntity.id)
            .flatMap { result ->
                if (result.isSuccess) {
                    storage.updateUser(userEntity)
                } else {
                    Single.just((Result.failure(RuntimeException("can not found user"))))
                }
            }
    }
}
