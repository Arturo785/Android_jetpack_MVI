package com.ar.jetpackarchitecture.api.main

import androidx.lifecycle.LiveData
import com.ar.jetpackarchitecture.api.GenericResponse
import com.ar.jetpackarchitecture.models.AccountProperties
import com.ar.jetpackarchitecture.util.GenericApiResponse
import retrofit2.http.*

interface OpenApiMainService {

    @GET("account/properties")
    fun getAccountProperties(
        @Header("Authorization") authorization : String // the token
    ): LiveData<GenericApiResponse<AccountProperties>>

    @PUT("account/properties/update")
    @FormUrlEncoded
    fun saveAccountProperties(
        @Header("Authorization") authorization: String,
        @Field("email") email : String,
        @Field("username") username : String,
    ) : LiveData<GenericApiResponse<GenericResponse>>


    @PUT("account/change_password/")
    @FormUrlEncoded
    fun updatePassword(
        @Header("Authorization") authorization: String,
        @Field("old_password") oldPassword : String,
        @Field("new_password") newPassword : String,
        @Field("confirm_new_password") confirmNewPassword : String,
    ) : LiveData<GenericApiResponse<GenericResponse>>

}