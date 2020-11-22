package edu.utap.nutrino

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentTransaction
import edu.utap.nutrino.ui.MainFragment

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "Nutrino"
        const val userAuthRequestCode = 10
        var userEmail = ""
    }
    private lateinit var mainFragment : MainFragment

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