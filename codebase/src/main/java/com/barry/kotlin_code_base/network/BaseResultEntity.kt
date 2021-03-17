package com.barry.kotlin_code_base.network

sealed class BaseResultEntity<out T> {
    data class ApiSuccess<out T>(val data: T) : BaseResultEntity<T>()
    data class ApiError(val code : Int,val msg: String) : BaseResultEntity<Nothing>()
    data class ApiException(val exception: Exception) : BaseResultEntity<Nothing>()
}