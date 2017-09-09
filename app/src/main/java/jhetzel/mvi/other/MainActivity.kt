package jhetzel.mvi.other
/*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import jhetzel.tasks.R
import jhetzel.tasks.data.Rep
import jhetzel.tasks.data.UserService
import jhetzel.tasks.real.domain.SignUpState
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
    }

    fun first() {
        val disposables: CompositeDisposable = CompositeDisposable()

        val service: UserService = Rep()

        disposables += RxView.clicks(button)
                .doOnNext {
                    button.isEnabled = false
                    progressBar.visibility = View.VISIBLE
                }
                .map { editTextEmail.text.toString() }                   // 1. reach the UI imperatively -> jump out of the stream, what if not in main thread?
                .flatMap(service::signUp)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { progressBar.visibility = View.INVISIBLE }   // 2. same as 1, also an error, gets not called onError, so progressBar keeps running onError!
                                                                        // Multiple requests at once will fight each other reaching the ui
                .subscribe({
                    finish()
                },{
                    button.isEnabled = true
                    Toast.makeText(this, "Failed to set email: ${it.message}", Toast.LENGTH_SHORT).show() // 3. Error is a Terminal event, so the Stream won't be there anymore
                })
    }

    fun second() {
        val disposables: CompositeDisposable = CompositeDisposable()

        val service: UserService = Rep()

        disposables += RxView.clicks(button)
                .map { UIEvent.Submit(editTextEmail.text.toString()) }
                .flatMap { service.signUp(it.name)
                        .map { SignUpState(false, true, null) as SignUpState }
                        .onErrorReturn { SignUpState(false, false, it.message) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .startWith{ SignUpState(true, false, null) as SignUpState }}
                .subscribe({
                    button.isEnabled = !it.checkingEmail
                    progressBar.visibility = if(it.checkingEmail) View.VISIBLE else View.INVISIBLE
                    if(!it.checkingEmail) {
                        if(it.signedUp) finish()
                        else Toast.makeText(this, "Failed to set email: ${it.errorMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
    }


    fun third() {
        val disposables: CompositeDisposable = CompositeDisposable()

        val service: UserService = Rep()

        val events: Observable<UIEvent.Submit> = RxView.clicks(button)
                .map { UIEvent.Submit(editTextEmail.text.toString()) }

        val model: Observable<SignUpState> = events
                .flatMap { service.signUp(it.name)
                        .map { SignUpState(false, true, null) as SignUpState }
                        .onErrorReturn { SignUpState(false, false, it.message) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .startWith{ SignUpState(true, false, null) as SignUpState }}

        disposables += model.subscribe({
                    button.isEnabled = !it.checkingEmail
                    progressBar.visibility = if(it.checkingEmail) View.VISIBLE else View.INVISIBLE
                    if(!it.checkingEmail) {
                        if(it.signedUp) finish()
                        else Toast.makeText(this, "Failed to set email: ${it.errorMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
    }


    fun fourth() {
        val disposables: CompositeDisposable = CompositeDisposable()

        val service: UserService = Rep()

        val events: Observable<UIEvent.Submit> = RxView.clicks(button)
                .map { UIEvent.Submit(editTextEmail.text.toString()) }

        val submit: ObservableTransformer<UIEvent.Submit, SignUpState> = ObservableTransformer {
            it.flatMap { service.signUp(it.name)
                    .map { SignUpState(false, true, null) as SignUpState }
                    .onErrorReturn { SignUpState(false, false, it.message) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .startWith{ SignUpState(true, false, null) as SignUpState }}
        }

        disposables += events
                .compose(submit)
                .subscribe({
            button.isEnabled = !it.checkingEmail
            progressBar.visibility = if(it.checkingEmail) View.VISIBLE else View.INVISIBLE
            if(!it.checkingEmail) {
                if(it.signedUp) finish()
                else Toast.makeText(this, "Failed to set email: ${it.errorMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun fifth() {
        val disposables: CompositeDisposable = CompositeDisposable()

        val service: UserService = Rep()

        // INTENT
        val checkNameEvents: Observable<UIEvent.CheckName> = RxTextView.afterTextChangeEvents(editTextEmail)
                .map { UIEvent.CheckName(it.editable().toString()) }

        val submitEvents: Observable<UIEvent.Submit> = RxView.clicks(button)
                .map { UIEvent.Submit(editTextEmail.text.toString()) }

        val events: Observable<UIEvent> = Observable.merge(checkNameEvents, submitEvents)

        // MODEL
        val submit: ObservableTransformer<UIEvent.Submit, SignUpState> = ObservableTransformer {
            it.flatMap { service.signUp(it.name)
                    .map { SignUpState(false, true, null) as SignUpState }
                    .onErrorReturn { SignUpState(false, false, it.message) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .startWith{ SignUpState(true, false, null) as SignUpState }}
        }

        val checkName: ObservableTransformer<UIEvent.CheckName, SignUpState> = ObservableTransformer {
            it.flatMap { service.signUp(it.name)
                    .map { SignUpState(false, true, null) as SignUpState }
                    .onErrorReturn { SignUpState(false, false, it.message) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .startWith{ SignUpState(true, false, null) as SignUpState }}
        }

        val models: ObservableTransformer<UIEvent, SignUpState> = ObservableTransformer {
            it.publish {
                Observable.merge(
                        it.ofType<UIEvent.Submit>().compose(submit),
                        it.ofType<UIEvent.CheckName>().compose(checkName)
                )
            }
        }

        TODO("38:00 vllt früher noch")

        // VIEW
        disposables += events
                .compose(models)
                .subscribe({
                    button.isEnabled = !it.checkingEmail
                    progressBar.visibility = if(it.checkingEmail) View.VISIBLE else View.INVISIBLE
                    if(!it.checkingEmail) {
                        if(it.signedUp) finish()
                        else Toast.makeText(this, "Failed to set email: ${it.errorMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
    }


    fun sixtht() {
        val disposables: CompositeDisposable = CompositeDisposable()

        val service: UserService = Rep()

        // INTENT
        val checkNameEvents: Observable<UIEvent.CheckName> = RxTextView.afterTextChangeEvents(editTextEmail)
                .map { UIEvent.CheckName(it.editable().toString()) }

        val submitEvents: Observable<UIEvent.Submit> = RxView.clicks(button)
                .map { UIEvent.Submit(editTextEmail.text.toString()) }

        val events: Observable<UIEvent> = Observable.merge(checkNameEvents, submitEvents)



        // MODEL
        val submit: ObservableTransformer<Action.Submit, Result> = ObservableTransformer {
            it.flatMap { service.signUp(it.name)
                    .map { Result.Submit.Success as Result }
                    .onErrorReturn { Result.Failure(it.message) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .startWith{ Result.Submit.InFlight }}
        }

        val checkName: ObservableTransformer<Action.CheckName, Result> = ObservableTransformer {
            it.flatMap { service.signUp(it.name)
                    .map { Result.CheckName.Success as Result }
                    .onErrorReturn { Result.Failure(it.message) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .startWith{ Result.CheckName.InFlight }}
        }

        val results: ObservableTransformer<Action, Result> = ObservableTransformer {
            it.publish {
                Observable.merge(
                        it.ofType<Action.Submit>().compose(submit),
                        it.ofType<Action.CheckName>().compose(checkName)
                )
            }
        }

        val uiModels: Observable<SignUpState> = actions.compose(results).scan<SignUpState>(SignUpState.INITIAL, { state, result -> when(result){
            is Result.Submit -> when(result) {
                Result.Submit.Success -> TODO()
                Result.Submit.InFlight -> TODO()
            }
            is Result.CheckName.InFlight -> state.copy(checkingEmail = true)
            is Result.CheckName.Success -> SignUpState.INITIAL
            is Result.Submit.Success -> state.copy(signedUp = true, checkingEmail = false)
            is Result.Failure -> TODO()
            else -> state.copy()
        }})

        // VIEW
        disposables += uiModels
                .subscribe({
                    button.isEnabled = !it.checkingEmail
                    progressBar.visibility = if(it.checkingEmail) View.VISIBLE else View.INVISIBLE
                    if(!it.checkingEmail) {
                        if(it.signedUp) finish()
                        else Toast.makeText(this, "Failed to set email: ${it.errorMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
    }


}
*/