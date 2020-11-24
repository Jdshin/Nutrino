package edu.utap.nutrino.api

import com.google.gson.annotations.SerializedName

data class Recipe (
    @SerializedName("id")
    val key: Int,
    @SerializedName("image")
    val imageURL: String,
    @SerializedName("imageType")
    val imageType: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("spoonacularScore")
    val spoonacularScore: Int,
    @SerializedName("pricePerServing")
    val pricePerServing: Double,
    @SerializedName("readyInMinutes")
    val readyInMinutes: Int,
    @SerializedName("nutrition")
    val nutrients: NutritionInfo
)

data class NutritionInfo (
        @SerializedName("nutrients")
        val nutrients : List<Nutrient>,
        @SerializedName("ingredients")
        val ingredients : List<RecipeIngredients>
)

data class RecipeIngredients (
        @SerializedName("name")
        val name : String,
        @SerializedName("amount")
        val amount : Double,
        @SerializedName("unit")
        val unit : String,
        @SerializedName("nutrients")
        val nutrients : List<Nutrient>
)

data class Nutrient (
        @SerializedName("title")
        val title : String,
        @SerializedName("amount")
        val amount : Double,
        @SerializedName("unit")
        val unit : String
)

