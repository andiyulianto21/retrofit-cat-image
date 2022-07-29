package com.example.cobaretrofitapi.pojo

import com.google.gson.annotations.SerializedName

data class Users(
    val id: Int,
    val name: String,
    val email: String,
    val image_url: String
)
