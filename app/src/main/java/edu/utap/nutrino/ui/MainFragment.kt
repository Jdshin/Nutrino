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
import edu.utap.nutrino.api.SpoonApi
import edu.utap.nutrino.ui.RecipeList.RecipeListFragment
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    private val viewModel : MainViewModel by activityViewModels()

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
        MainActivity.userEmail = FirebaseAuth.getInstance().currentUser!!.email.toString()
        if (displayName.isNullOrBlank()) {
            welcomeTV.text = "Hello!"
        }
        else {
            welcomeTV.text = "Hello, " + displayName + "!"
        }
      
        if (MainActivity.userEmail.isNotEmpty()){
            var userPostData = SpoonApi.UserPostData(MainActivity.userEmail)
            viewModel.connectUser(userPostData, getString(R.string.Spoonacular_API_KEY))
        }

        viewModel.initFireBaseRefs()

        var getRecipeBut = view.findViewById<Button>(R.id.getRecipeBut)
        var mealPlanBut = view.findViewById<Button>(R.id.savedRecipeBut)
        var profileBut = view.findViewById<Button>(R.id.profileBut)
        var logoutBut = view.findViewById<Button>(R.id.logoutBut)
        initClickListeners(getRecipeBut, mealPlanBut, profileBut, logoutBut)

    }

    private fun initClickListeners(
        getRecipeBut: Button,
        mealPlanBut: Button,
        profileBut: Button,
        logoutBut: Button
    ) {
        getRecipeBut.setOnClickListener{
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.main_container, RecipeSearchFragment.newInstance(), MainActivity.recipeSearchFragTag)
                .addToBackStack(MainActivity.mainFragTag)
                .commit()
        }

        savedRecipeBut.setOnClickListener{
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.main_container, RecipeListFragment.newInstance(), MainActivity.savedRecipeFragTag)
                .addToBackStack(MainActivity.mainFragTag)
                .commit()
        }

        profileBut.setOnClickListener{

        }

        logoutBut.setOnClickListener{
            AuthUI.getInstance().signOut(requireContext())
            activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
            activity!!.recreate()
        }
    }
}