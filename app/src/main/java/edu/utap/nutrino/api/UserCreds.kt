package edu.utap.nutrino.api

import com.google.gson.annotations.SerializedName

data class UserCreds (
    @SerializedName ("username")
    val username: String,
    @SerializedName ("hash")
    val hash: String
)