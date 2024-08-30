package com.example.chat.util

data class Response<T>( val responseStatus: ResponseStatus,
                        val data: T? = null,
                        val message: String? = null){
    companion object {

        fun <T> success(data: T): Response<T> {
            return Response(ResponseStatus.SUCCESS, data = data)
        }

        fun <T> error(errorMessage: String): Response<T> {
            return Response(ResponseStatus.ERROR, null, message = errorMessage)
        }

        fun <T> loading(): Response<T> {
            return Response(ResponseStatus.LOADING)
        }
    }

}
