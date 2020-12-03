package edu.utap.nutrino

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SecretsManager {
    companion object {
        // "spoonacular_api_key_1" is an actual key value in the EncryptedSharedPreferences (there may be multiple possible keys for same API)
        const val spoonacular_api_key = "spoonacular_api_key_1"
        // const val spoonacular_api_key = "spoonacular_api_key_2"
    }

    private var masterKeyAlias: String? = null
    private var sharedPrefs: SharedPreferences? = null

    // Starter code from here:  https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences
    // Return "" to signal that the key is either nonexistent or stale and will need to fetch from the DB
    fun getValueFromKey(theKey: String, context: Context): String {
        if (masterKeyAlias == null) {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

            sharedPrefs= EncryptedSharedPreferences.create(
                    "secret_shared_prefs",
                    masterKeyAlias!!,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }

        if (sharedPrefs!!.contains(theKey)) {



            // Test the validity of the key (it may be stale) to determine whether or not to re-fetch from DB


            // Key is fresh so we're good to go!
            return sharedPrefs!!.getString(theKey, "")!!
        }
        else {
            return ""
        }
    }

    // Starter code from here:  https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences
    fun setKvPair(theKey: String, theValue: String, context: Context) {
        if (masterKeyAlias == null) {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

            sharedPrefs = EncryptedSharedPreferences.create(
                    "secret_shared_prefs",
                    masterKeyAlias!!,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
        sharedPrefs!!.edit().putString(theKey, theValue).commit()
    }
}