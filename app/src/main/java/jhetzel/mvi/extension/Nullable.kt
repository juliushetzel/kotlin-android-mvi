package jhetzel.mvi.extension


class Nullable<T>(
        val value: T?
) {
    fun ifNotNull(consume: (value: T) -> Unit) {
        if(value != null) consume(value)
    }

    fun isNull(): Boolean = value == null

    fun isNotNull(): Boolean = value != null
}