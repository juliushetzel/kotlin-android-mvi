package jhetzel.mvi.signup.operation

import io.reactivex.Observable
import jhetzel.mvi.base.BaseAction
import jhetzel.mvi.base.BaseResult
import jhetzel.mvi.base.BaseOperation
import jhetzel.mvi.data.UserService


class SignUpOperation(
        val userService: UserService
) : BaseOperation<SignUpOperation.Action>() {

    override fun buildOperation(action: Observable<Action>): Observable<BaseResult> {
        return action.flatMap { userService
                .signUp(it.name, it.password)
                .map { Result.Success as BaseResult }
                .onErrorReturn { Result.Failure(it) }
                .startWith(Result.InFlight) }
    }

    sealed class Result : BaseResult {

        object Success: Result()

        object InFlight: Result()

        data class Failure(val error: Throwable): Result()

    }

    data class Action(val name: String, val password: String): BaseAction
}