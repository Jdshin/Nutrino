package edu.utap.nutrino

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Property
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.firebase.ui.auth.AuthUI
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.nutrino.ui.MainFragment
import java.util.*
import edu.utap.nutrino.SecretsManager.Companion.spoonacular_api_key
import edu.utap.nutrino.SecretsManager.Companion.spoonacular_api_key_3
import java.lang.System.exit

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
    private var sm: SecretsManager = SecretsManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Thread.setDefaultUncaughtExceptionHandler(GeneralExceptionHandler(this))

        if (!sm.getBoolFromKey("exceptionHit", this)) {
            doSignIn()
        }

        else {
            Toast.makeText(this, "Crash encountered. Please sign in again.", Toast.LENGTH_SHORT).show()
            supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            sm.setKBoolPair("exceptionHit", false, this) // Reset to false since exception has been handled
            doSignIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(javaClass.simpleName, "onActivityResult")
        when (requestCode) {
            userAuthRequestCode -> {
                if (resultCode == RESULT_OK) {
                    Log.i("User Email: ", userEmail)
                    spoonApiKey = sm.getValueFromKey(spoonacular_api_key_3, applicationContext)
                    if (spoonApiKey != "") {
                        mainFragment = MainFragment.newInstance()
                        initMainFragment()
                    }
                    else {
                        fetchKeyFromDb(spoonacular_api_key_3)
                    }
                }
                else {
                    Log.d(javaClass.simpleName, "The sign-in stage failed.")
                    finish()
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
                spoonApiKey = result.getString(spoonacular_api_key_3).toString()
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