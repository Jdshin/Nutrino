package edu.utap.nutrino.api

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Spoonacular API

interface SpoonApi {

    /* Changing Path parameter here will enable all functionality listed under "Recipes" in the Spoonacular doc,
       limit number to less than 5 to minimize quota usage for the free plan
       apiKey is stored in strings.xml for now but remember to delete before commit. */

    @GET("/recipes/{search_by}")
    suspend fun getRecipes (
        @Path("search_by") search_by : String,
        @Query("apiKey") apiKey : String,
        @Query("number") number : String
    ) : RecipeResponse

    data class RecipeResponse (val results: List<Recipe>)

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