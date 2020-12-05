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

    private var savedRecipesList = mutableListOf<Recipe>()
    private var shoppingCartRecipesList = mutableListOf<Recipe>()

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
        }
    }

    fun netRecipes(apiKey : String, searchText: String) {
        viewModelScope.launch (context = viewModelScope.coroutineContext + Dispatchers.IO) {
            var userIntoleranceStr = userProfileIntolList.joinToString(",")
            recipeResults.postValue(repository.getRecipeEndpoint(apiKey, "5", searchText, userDietType, userIntoleranceStr))
        }
    }

    fun addFavRecipe(recipe : Recipe) {
        viewModelScope.launch (viewModelScope.coroutineContext + Dispatchers.IO) {
            userDocRef.collection("FavoriteRecipes").document(recipe.key.toString()).set(recipe)
            savedRecipesList.add(recipe)
            savedRecipeResults.postValue(savedRecipesList)
        }
    }

    fun removeFavRecipe(recipe: Recipe) {
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
            userDocRef.collection("FavoriteRecipes").document(recipe.key.toString()).delete()
            savedRecipesList.remove(recipe)
            savedRecipeResults.postValue(savedRecipesList)
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
                                savedRecipesList = querySnapshot!!.documents.mapNotNull {
                                    it.toObject(Recipe::class.java)
                                } as MutableList<Recipe>
                                savedRecipeResults.postValue(savedRecipesList)
                            }
                        }
            }
        }
    }

    fun addToShoppingCart(recipe: Recipe) {
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
            userDocRef.collection("ShoppingCart").document(recipe.key.toString()).set(recipe)
            shoppingCartRecipesList.add(recipe)
            shoppingCartRecipes.postValue(shoppingCartRecipesList)
        }
    }

    fun removeFromShoppingCart(recipe: Recipe) {
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
            userDocRef.collection("ShoppingCart").document(recipe.key.toString()).delete()
            shoppingCartRecipesList.remove(recipe)
            shoppingCartRecipes.postValue(shoppingCartRecipesList)
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
                            shoppingCartRecipesList = querySnapshot!!.documents.mapNotNull {
                                it.toObject(Recipe::class.java)
                            } as MutableList<Recipe>
                        }
                    }
            }
        }
    }

    fun updateShoppingList() {
        shoppingCartList.clear()
        if (shoppingCartRecipesList != null) {
            shoppingCartRecipesList.forEach {recipe ->
                recipe.nutrition!!.ingredients!!.forEach {recipeIngredient ->
                    if (!shoppingCartList.contains(recipeIngredient.name)) {
                        shoppingCartList.add(recipeIngredient.name!!)
                    }
                }
            }
        }
        shoppingCart.value = shoppingCartList
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

    fun recipeInSavedList(recipe: Recipe) : Boolean {
        return savedRecipesList.contains(recipe)
    }

    fun recipeInShoppingCart(recipe: Recipe) : Boolean {
        return shoppingCartRecipesList.contains(recipe)
    }
}