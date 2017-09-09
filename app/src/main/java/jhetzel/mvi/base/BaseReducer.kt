package jhetzel.mvi.base


interface BaseReducer<S> {
    val initialState: S

    fun reduce(currentState: S, result: BaseResult): S
}