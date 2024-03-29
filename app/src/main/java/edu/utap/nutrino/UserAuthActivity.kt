package edu.utap.nutrino

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// This class was inspired by Flipped Classroom #8 but with modifications
class UserAuthActivity : AppCompatActivity() {
    companion object {
        val rcSignIn = 9000
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FirebaseAuth.getInstance().currentUser != null) {
            setResult(RESULT_OK)
            finish()
        } else {
            // Create and launch sign-in intent. TODO: Implement smart lock if there is time.
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setIsSmartLockEnabled(false)
                    .build(),
                rcSignIn
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == rcSignIn) {
            val response = IdpResponse.fromResultIntent(data)
            if (Firebase.auth.currentUser != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    MainActivity.userEmail = Firebase.auth.currentUser!!.email.toString()
                }
            }

            Log.d(javaClass.simpleName, "activity result $resultCode")
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                // Can send back bundle with content if needed
                setResult(RESULT_OK)
                finish()
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Log.d(MainActivity.TAG, "Error signing in", response?.error)
                finish()
            }
        }
    }
}