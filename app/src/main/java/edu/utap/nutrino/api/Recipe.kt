package edu.utap.nutrino.api

import com.google.gson.annotations.SerializedName

data class Recipe (
    @SerializedName("id")
    val key: Int? = null,
    @SerializedName("image")
    val imageURL: String? = null,
    @SerializedName("imageType")
    val imageType: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("spoonacularScore")
    val spoonacularScore: Int? = null,
    @SerializedName("pricePerServing")
    val pricePerServing: Double? = null,
    @SerializedName("readyInMinutes")
    val readyInMinutes: Int? = null,
    @SerializedName("nutrition")
    val nutrition: NutritionInfo? = null,
    @SerializedName("analyzedInstructions")
    val analyzedInstructions : List<AnalyzedInstructions>? = listOf<AnalyzedInstructions>()
)

data class NutritionInfo (
        @SerializedName("nutrients")
        val nutrients : List<Nutrient>? = null,
        @SerializedName("ingredients")
        val ingredients : List<RecipeIngredient>? = null
)

data class RecipeIngredient (
        @SerializedName("name")
        val name : String? = null,
        @SerializedName("amount")
        val amount : Double? = null,
        @SerializedName("unit")
        val unit : String? = null
)

data class Nutrient (
        @SerializedName("title")
        val title : String? = null,
        @SerializedName("amount")
        val amount : Double? = null,
        @SerializedName("unit")
        val unit : String? = null
)

data class AnalyzedInstructions (
        @SerializedName("steps")
        val recipeSteps: List<RecipeInstruction>? = null
)

data class RecipeInstruction (
        @SerializedName("number")
        val stepNumber : Int? = null,
        @SerializedName("step")
        val instructionString : String? = null
)

data class UserProfile (
        @SerializedName ("intolerances")
        val intolList : List<String> = listOf(),
        @SerializedName ("dietType")
        val dietType : String? = ""
)

