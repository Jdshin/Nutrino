package edu.utap.nutrino.ui

import android.provider.Settings.Global.getString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.utap.nutrino.R
import edu.utap.nutrino.api.Recipe
import edu.utap.nutrino.api.SpoonApi
import edu.utap.nutrino.api.SpoonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val spoonApi = SpoonApi.create()
    private val repository = SpoonRepository(spoonApi)

    private val recipeResults = MutableLiveData<List<Recipe>>()

    fun netRecipes(apiKey : String){
        viewModelScope.launch (context = viewModelScope.coroutineContext + Dispatchers.IO) {
            recipeResults.postValue(repository.getRecipes("complexSearch", apiKey, "1"))
        }
    }

    fun observeRecipes() : LiveData<List<Recipe>>?{
        return recipeResults
    }
}