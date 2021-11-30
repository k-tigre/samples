package by.tigre.samples.data.bisness

import by.tigre.samples.data.models.NewUserEntity
import by.tigre.samples.data.models.UserEntity
import by.tigre.samples.data.models.UserId
import io.reactivex.rxjava3.core.Single

interface UsersStorage {
    fun users(): Single<Result<List<UserEntity>>>
    fun addUser(userEntity: NewUserEntity): Single<Result<Unit>>
    fun updateUser(userEntity: UserEntity): Single<Result<Unit>>
    fun findUser(id: UserId): Single<Result<UserEntity>>

    class Impl(private val idGenerator: IdGenerator) : UsersStorage {

        private val userStorage = mutableMapOf<UserId, UserEntity>()

        override fun users(): Single<Result<List<UserEntity>>> =
            Single.fromCallable { Result.success(userStorage.values.toList()) }

        override fun addUser(userEntity: NewUserEntity): Single<Result<Unit>> =
            Single.fromCallable {
                val id = UserId(idGenerator.generate())
                userStorage[id] = UserEntity.from(userEntity, id)
                Result.success(Unit)
            }

        override fun updateUser(userEntity: UserEntity): Single<Result<Unit>> =
            Single.fromCallable {
                if (userStorage.containsKey(userEntity.id)) {
                    userStorage[userEntity.id] = userEntity
                    Result.success(Unit)
                } else {
                    Result.failure(RuntimeException("can not found user"))
                }
            }

        override fun findUser(id: UserId): Single<Result<UserEntity>> =
            Single.fromCallable {
                val user = userStorage[id]
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(RuntimeException("can not found user"))
                }
            }
    }
}
