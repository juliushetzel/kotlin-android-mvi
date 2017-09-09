package jhetzel.mvi.signup

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import jhetzel.mvi.R
import jhetzel.mvi.base.BaseActivity
import jhetzel.mvi.base.BaseContract
import jhetzel.mvi.data.UserService
import jhetzel.mvi.extension.Nullable
import jhetzel.mvi.signup.operation.CheckEmailAvailableOperation
import jhetzel.mvi.signup.operation.CheckSafePasswordOperation
import jhetzel.mvi.signup.operation.SignUpOperation
import kotlinx.android.synthetic.main.activity_main.*


class SignUpActivity : BaseActivity<SignUpUiEvent, SignUpState>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        RxView.clicks(buttonLogin)
                .map { SignUpUiEvent.ClickSubmit }
                .subscribe(events)

        RxTextView.afterTextChangeEvents(editTextEmail)
                .skipInitialValue()
                .map { SignUpUiEvent.TypeEmail(it.editable().toString()) }
                .subscribe(events)

        RxTextView.afterTextChangeEvents(editTextPassword)
                .skip(1)
                .map { SignUpUiEvent.TypePassword(it.editable().toString()) }
                .subscribe(events)
    }

    override fun initContract(): BaseContract<SignUpUiEvent, SignUpState> = SignUpContract(
            SignUpOperation(UserService.INSTANCE),
            CheckEmailAvailableOperation(UserService.INSTANCE),
            CheckSafePasswordOperation(),
            SignUpReducer(SignUpState.INITIAL)
    )

    override fun render(state: Observable<SignUpState>) {

        state.subscribe { Log.d("${this.javaClass.simpleName}", "state: $it")}

        state.map { it.checkingEmail > 0 || it.signingUp }
                .subscribe { progressBar.visibility = if(it) View.VISIBLE else View.INVISIBLE }

        state.map { it.signingUp }
                .subscribe {
                    editTextEmail.isEnabled = !it
                    editTextPassword.isEnabled = !it
                }

        state.map { it.signedUp }
                .distinctUntilChanged()
                .filter { it }
                .subscribe { Toast.makeText(this, "Successfully signed up!", Toast.LENGTH_SHORT).show() }

        state.map { Nullable(it.errorMessage) }
                .filter { it.isNotNull() }
                .distinctUntilChanged()
                .subscribe { it.ifNotNull(this::showError) }

        state.map { it.passwordSafe && it.emailAvailable && !it.signingUp }
                .distinctUntilChanged()
                .subscribe { buttonLogin.isEnabled = it }
    }

    private fun showError(errorResId: Int) {
        val errorMessage: String = getString(errorResId)
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

}