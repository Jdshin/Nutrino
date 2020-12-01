package edu.utap.nutrino.ui

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.nutrino.MainActivity
import edu.utap.nutrino.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val spoonApi = SpoonApi.create()
    private val repository = SpoonRepository(spoonApi)
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    private val recipeResults = MutableLiveData<List<Recipe>>()
    private val savedRecipeResults = MutableLiveData<List<Recipe>>()
    private val savedRecipeList = mutableListOf<Recipe>()

    private val shoppingCart = MutableLiveData<List<String>>()
    private val shoppingCartList = mutableListOf<String>()
    private val shoppingCartListMap = mutableMapOf<String, List<RecipeIngredient>>()

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
            recipeResults.postValue(repository.getRecipeEndpoint(apiKey, "6", searchText))
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
        // BELOW: debugging saved recipes to make sure tag switch works so RecipeList files can be recycled
    }

    fun getFavRecipes() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            savedRecipeResults.value = listOf()
        }
        else {
            db.collection("UserData")
                    .document(MainActivity.userEmail)
                    .collection("FavoriteRecipes")
                    .limit(50)
                    .addSnapshotListener{ querySnapshot, ex ->
                        if (ex != null) {
                            return@addSnapshotListener
                        }
                        else {
                            savedRecipeResults.value = querySnapshot!!.documents.mapNotNull {
                                it.toObject(Recipe::class.java)
                            }
                        }
                    }
        }
    }

    fun addToShoppingCart(recipe: Recipe) {
        if (recipe != null) {
            shoppingCartListMap[recipe.key.toString()] = recipe.nutrition!!.ingredients!!
        }
    }

    fun observeShoppingCart() : LiveData<List<String>> {
        return shoppingCart
    }

    fun updateShoppingCart() {
        shoppingCartListMap.values.forEach { list ->
            list.map { ingredient ->
                if (!shoppingCartList.contains(ingredient.name)) {
                    ingredient.name?.let { shoppingCartList.add(it) }
                    shoppingCart.postValue(shoppingCartList)
                }
            }
        }
    }

    fun observeRecipes() : LiveData<List<Recipe>>{
        return recipeResults
    }

    fun observeSavedRecipes() : LiveData<List<Recipe>> {
        return savedRecipeResults
    }

    fun setOneRecipe(recipe : Recipe) {
        oneRecipe = recipe
    }

    fun getOneRecipe() : Recipe {
        return oneRecipe
    }
}