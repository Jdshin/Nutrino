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
    @SerializedName("spoonacularScore")
    val spoonacularScore: Int,
    @SerializedName("pricePerServing")
    val pricePerServing: Double,
    @SerializedName("readyInMinutes")
    val readyInMinutes: Int,
    @SerializedName("nutrition")
    val nutrition: NutritionInfo,
    @SerializedName("analyzedInstructions")
    val analyzedInstructions : List<AnalyzedInstructions>
)

data class NutritionInfo (
        @SerializedName("nutrients")
        val nutrients : List<Nutrient>,
        @SerializedName("ingredients")
        val ingredients : List<RecipeIngredient>
)

data class RecipeIngredient (
        @SerializedName("name")
        val name : String,
        @SerializedName("amount")
        val amount : Double,
        @SerializedName("unit")
        val unit : String
)

data class Nutrient (
        @SerializedName("title")
        val title : String,
        @SerializedName("amount")
        val amount : Double,
        @SerializedName("unit")
        val unit : String
)

data class AnalyzedInstructions (
        @SerializedName("steps")
        val recipeSteps: List<RecipeInstruction>
)

data class RecipeInstruction (
        @SerializedName("number")
        val stepNumber : Int,
        @SerializedName("step")
        val instructionString : String
)

