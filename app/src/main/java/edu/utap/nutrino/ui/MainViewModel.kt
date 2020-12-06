package edu.utap.nutrino.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.nutrino.MainActivity
import edu.utap.nutrino.R
import edu.utap.nutrino.api.*
import edu.utap.nutrino.ui.ShoppingCart.ShoppingCartAdapter
import kotlinx.android.synthetic.main.user_profile_frag.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel : ViewModel() {
    private val spoonApi = SpoonApi.create()
    private val repository = SpoonRepository(spoonApi)
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val recipeResults = MutableLiveData<List<Recipe>>()
    private val savedRecipeResults = MutableLiveData<List<Recipe>>()

    private val shoppingCartIngredients = MutableLiveData<List<String>>()
    private val shoppingCartRecipes = MutableLiveData<List<Recipe>>()

    private var savedRecipesList = mutableListOf<Recipe>()
    private var shoppingCartList = mutableListOf<String>()
    private var shoppingCartRecipesList = mutableListOf<Recipe>()

    private var userProfileIntolList = mutableListOf<String>()
    private var userDietType: String? = null

    private lateinit var userCreds: UserCreds
    private lateinit var userDocRef: DocumentReference
    private lateinit var oneRecipe: Recipe


    fun initFireBaseRefs() {
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
            try {
                userDocRef = db.collection("UserData").document(MainActivity.userEmail)
            } catch (e: Exception) {
                Log.i("Firebase retrieval error: ", e.toString())
            }
        }
    }


    fun connectUser(body: SpoonApi.UserPostData, apiKey: String) {
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            userCreds = repository.connectUser(body, apiKey)
            val userEmailMap = hashMapOf<String, String>("email" to body.email)
            userDocRef.set(userEmailMap)
            userDocRef.collection("UserCred").document("UserCred").set(userCreds)
        }
    }

    fun netRecipes(apiKey: String, searchText: String) {
        try {
            viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
                val userIntoleranceStr = userProfileIntolList.joinToString(",")
                recipeResults.postValue(repository.getRecipeEndpoint(apiKey, "20", searchText, userDietType, userIntoleranceStr))
            }
        } catch (e: HttpException) {
            recipeResults.value = (listOf(Recipe(title = "Network error, try again")))
        }
    }

    fun addFavRecipe(recipe: Recipe) {
        try {
            viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
                userDocRef.collection("FavoriteRecipes").document(recipe.key.toString()).set(recipe)
                savedRecipesList.add(recipe)
                savedRecipeResults.postValue(savedRecipesList)
            }
        } catch (e: FirebaseException) {}
    }

    fun removeFavRecipe(recipe: Recipe) {
        try {
            viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
                userDocRef.collection("FavoriteRecipes").document(recipe.key.toString()).delete()
                savedRecipesList.remove(recipe)
                savedRecipeResults.postValue(savedRecipesList)
            }
        } catch (e: FirebaseException) {}
    }

    fun getFavRecipes() {
        try {
            viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
                if (FirebaseAuth.getInstance().currentUser == null) {
                    savedRecipeResults.value = listOf()
                } else {
                    db.collection("UserData")
                            .document(MainActivity.userEmail)
                            .collection("FavoriteRecipes")
                            .limit(50)
                            .addSnapshotListener { querySnapshot, ex ->
                                if (ex != null) {
                                    return@addSnapshotListener
                                } else {
                                    savedRecipesList = querySnapshot!!.documents.mapNotNull {
                                        it.toObject(Recipe::class.java)
                                    } as MutableList<Recipe>
                                    savedRecipeResults.postValue(savedRecipesList)
                                }
                            }
                }
            }
        } catch (e: FirebaseException) {}
    }

    fun addToShoppingCart(recipe: Recipe) {
        try {
            viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
                userDocRef.collection("ShoppingCart").document(recipe.key.toString()).set(recipe)
                shoppingCartRecipesList.add(recipe)
                shoppingCartRecipes.postValue(shoppingCartRecipesList)
            }
        } catch (e: FirebaseException) {}
    }

    fun removeFromShoppingCart(recipe: Recipe) {
        try {
            viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
                userDocRef.collection("ShoppingCart").document(recipe.key.toString()).delete()
                shoppingCartRecipesList.remove(recipe)
                shoppingCartRecipes.postValue(shoppingCartRecipesList)
            }
        } catch (e: FirebaseException) {}
    }

    fun netShoppingCart() {
        try {
            viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
                if (FirebaseAuth.getInstance().currentUser == null) {
                    shoppingCartRecipes.value = listOf()
                } else {
                    db.collection("UserData").document(MainActivity.userEmail)
                            .collection("ShoppingCart")
                            .limit(50)
                            .addSnapshotListener { querySnapshot, ex ->
                                if (ex != null) {
                                    return@addSnapshotListener
                                } else {
                                    shoppingCartRecipesList = querySnapshot!!.documents.mapNotNull {
                                        it.toObject(Recipe::class.java)
                                    } as MutableList<Recipe>
                                }
                            }
                }
            }
        } catch (e: FirebaseException) {}
    }

    fun updateShoppingList() {
        shoppingCartList.clear()
        shoppingCartRecipesList.forEach { recipe ->
            recipe.nutrition!!.ingredients!!.forEach { recipeIngredient ->
                if (!shoppingCartList.contains(recipeIngredient.name)) {
                    shoppingCartList.add(recipeIngredient.name!!)
                }
            }
        }
        shoppingCartIngredients.value = shoppingCartList
    }

    fun clearShoppingCart() {
        try {
            viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
                for (recipe in shoppingCartRecipesList) {
                    userDocRef.collection("ShoppingCart").document(recipe.key.toString()).delete().addOnCompleteListener {
                        shoppingCartRecipesList.clear()
                        shoppingCartList.clear()
                        shoppingCartIngredients.postValue(shoppingCartList)
                    }
                }
            }
        } catch (e: FirebaseException) { }
    }

    fun setOneRecipe(recipe: Recipe) {
        oneRecipe = recipe
    }

    fun getOneRecipe(): Recipe {
        return oneRecipe
    }

    fun observeShoppingCart(): LiveData<List<String>> {
        return shoppingCartIngredients
    }

    fun observeSavedRecipes(): LiveData<List<Recipe>> {
        return savedRecipeResults
    }

    fun observeRecipes(): LiveData<List<Recipe>> {
        return recipeResults
    }

    fun recipeInSavedList(recipe: Recipe): Boolean {
        return savedRecipesList.contains(recipe)
    }

    fun recipeInShoppingCart(recipe: Recipe): Boolean {
        return shoppingCartRecipesList.contains(recipe)
    }

    fun updateProfile(dietInfo: HashMap<String, Any>) {
        try {
            viewModelScope.launch {
                db.collection("UserData").document(MainActivity.userEmail).collection("UserDiet")
                        .document("UserDiet").set(dietInfo)
                        .addOnSuccessListener { _ ->
                            Log.d(MainActivity.userProfileFragTag, "Success adding diet info.")
                        }
                        .addOnFailureListener { e ->
                            Log.w(MainActivity.userProfileFragTag, "Error adding diet info", e)
                        }
            }
        } catch (e: FirebaseException) {}
    }
}
