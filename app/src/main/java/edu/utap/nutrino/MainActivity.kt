package edu.utap.nutrino

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Property
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.nutrino.ui.MainFragment
import java.util.*
import edu.utap.nutrino.SecretsManager.Companion.spoonacular_api_key

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "Nutrino"
        const val mainFragTag = "mainFragTag"
        const val recipeResultsFragTag = "recipeListTag"
        const val recipeSearchFragTag = "recipeSearchFragTag"
        const val userProfileFragTag = "userProfileFragTag"
        const val savedRecipeFragTag = "savedRecipeFragTag"
        const val oneRecipeFragTag = "oneRecipeFragTag"
        const val userAuthRequestCode = 10
        var userEmail = ""
        var spoonApiKey: String = ""
    }

    private lateinit var mainFragment: MainFragment
    private lateinit var sm: SecretsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        doSignIn()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(javaClass.simpleName, "onActivityResult")
        when (requestCode) {
            userAuthRequestCode -> {
                if (resultCode == RESULT_OK) {
                    Log.i("User Email: ", userEmail)
                    sm = SecretsManager()
                    spoonApiKey = sm.getValueFromKey(spoonacular_api_key, applicationContext)
                    if (spoonApiKey != "") {
                        mainFragment = MainFragment.newInstance()
                        initMainFragment()
                    }
                    else {
                        fetchKeyFromDb(spoonacular_api_key)
                    }
                } else {
                    Log.d(javaClass.simpleName, "The sign-in stage failed.")
                }
            }
        }
    }

    private fun initMainFragment() {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.main_container, mainFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
    }

    private fun doSignIn() {
        val userAuthIntent = Intent(this, UserAuthActivity::class.java)
        startActivityForResult(userAuthIntent, userAuthRequestCode)
    }

    private fun fetchKeyFromDb(key: String) {
        var db: FirebaseFirestore = FirebaseFirestore.getInstance()
        db.collection("Keys").document("ApiKeys").get()
            .addOnSuccessListener { result ->
                Log.d("MainActivity", "Fetching key from DB succeeded.")
                spoonApiKey = result.getString(spoonacular_api_key).toString()
                // Update EncryptedSharedPreferences
                sm.setKvPair(key, spoonApiKey, this)
                mainFragment = MainFragment.newInstance()
                initMainFragment()
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Fetching key from DB failed ", it)
            }
    }
}