package jhetzel.mvi.signup


data class SignUpState(
        val checkingEmail: Int,
        val signingUp: Boolean,
        val signedUp: Boolean,
        val passwordSafe: Boolean,
        val emailAvailable: Boolean,
        val errorMessage: Int?
) {
    companion object {
        val INITIAL = SignUpState(
                0,
                false,
                false,
                false,
                false,
                null

        )
    }
}