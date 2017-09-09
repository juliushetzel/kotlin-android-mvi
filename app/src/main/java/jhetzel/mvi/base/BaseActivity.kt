package jhetzel.mvi.base

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatActivity
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import jhetzel.mvi.R


abstract class BaseActivity<E, S> : AppCompatActivity() {

    override fun onRetainCustomNonConfigurationInstance(): Any {
        return contract!!
    }

    private lateinit var state: Relay<S>
    private lateinit var contractDisposable: Disposable

    private var contract: BaseContract<E, S>? = null

    lateinit var events: Relay<E>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        events = PublishRelay.create()
        state = PublishRelay.create()
        contract = lastCustomNonConfigurationInstance as BaseContract<E, S>?

        if(contract == null) {
            contract = initContract()
        }
        render(state)
    }

    @CallSuper
    override fun onStart() {
        super.onStart()

        contractDisposable = contract!!
                .sign(events)
                .subscribe(state)
    }

    @CallSuper
    override fun onStop() {
        super.onStop()

        contractDisposable.dispose()
    }

    abstract fun initContract(): BaseContract<E, S>

    abstract fun render(state: Observable<S>)
}