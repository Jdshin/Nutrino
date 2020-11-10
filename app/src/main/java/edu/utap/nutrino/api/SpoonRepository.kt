package edu.utap.nutrino.api

import android.util.Log

class SpoonRepository(private val spoonApi : SpoonApi) {

    //TODO Update arguments for getRecipes
    //TODO prevent crash with no API key
    suspend fun getRecipeEndpoint (apiKey : String, number : String) : List<Recipe>? {
        return spoonApi.getRecipeEndpoint(apiKey, number, null, null, null, null, null, null, null).results
    }

    suspend fun connectUser () : UserCreds {
        val userCred = spoonApi.connectUser()
        Log.i("Username : ", userCred.username)
        Log.i("User Hash", userCred.hash)
        return userCred
    }
}