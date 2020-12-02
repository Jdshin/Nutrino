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
import edu.utap.nutrino.R
import edu.utap.nutrino.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class MainViewModel : ViewModel() {
    private val spoonApi = SpoonApi.create()
    private val repository = SpoonRepository(spoonApi)
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    private val recipeResults = MutableLiveData<List<Recipe>>()
    private val savedRecipeResults = MutableLiveData<List<Recipe>>()

    private val shoppingCart = MutableLiveData<List<String>>()
    private val shoppingCartList = mutableListOf<String>()
    private val shoppingCartListMap = mutableMapOf<String, List<RecipeIngredient>>()

    private var userProfile : UserProfile? = UserProfile()

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
            recipeResults.postValue(repository.getRecipeEndpoint(apiKey, "1", searchText))
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
        shoppingCartListMap[recipe.key.toString()] = recipe.nutrition!!.ingredients!!
    }

    fun removeFromShoppingCart(recipe: Recipe) {
        shoppingCartListMap.remove(recipe.key.toString())
    }

    fun updateShoppingCart() {
        shoppingCartList.clear()
        shoppingCartListMap.values.forEach { list ->
            list.map { ingredient ->
                if (!shoppingCartList.contains(ingredient.name)) {
                    ingredient.name?.let { shoppingCartList.add(it) }
                    shoppingCart.postValue(shoppingCartList)
                }
            }
        }
        db.collection("UserData")
                .document(MainActivity.userEmail)
                .collection("ShoppingCart")
                .document()
                .set(shoppingCartListMap)
    }

    fun clearShoppingCart() {
        shoppingCartListMap.clear()
        shoppingCartList.clear()
        shoppingCart.postValue(shoppingCartList)
    }

    fun netUserProfile() {
        viewModelScope.launch (viewModelScope.coroutineContext + Dispatchers.IO) {
            val query = db.collection("UserData")
                    .document(MainActivity.userEmail)
                    .collection("UserDiet")
                    .document("UserDiet")
                    .get()
            query.addOnSuccessListener {
                userProfile = it.toObject(UserProfile::class.java)
                Log.i("User Diet Retrieved", " Success")
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

    fun observeShoppingCartListMap() : Map<String, List<RecipeIngredient>>{
        return shoppingCartListMap
    }

    fun observeSavedRecipes() : LiveData<List<Recipe>> {
        return savedRecipeResults
    }

    fun observeRecipes() : LiveData<List<Recipe>>{
        return recipeResults
    }
}