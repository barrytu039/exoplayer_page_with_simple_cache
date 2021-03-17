package com.barry.kotlin_code_base.network

import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class RetrofitWorker {
    private var retrofitService : RetrofitService
    private var retrofit : Retrofit
    private var okHttpClient : OkHttpClient

    init {
        okHttpClient = OkHttpProvider.clientBuilder.build()
        retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        retrofitService = retrofit.create(RetrofitService::class.java)
    }


    suspend fun startGetRequest(header : Map<String,String>, url : String, params: Map<String, String>?) : Response<String> {
        return if (params.isNullOrEmpty()) {
            retrofitService.get(headers = header, url = url)
        } else {
            retrofitService.get(headers = header, url = url, params = params)
        }
    }

    suspend fun startPostRequest(header : Map<String,String>, url : String, requestBody: RequestBody) : Response<*> {
        return retrofitService.post(headers = header, url = url, requestBody = requestBody)
    }

    suspend fun startDeleteRequest(header : Map<String,String>, url : String, requestBody: RequestBody) : Response<*> {
        return retrofitService.delete(headers = header, url = url, requestBody = requestBody)
    }

    suspend fun startPutRequest(header : Map<String,String>, url : String, requestBody: RequestBody) : Response<*> {
        return retrofitService.put(headers = header, url = url, requestBody = requestBody)
    }

    suspend fun startPatchRequest(header : Map<String,String>, url : String, requestBody: RequestBody) : Response<*> {
        return retrofitService.patch(headers = header, url = url, requestBody = requestBody)
    }
}