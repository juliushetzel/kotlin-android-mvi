package jhetzel.mvi.other



/*
interface Result{

    sealed class CheckName: Result {
        object Success: CheckName()
        object InFlight: CheckName()
    }

    sealed class Submit: Result {
        object Success: Submit()
        object InFlight: Submit()
    }

    data class Failure(val errorMessage: String?): Result
}*/

// interface BaseResult -> sealed classes implement BaseResult -> unterteilen