package com.example.cobaretrofitapi

import com.example.cobaretrofitapi.pojo.ResponseChanged
import com.example.cobaretrofitapi.pojo.ResponseUpload
import com.example.cobaretrofitapi.pojo.Users
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface JsonPlaceholderApi {

    @GET("user.php")
    fun getUserImage(): Call<List<Users>>

    @Multipart
    @POST("edit.php")
    fun updateUser(
        @Part imageEdit: MultipartBody.Part,
        @Part("id") id: RequestBody,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody
    ): Call<ResponseChanged>

    @Multipart
    @POST("edit.php")
    fun updateUser(
        @Part("id") id: RequestBody,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody
    ): Call<ResponseChanged>

    @Multipart
    @POST("image.php")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
    ): Call<ResponseUpload>
}