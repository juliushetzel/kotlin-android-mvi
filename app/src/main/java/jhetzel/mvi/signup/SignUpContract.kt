package jhetzel.mvi.signup

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function3
import io.reactivex.rxkotlin.ofType
import jhetzel.mvi.base.BaseAction
import jhetzel.mvi.base.BaseContract
import jhetzel.mvi.base.BaseResult
import jhetzel.mvi.signup.operation.CheckEmailAvailableOperation
import jhetzel.mvi.signup.operation.CheckSafePasswordOperation
import jhetzel.mvi.signup.operation.SignUpOperation


class SignUpContract(
        val signUpOperation: SignUpOperation,
        val checkEmailAvailableOperation: CheckEmailAvailableOperation,
        val checkSavePasswordOperation: CheckSafePasswordOperation,
        reducer: SignUpReducer
) : BaseContract<SignUpUiEvent, SignUpState>(reducer) {


    // events to actions
    override fun translateIntentions(events: Observable<SignUpUiEvent>): Observable<BaseAction> {

        val signUp: Observable<BaseAction> = events.ofType<SignUpUiEvent.ClickSubmit>()
                .withLatestFrom(
                        events.ofType<SignUpUiEvent.TypeEmail>().map { it.email },
                        events.ofType<SignUpUiEvent.TypePassword>().map { it.password },
                        Function3 { _, email, password -> SignUpOperation.Action(email, password) })


        return Observable.merge(
                signUp,
                events.ofType<SignUpUiEvent.TypeEmail>().map { CheckEmailAvailableOperation.Action(it.email) },
                events.ofType<SignUpUiEvent.TypePassword>().map { CheckSafePasswordOperation.Action(it.password) }
        )
    }

    // interactions
    override fun mapInteractions(actions: Observable<BaseAction>): Observable<BaseResult> {
        return Observable.merge(
                actions.ofType<SignUpOperation.Action>().compose(signUpOperation),
                actions.ofType<CheckEmailAvailableOperation.Action>().compose(checkEmailAvailableOperation),
                actions.ofType<CheckSafePasswordOperation.Action>().compose(checkSavePasswordOperation)
        ).observeOn(AndroidSchedulers.mainThread())
    }

}