package jhetzel.mvi.base

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable


abstract class BaseContract<E, S>(
        val stateReducer: BaseReducer<S>
) {

    private val events: PublishRelay<E> = PublishRelay.create()

    private val state: Observable<S> = events
            .compose { it.publish(this::translateIntentions) }
            .compose { it.publish(this::mapInteractions) }
            .scan<S>(stateReducer.initialState, stateReducer::reduce)
            .replay(1)
            .autoConnect()

    fun sign(events: Observable<E>): Observable<S> {
        events.subscribe(this.events)
        return state
    }

    abstract protected fun translateIntentions(events: Observable<E>): Observable<BaseAction>

    abstract fun mapInteractions(actions: Observable<BaseAction>): Observable<BaseResult>

}