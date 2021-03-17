package com.barry.kotlin_code_base.network

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {
    @GET
    suspend fun get(
        @HeaderMap headers: Map<String, String>,
        @Url url: String
    ): Response<String>

    @GET
    suspend fun get(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @QueryMap params: Map<String, String>
    ): Response<String>

    @POST
    suspend fun post(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @Body requestBody: RequestBody
    ): Response<String>

    @PUT
    suspend fun put(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @Body requestBody: RequestBody
    ): Response<String>

    @HTTP(method = "DELETE", hasBody = true)
    suspend fun delete(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @Body requestBody: RequestBody
    ): Response<String>

    @PATCH
    suspend fun patch(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @Body requestBody: RequestBody
    ): Response<String>
}