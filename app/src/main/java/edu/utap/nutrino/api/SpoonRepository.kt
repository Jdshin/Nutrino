package edu.utap.nutrino.api

class SpoonRepository(private val spoonApi : SpoonApi) {

    suspend fun getRecipes (search_by : String, apiKey : String, number : String) : List<Recipe>? {
        return spoonApi.getRecipes(search_by, apiKey, number).results
    }

}