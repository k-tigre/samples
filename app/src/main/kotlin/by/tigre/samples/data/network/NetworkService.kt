package by.tigre.samples.data.network

import by.tigre.samples.data.models.UserDetailsEntity
import by.tigre.samples.data.models.UserEntity
import by.tigre.samples.data.models.UserId
import io.reactivex.rxjava3.core.Single

interface NetworkService {
    fun getUsers(): Single<List<UserEntity>>
    fun getUserDetails(id: UserId): Single<UserDetailsEntity>
    fun saveUserDetails(user: UserDetailsEntity): Single<Unit>

    class Impl : NetworkService {
        override fun getUsers(): Single<List<UserEntity>> {
            TODO("Not yet implemented")
        }

        override fun getUserDetails(id: UserId): Single<UserDetailsEntity> {
            TODO("Not yet implemented")
        }

        override fun saveUserDetails(user: UserDetailsEntity): Single<Unit> {
            TODO("Not yet implemented")
        }
    }
}
