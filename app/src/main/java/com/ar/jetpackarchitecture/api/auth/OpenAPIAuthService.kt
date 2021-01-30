package com.ar.jetpackarchitecture.api.auth

import com.ar.jetpackarchitecture.api.auth.network_responses.LoginResponse
import com.ar.jetpackarchitecture.api.auth.network_responses.RegistrationResponse
import com.ar.jetpackarchitecture.di.auth.AuthScope
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

@AuthScope
interface OpenAPIAuthService {

    @POST("account/login")
    @FormUrlEncoded
   suspend fun login(
        @Field("username") email : String,
        @Field("password") password : String,
    ) : LoginResponse

    @POST("account/register")
    @FormUrlEncoded
   suspend fun register(
        @Field("email") email : String,
        @Field("username") username : String,
        @Field("password") password : String,
        @Field("password2") password2 : String
    ) : RegistrationResponse

}