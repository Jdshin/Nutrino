package edu.utap.nutrino.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.nutrino.BuildConfig
import edu.utap.nutrino.MainActivity
import edu.utap.nutrino.R
import kotlinx.android.synthetic.main.fragment_rv.*
import java.util.*

class RecipeSearchFragment : Fragment() {
    private val viewModel : MainViewModel by activityViewModels()

    private lateinit var recipeSearchET : EditText
    private lateinit var recipeSearchBut : Button

    companion object {
        fun newInstance() : RecipeSearchFragment {
            return RecipeSearchFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.recipe_search_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recipeSearchET = view.findViewById(R.id.recipe_search_ET)
        recipeSearchBut = view.findViewById(R.id.recipe_search_but)
        setRecipeSearchBut(recipeSearchET, recipeSearchBut)
    }

    private fun setRecipeSearchBut(editText: EditText, button: Button) {
        button.setOnClickListener{
            Log.i("Search Text: ", editText.text.toString())
            viewModel.netRecipes(getString(R.string.Spoonacular_API_KEY), editText.text.toString())
            parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_container, RecipeListFragment.newInstance())
                    .addToBackStack(MainActivity.recipeListFragTag)
                    .commit()
        }
    }
}