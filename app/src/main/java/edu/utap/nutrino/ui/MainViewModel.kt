package edu.utap.nutrino.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.nutrino.MainActivity
import edu.utap.nutrino.api.Recipe
import edu.utap.nutrino.api.SpoonApi
import edu.utap.nutrino.api.SpoonRepository
import edu.utap.nutrino.api.UserCreds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val spoonApi = SpoonApi.create()
    private val repository = SpoonRepository(spoonApi)
    private var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val recipeResults = MutableLiveData<List<Recipe>>()

    private lateinit var userCreds : UserCreds
    private lateinit var userDocRef : DocumentReference
    private lateinit var oneRecipe : Recipe



    fun initFireBaseRefs() {
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
            userDocRef = db.collection("UserData").document(MainActivity.userEmail)
        }
    }

    fun netRecipes(apiKey : String, searchText: String) {
        viewModelScope.launch (context = viewModelScope.coroutineContext + Dispatchers.IO) {
            recipeResults.postValue(repository.getRecipeEndpoint(apiKey, "1", searchText))
        }
    }

    fun connectUser(body : SpoonApi.UserPostData, apiKey: String) {
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            userCreds = repository.connectUser(body, apiKey)
            var userEmailMap = hashMapOf<String, String>("email" to body.email)
            userDocRef.set(userEmailMap)
            userDocRef.collection("UserCred").document("UserCred").set(userCreds)
            Log.i("Username: ", userCreds.username)
            Log.i("Hash: ", userCreds.hash)
        }
    }

    fun addFavRecipe(recipe : Recipe) {
        viewModelScope.launch (viewModelScope.coroutineContext + Dispatchers.IO) {
            userDocRef.collection("FavoriteRecipes").document(recipe.key.toString()).set(recipe)
        }
    }

    fun setOneRecipe(recipe : Recipe) {
        oneRecipe = recipe
    }

    fun getOneRecipe() : Recipe {
        return oneRecipe
    }

    fun observeRecipes() : LiveData<List<Recipe>>{
        return recipeResults
    }
}