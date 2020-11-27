package edu.utap.nutrino

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Property
import androidx.fragment.app.FragmentTransaction
import edu.utap.nutrino.ui.MainFragment
import java.util.*
import edu.utap.nutrino.SecretsManager.Companion.spoonacular_api_key

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "Nutrino"
        const val recipeListFragTag = "recipeListTag"
        const val recipeSearchFragTag = "recipeSearchFragTag"
        const val userAuthRequestCode = 10
        var userEmail = ""
        var spoonApiKey = ""
    }

    private lateinit var mainFragment: MainFragment

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
                    spoonApiKey = SecretsManager().getValueFromKey(spoonacular_api_key, applicationContext)
                    mainFragment = MainFragment.newInstance()
                    initMainFragment()
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
}