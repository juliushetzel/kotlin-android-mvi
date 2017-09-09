package jhetzel.mvi.signup


sealed class SignUpUiEvent {

    object ClickSubmit: SignUpUiEvent()

    data class TypeEmail(val email: String): SignUpUiEvent()

    data class TypePassword(val password: String): SignUpUiEvent()
}