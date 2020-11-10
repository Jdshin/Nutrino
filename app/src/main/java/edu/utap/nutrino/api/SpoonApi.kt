package edu.utap.nutrino.api

import android.net.wifi.hotspot2.pps.Credential
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

// Spoonacular API

interface SpoonApi {

    /*
       https://spoonacular.com/food-api/docs#Search-Recipes-Complex
       limit number to less than 5 to minimize quota usage for the free plan
       apiKey is stored in strings.xml for now but remember to delete before commit.
       COSTS 1PT + 0.01 PTS PER NUMBER OF RESULTS*/

    data class RecipeResponse (val results: List<Recipe>)

    @GET("/recipes/complexSearch")
    suspend fun getRecipeEndpoint (
        @Query("apiKey") apiKey : String,
        @Query("number") number : String,
        @Query("cuisine") cuisineListString : String?,
        @Query("excludeCuisine") excludeCuisineListString : String?,
        @Query("diet") diet : String?,
        @Query("intolerances") intolerances : String?,
        @Query("addRecipeInformation") addRecipeInformation : Boolean?,
        @Query("addRecipeNutrition") addRecipeNutrition : Boolean?,
        @Query("titleMatch") titleMatch : String?,
    ) : RecipeResponse

    /* In order to use some of the endpoints in Spoonacular, need to get a unique username and hash from spoonacular by sending POST request for usercredentials,
    need to store user credentials either through shared preferences or firebase storage, prob better on shared prefs for speed.
     */
    @POST("/users/connect")
    suspend fun connectUser() : UserCreds

    //TODO need meal planning endpoint call : https://spoonacular.com/food-api/docs#Generate-Meal-Plan
    //TODO need to connect user to spoonacular : https://spoonacular.com/food-api/docs#Connect-User

    companion object {
        var httpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("api.spoonacular.com")
            .build()

        fun create(): SpoonApi = create(httpUrl)
        private fun create(httpUrl: HttpUrl) : SpoonApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SpoonApi::class.java)
        }
    }
}