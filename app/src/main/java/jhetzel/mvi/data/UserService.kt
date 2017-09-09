package jhetzel.mvi.data

import io.reactivex.Observable


interface UserService {

    fun signUp(name: String, password: String): Observable<Any>

    fun emailAvailable(email: String): Observable<Boolean>

    companion object {
        val INSTANCE: UserService = InMemoryUserService(true)
    }

}