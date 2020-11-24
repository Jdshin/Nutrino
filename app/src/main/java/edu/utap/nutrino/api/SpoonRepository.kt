package edu.utap.nutrino.api

import android.util.Log

class SpoonRepository(private val spoonApi : SpoonApi) {

    //TODO Update arguments for getRecipes
    //TODO prevent crash with no API key
    suspend fun getRecipeEndpoint (apiKey : String, number : String, searchText : String) : List<Recipe>? {
        return spoonApi.getRecipeEndpoint(
            apiKey,
            number,
            searchText,
            null,
            null,
            null,
            null,
            addRecipeInformation = true,
            addRecipeNutrition = true,
            null,
            true).results
    }

    suspend fun connectUser (body: SpoonApi.UserPostData, apiKey: String) : UserCreds {
        val userCred = spoonApi.connectUser(body, apiKey)
        Log.i("Username : ", userCred.username)
        Log.i("User Hash", userCred.hash)
        return userCred
    }
}