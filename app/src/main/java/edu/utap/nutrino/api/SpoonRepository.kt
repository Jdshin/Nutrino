package edu.utap.nutrino.api

class SpoonRepository(private val spoonApi : SpoonApi) {

    //TODO Update arguments for getRecipes
    suspend fun getRecipeEndpoint (apiKey : String, number : String) : List<Recipe>? {
        return spoonApi.getRecipeEndpoint(apiKey, number, null, null, null, null, null, null, null).results
    }
}