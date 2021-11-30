package by.tigre.samples.data.bisness

import by.tigre.samples.data.models.NewUserEntity
import by.tigre.samples.data.models.UserEntity
import by.tigre.samples.data.models.UserId
import by.tigre.samples.mock.data.bisness.UsersStorageMock
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.rxjava3.observers.TestObserver
import org.mockito.internal.verification.Times
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UseUsersRepository : Spek({
    val env by memoized { Environment() }

    describe("get users") {
        val subscription by memoized { TestObserver<Result<List<UserEntity>>>() }
        beforeEachTest {
            env.repository.users().subscribe(subscription)
        }

        it("calls get users") {
            verify(env.usersStorage).users()
        }

        context("received success") {
            val successResult = Result.success(USERS)

            beforeEachTest {
                env.usersStorage.usersEmitter.onSuccess(successResult)
            }

            it("emits success result") {
                subscription.assertValues(successResult)
            }
        }

        context("received failure") {
            val failureResult = Result.failure<List<UserEntity>>(Throwable(""))

            beforeEachTest {
                env.usersStorage.usersEmitter.onSuccess(failureResult)
            }

            it("emits failure result") {
                subscription.assertValues(failureResult)
            }
        }
    }

    describe("add users") {
        val subscription by memoized { TestObserver<Result<Unit>>() }
        beforeEachTest {
            env.repository.addUser(NEW_USER).subscribe(subscription)
        }

        it("calls add user") {
            verify(env.usersStorage).addUser(NEW_USER)
        }

        context("received success") {
            val successResult = Result.success(Unit)

            beforeEachTest {
                env.usersStorage.addUserEmitter.onSuccess(successResult)
            }

            it("emits success result") {
                subscription.assertValues(successResult)
            }
        }

        context("received failure") {
            val failureResult = Result.failure<Unit>(Throwable(""))

            beforeEachTest {
                env.usersStorage.addUserEmitter.onSuccess(failureResult)
            }

            it("emits failure result") {
                subscription.assertValues(failureResult)
            }
        }
    }

    describe("update users") {
        val subscription by memoized { TestObserver<Result<Unit>>() }
        beforeEachTest {
            env.repository.updateUser(USER).subscribe(subscription)
        }

        it("calls find user") {
            verify(env.usersStorage).findUser(USER.id)
        }

        it("does not call update user") {
            verify(env.usersStorage, Times(0)).updateUser(any())
        }

        context("received success for find") {
            beforeEachTest {
                env.usersStorage.findUserEmitter.onSuccess(Result.success(USER_1))
            }

            it("does not emit result") {
                subscription.assertNoValues()
            }

            it("calls update user") {
                verify(env.usersStorage).updateUser(USER)
            }

            context("received success for update") {
                val successResult = Result.success(Unit)

                beforeEachTest {
                    env.usersStorage.updateUserEmitter.onSuccess(successResult)
                }

                it("emits success result") {
                    subscription.assertValues(successResult)
                }
            }

            context("received failure for update") {
                val failureResult = Result.failure<Unit>(Throwable(""))

                beforeEachTest {
                    env.usersStorage.updateUserEmitter.onSuccess(failureResult)
                }

                it("emits failure result") {
                    subscription.assertValues(failureResult)
                }
            }
        }

        context("received failure for find") {
            val failureResult = Result.failure<UserEntity>(Throwable(""))

            beforeEachTest {
                env.usersStorage.findUserEmitter.onSuccess(failureResult)
            }

            it("emits failure result") {
                subscription.assertValue { value -> value.isFailure }
            }
        }
    }

}) {
    private class Environment {
        val usersStorage = spy(UsersStorageMock())

        val repository = UsersRepository.Impl(
            storage = usersStorage
        )
    }

    private companion object {
        val NEW_USER = NewUserEntity(10f, 1000L, "")
        val USER = UserEntity(UserId("id"), 10f, 1000L, "")
        val USER_1 = UserEntity(UserId("id"), 30f, 4000L, "")
        val USERS = listOf(USER, USER, USER)
    }
}
