package edu.utap.nutrino.api

import com.google.gson.annotations.SerializedName

data class Recipe (
    @SerializedName("id")
    val key: Int,
    @SerializedName("calories")
    val calories: Int,
    @SerializedName("carbs")
    val carbs: String,
    @SerializedName("fat")
    val fat: String,
    @SerializedName("image")
    val imageURL: String,
    @SerializedName("imageType")
    val imageType: String,
    @SerializedName("protein")
    val protein: String,
    @SerializedName("title")
    val title: String
)