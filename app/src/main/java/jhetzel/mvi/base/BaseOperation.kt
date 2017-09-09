package jhetzel.mvi.base

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer


abstract class BaseOperation<A: BaseAction> : ObservableTransformer<A, BaseResult> {

    final override fun apply(upstream: Observable<A>): ObservableSource<BaseResult> = buildOperation(upstream)

    abstract fun buildOperation(action: Observable<A>): Observable<BaseResult>

}
