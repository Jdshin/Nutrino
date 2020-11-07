package edu.utap.nutrino

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import edu.utap.nutrino.ui.MainFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mainFragment : MainFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainFragment = MainFragment.newInstance()
        initMainFragment()
    }

    private fun initMainFragment(){
        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_container, mainFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
}