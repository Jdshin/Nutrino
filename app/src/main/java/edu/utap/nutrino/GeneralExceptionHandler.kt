package edu.utap.nutrino

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import com.firebase.ui.auth.AuthUI
import androidx.fragment.app.FragmentManager

class GeneralExceptionHandler(private val activity: Activity) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        var sm = SecretsManager()

        // Clear out EncryptedSharedPrefs, store exception indicator in it, and sign out the user
        var nutrinoApp = NutrinoApplication.getInstance()
        sm.clearSharedPrefs(nutrinoApp.applicationContext)
        sm.setKBoolPair("exceptionHit", true, nutrinoApp.applicationContext)

        AuthUI.getInstance().signOut(nutrinoApp.applicationContext)

        // Go back to MainActivity, which in turn will make us go back to sign out screen
        var intent: Intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TASK
                    or Intent.FLAG_ACTIVITY_NEW_TASK
        )
        startActivity(activity, intent, Bundle())
    }
}