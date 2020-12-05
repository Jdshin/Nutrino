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
import edu.utap.nutrino.ui.ShoppingCart.ShoppingCartFragment

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

        val welcomeTV = view.findViewById<TextView>(R.id.welcomeTV)
        val displayName = FirebaseAuth.getInstance().currentUser!!.displayName
        MainActivity.userEmail = FirebaseAuth.getInstance().currentUser!!.email.toString()
        if (displayName.isNullOrBlank()) {
            welcomeTV.text = "Hello!"
        }
        else {
            val welcomeString = "Hello, ${displayName}!"
            welcomeTV.text = welcomeString
        }
      
        if (MainActivity.userEmail.isNotEmpty()) {
            var userPostData = SpoonApi.UserPostData(MainActivity.userEmail)
            //viewModel.connectUser(userPostData, MainActivity.spoonApiKey)
        }

        viewModel.initFireBaseRefs()
        viewModel.netShoppingCart()
        viewModel.getFavRecipes()

        val getRecipeBut = view.findViewById<Button>(R.id.getRecipeBut)
        val savedRecipeBut = view.findViewById<Button>(R.id.savedRecipeBut)
        val profileBut = view.findViewById<Button>(R.id.profileBut)
        val shoppingCartBut = view.findViewById<Button>(R.id.shoppingCartBut)
        val logoutBut = view.findViewById<Button>(R.id.logoutBut)
        initClickListeners(getRecipeBut, savedRecipeBut, profileBut, shoppingCartBut, logoutBut)

    }

    private fun initClickListeners(
        getRecipeBut: Button,
        savedRecipeBut: Button,
        profileBut: Button,
        shoppingCartBut: Button,
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
            viewModel.getFavRecipes()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.main_container, RecipeListFragment.newInstance(), MainActivity.savedRecipeFragTag)
                .addToBackStack(MainActivity.mainFragTag)
                .commit()
        }

        profileBut.setOnClickListener{
            parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_container, UserProfileFragment.newInstance())
                    .addToBackStack(MainActivity.userProfileFragTag)
                    .commit()
        }

        shoppingCartBut.setOnClickListener{
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.main_container, ShoppingCartFragment.newInstance())
                .addToBackStack(MainActivity.mainFragTag)
                .commit()
        }

        logoutBut.setOnClickListener{
            AuthUI.getInstance().signOut(requireContext())
            activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
            activity!!.recreate()
        }
    }
}