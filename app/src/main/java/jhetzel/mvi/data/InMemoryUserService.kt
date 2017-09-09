package jhetzel.mvi.data

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import jhetzel.mvi.error.NetworkException
import java.util.*
import java.util.concurrent.TimeUnit


class InMemoryUserService(
        val forceError: Boolean
) : UserService {

    private val signUpRelay: PublishRelay<Any> = PublishRelay.create()
    private val checkEmailRelay: PublishRelay<Boolean> = PublishRelay.create()

    override fun signUp(name: String, password: String): Observable<Any> {
        Observable.create<Any> {
            if(forceError){
                randomCall(0, {
                    it.onNext(Object())
                }, {
                    it.onError(NetworkException())
                })
            }else{
                it.onNext(Object())
            }
        }.delay(10000, TimeUnit.MILLISECONDS).subscribe(signUpRelay)

        return signUpRelay
    }

    override fun emailAvailable(email: String): Observable<Boolean> {
        return Observable.create<Boolean> {
            if (forceError) {
                randomCall(0, {
                    it.onNext(email == "email")
                }, {
                    it.onError(NetworkException())
                })
            } else {
                it.onNext(email == "email")
            }
        }.delay(4000, TimeUnit.MILLISECONDS)
    }

    private fun randomCall(failRate: Int, onSuccess: () -> Unit, onFail: () -> Unit) {
        if (failRate < 0 || failRate > 100) throw IllegalStateException("Fail rate should be between 0 and 100. Was $failRate")
        val result = Random().nextInt(100)

        if (failRate > result) {
            onFail.invoke()
        } else {
            onSuccess.invoke()
        }
    }

}