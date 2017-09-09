package jhetzel.mvi.signup.operation

import io.reactivex.Observable
import jhetzel.mvi.base.BaseAction
import jhetzel.mvi.base.BaseOperation
import jhetzel.mvi.base.BaseResult
import jhetzel.mvi.data.UserService
import java.util.concurrent.TimeUnit

class CheckEmailAvailableOperation(
        val userService: UserService
) : BaseOperation<CheckEmailAvailableOperation.Action>() {

    override fun buildOperation(action: Observable<Action>): Observable<BaseResult> {
        return action
                .debounce(1000, TimeUnit.MILLISECONDS)
                .flatMap { userService
                .emailAvailable(it.email)
                .map { if(it) Result.Available else Result.NotAvailable as BaseResult }
                .onErrorReturn { Result.Failure(it) }
                .startWith(Result.InFlight) }
    }

    sealed class Result : BaseResult {

        object InFlight: Result()

        object Available: Result()

        object NotAvailable: Result()

        data class Failure(val error: Throwable): Result()

    }

    data class Action(val email: String): BaseAction
}