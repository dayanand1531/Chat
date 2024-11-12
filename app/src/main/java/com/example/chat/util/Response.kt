package com.example.chat.util

data class Response<T>( val responseStatus: ResponseStatus,
                        val isLoading:Boolean = false,
                        val data: T? = null,
                        val message: String? = null){
    companion object {

        fun <T> success(data: T,isSuccess:Boolean): Response<T> {
            return Response(ResponseStatus.SUCCESS, data = data, isLoading = isSuccess)
        }

        fun <T> error(errorMessage: String,isError:Boolean): Response<T> {
            return Response(ResponseStatus.ERROR,isError ,null, message = errorMessage)
        }

        fun <T> loading(isLoading: Boolean): Response<T> {
            return Response(ResponseStatus.LOADING, isLoading = isLoading)
        }
    }

}
