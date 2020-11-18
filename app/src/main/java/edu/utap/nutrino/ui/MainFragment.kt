package edu.utap.nutrino.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import edu.utap.nutrino.MainActivity
import edu.utap.nutrino.R

class MainFragment : Fragment() {

    private val viewModel : MainViewModel by activityViewModels()
    private val recipeListFragTag = "recipeListTag"

    companion object{
        fun newInstance() : MainFragment {
            return MainFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var welcomeTV = view.findViewById<TextView>(R.id.welcomeTV)
        var displayName = FirebaseAuth.getInstance().currentUser!!.displayName
        if (displayName.isNullOrBlank()) {
            welcomeTV.text = "Hello!"
        }
        else {
            welcomeTV.text = "Hello, " + displayName + "!"
        }

        var getRecipeBut = view.findViewById<Button>(R.id.getRecipeBut)
        var mealPlanBut = view.findViewById<Button>(R.id.mealPlanBut)
        var profileBut = view.findViewById<Button>(R.id.profileBut)
        var settingsBut = view.findViewById<Button>(R.id.settingsBut)
        var logoutBut = view.findViewById<Button>(R.id.logoutBut)
        initClickListeners(getRecipeBut, mealPlanBut, profileBut, settingsBut, logoutBut)

    }

    private fun initClickListeners(
        getRecipeBut: Button,
        mealPlanBut: Button,
        profileBut: Button,
        settingsBut: Button,
        logoutBut: Button
    ) {
        getRecipeBut.setOnClickListener{
            viewModel.netRecipes(getString(R.string.Spoonacular_API_KEY))
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.main_container, RecipeListFragment.newInstance())
                .addToBackStack(recipeListFragTag)
                .commit()
        }

        mealPlanBut.setOnClickListener{

        }

        profileBut.setOnClickListener{

        }

        settingsBut.setOnClickListener{

        }

        logoutBut.setOnClickListener{
            AuthUI.getInstance().signOut(requireContext())
            activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
            activity!!.recreate()
        }
    }
}