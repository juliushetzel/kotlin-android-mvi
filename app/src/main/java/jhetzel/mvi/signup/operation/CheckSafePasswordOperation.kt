package jhetzel.mvi.signup.operation

import io.reactivex.Observable
import jhetzel.mvi.base.BaseAction
import jhetzel.mvi.base.BaseResult
import jhetzel.mvi.base.BaseOperation


class CheckSafePasswordOperation : BaseOperation<CheckSafePasswordOperation.Action>() {

    override fun buildOperation(action: Observable<Action>): Observable<BaseResult> {
        return action.map { isPasswordSafe(it.password) }
                .map { (if(it) Result.Safe else Result.NotSafe) as BaseResult}
    }

    fun isPasswordSafe(password: String): Boolean{
        return password.length == 8
    }

    sealed class Result : BaseResult {

        object Safe: Result()

        object NotSafe: Result()

    }

    data class Action(val password: String): BaseAction
}