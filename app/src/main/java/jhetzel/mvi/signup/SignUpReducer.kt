package jhetzel.mvi.signup

import jhetzel.mvi.base.BaseReducer
import jhetzel.mvi.base.BaseResult
import jhetzel.mvi.error.ErrorMessageFactory
import jhetzel.mvi.signup.operation.CheckEmailAvailableOperation
import jhetzel.mvi.signup.operation.CheckSafePasswordOperation
import jhetzel.mvi.signup.operation.SignUpOperation


class SignUpReducer (
        override val initialState: SignUpState
) : BaseReducer<SignUpState> {

    override fun reduce(currentState: SignUpState, result: BaseResult): SignUpState = when(result){

        is CheckEmailAvailableOperation.Result -> when(result){
            CheckEmailAvailableOperation.Result.InFlight -> currentState.copy(checkingEmail = currentState.checkingEmail + 1)
            CheckEmailAvailableOperation.Result.Available -> currentState.copy(checkingEmail = currentState.checkingEmail - 1, emailAvailable = true)
            CheckEmailAvailableOperation.Result.NotAvailable -> currentState.copy(checkingEmail = currentState.checkingEmail - 1, emailAvailable = false)
            is CheckEmailAvailableOperation.Result.Failure -> currentState.copy(checkingEmail = currentState.checkingEmail - 1, errorMessage = ErrorMessageFactory.get(result.error))
        }

        is CheckSafePasswordOperation.Result -> when(result){
            CheckSafePasswordOperation.Result.Safe -> currentState.copy(passwordSafe = true)
            CheckSafePasswordOperation.Result.NotSafe -> currentState.copy(passwordSafe = false)
        }

        is SignUpOperation.Result -> when(result){
            SignUpOperation.Result.Success -> currentState.copy(signingUp = false, signedUp = true)
            SignUpOperation.Result.InFlight -> currentState.copy(signingUp = true)
            is SignUpOperation.Result.Failure -> currentState.copy(signingUp = false, errorMessage = ErrorMessageFactory.get(result.error))
        }

        else -> {
            currentState
        }
    }
}