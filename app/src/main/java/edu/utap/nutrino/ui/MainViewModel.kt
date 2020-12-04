package edu.utap.nutrino.ui

import android.util.Log
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

    private val shoppingCart = MutableLiveData<List<String>>()
    private var shoppingCartList = mutableListOf<String>()
    private val shoppingCartRecipes = MutableLiveData<List<Recipe>>()
    private val shoppingCartListMap = mutableMapOf<String, List<RecipeIngredient>>()

    private var userProfileIntolList = mutableListOf<String>()
    private var userDietType : String? = null

    private lateinit var userCreds : UserCreds
    private lateinit var userDocRef : DocumentReference
    private lateinit var oneRecipe : Recipe



    fun initFireBaseRefs() {
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
            userDocRef = db.collection("UserData").document(MainActivity.userEmail)
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

    fun netRecipes(apiKey : String, searchText: String) {
        viewModelScope.launch (context = viewModelScope.coroutineContext + Dispatchers.IO) {
            var userIntoleranceStr = userProfileIntolList.joinToString(",")
            Log.i("Net Recipes: ", userIntoleranceStr)
            recipeResults.postValue(repository.getRecipeEndpoint(apiKey, "5", searchText, userDietType, userIntoleranceStr))
        }
    }

    fun addFavRecipe(recipe : Recipe) {
        viewModelScope.launch (viewModelScope.coroutineContext + Dispatchers.IO) {
            userDocRef.collection("FavoriteRecipes").document(recipe.key.toString()).set(recipe)
        }
    }

    fun removeFavRecipe(recipe: Recipe) {
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
            userDocRef.collection("FavoriteRecipes").document(recipe.key.toString()).delete()
        }
    }

    fun getFavRecipes() {
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
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
    }

    fun addToShoppingCart(recipe: Recipe) {
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
            userDocRef.collection("ShoppingCart").document(recipe.key.toString()).set(recipe)
        }
    }

    fun removeFromShoppingCart(recipe: Recipe) {
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
            userDocRef.collection("ShoppingCart").document(recipe.key.toString()).delete()
        }
    }

    fun netShoppingCart() {
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
            if (FirebaseAuth.getInstance().currentUser == null) {
                shoppingCartRecipes.value = listOf()
            }
            else {
                db.collection("UserData").document(MainActivity.userEmail)
                    .collection("ShoppingCart")
                    .limit(50)
                    .addSnapshotListener{ querySnapshot, ex ->
                        if (ex != null) {
                            return@addSnapshotListener
                        }
                        else {
                            shoppingCartRecipes.value = querySnapshot!!.documents.mapNotNull {
                                it.toObject(Recipe::class.java)
                            }
                        }
                    }
            }
        }
    }

    fun updateShoppingList() {
        Log.i("UPDATING SHOPPING CART LIST: ", " OK")
        shoppingCartList.clear()
        if (shoppingCartRecipes.value != null) {
            shoppingCartRecipes.value?.forEach {recipe ->
                Log.i("RECIPE KEY: ", recipe.key.toString())
                recipe.nutrition!!.ingredients!!.forEach {recipeIngredient ->
                    Log.i("RECIPE INGREDIENT: ", recipeIngredient.name.toString())
                    if (!shoppingCartList.contains(recipeIngredient.name)) {
                        shoppingCartList.add(recipeIngredient.name!!)
                    }
                }
            }
        }
        Log.i("SHOPPING CART LIST: ", shoppingCartList.toString())
        shoppingCart.value = shoppingCartList
        Log.i("SHOPPING CART LIVE DATA: ", shoppingCart.value.toString())
    }

    //TODO implement button in shopping cart fragment to clear all items in cart
    fun clearShoppingCart() {
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
            if (shoppingCartRecipes.value != null) {
                for (recipe in shoppingCartRecipes.value!!) {
                    userDocRef.collection("ShoppingCart").document(recipe.key.toString()).delete()
                }
            }
        }
    }

    fun setOneRecipe(recipe : Recipe) {
        oneRecipe = recipe
    }

    fun getOneRecipe() : Recipe {
        return oneRecipe
    }

    fun observeShoppingCart() : LiveData<List<String>> {
        return shoppingCart
    }

    fun observeShoppingCartRecipes() : LiveData<List<Recipe>>{
        return shoppingCartRecipes
    }

    fun observeSavedRecipes() : LiveData<List<Recipe>> {
        return savedRecipeResults
    }

    fun observeRecipes() : LiveData<List<Recipe>>{
        return recipeResults
    }
}