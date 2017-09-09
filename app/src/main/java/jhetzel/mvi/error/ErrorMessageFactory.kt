package jhetzel.mvi.error

import jhetzel.mvi.R


class ErrorMessageFactory {

    companion object {
        fun get(error: Throwable): Int = when(error) {

            else -> {
                R.string.error_unknown
            }
        }
    }
}