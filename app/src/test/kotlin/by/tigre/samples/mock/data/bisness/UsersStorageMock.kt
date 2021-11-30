package by.tigre.samples.mock.data.bisness

import by.tigre.samples.data.bisness.UsersStorage
import by.tigre.samples.data.models.NewUserEntity
import by.tigre.samples.data.models.UserEntity
import by.tigre.samples.data.models.UserId
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.SingleSubject

open class UsersStorageMock : UsersStorage {
    var usersEmitter = SingleSubject.create<Result<List<UserEntity>>>()
    var addUserEmitter = SingleSubject.create<Result<Unit>>()
    var updateUserEmitter = SingleSubject.create<Result<Unit>>()
    var findUserEmitter = SingleSubject.create<Result<UserEntity>>()

    override fun users(): Single<Result<List<UserEntity>>> = usersEmitter
    override fun addUser(userEntity: NewUserEntity): Single<Result<Unit>> = addUserEmitter
    override fun updateUser(userEntity: UserEntity): Single<Result<Unit>> = updateUserEmitter
    override fun findUser(id: UserId): Single<Result<UserEntity>> = findUserEmitter
}
