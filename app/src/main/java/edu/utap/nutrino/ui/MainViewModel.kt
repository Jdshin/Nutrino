package edu.utap.nutrino.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import edu.utap.nutrino.api.Recipe
import edu.utap.nutrino.api.SpoonApi
import edu.utap.nutrino.api.SpoonRepository
import edu.utap.nutrino.api.UserCreds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val spoonApi = SpoonApi.create()
    private val repository = SpoonRepository(spoonApi)
    private lateinit var userCreds : UserCreds
    private var db : FirebaseFirestore = FirebaseFirestore.getInstance()

    private val recipeResults = MutableLiveData<List<Recipe>>()

    fun netRecipes(apiKey : String){
        viewModelScope.launch (context = viewModelScope.coroutineContext + Dispatchers.IO) {
            recipeResults.postValue(repository.getRecipeEndpoint(apiKey, "5"))
        }
    }

    fun connectUser(body : SpoonApi.UserPostData) {
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            userCreds = repository.connectUser(body)
            db.collection(body.email).document("UserCreds").set(userCreds)
            Log.i("Username: ", userCreds.username)
            Log.i("Hash: ", userCreds.hash)
        }
    }

    fun observeRecipes() : LiveData<List<Recipe>>{
        return recipeResults
    }
}