package com.dapoi.pitjarustest.data.source.remote.network

import com.dapoi.pitjarustest.data.source.remote.model.LoginResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("api/sariroti_md/index.php/login/loginTest")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse
}