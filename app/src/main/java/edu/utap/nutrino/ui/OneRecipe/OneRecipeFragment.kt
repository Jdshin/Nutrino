package edu.utap.nutrino.ui.OneRecipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.nutrino.R
import edu.utap.nutrino.glide.Glide
import edu.utap.nutrino.ui.MainViewModel

class OneRecipeFragment : Fragment() {

    private val viewModel : MainViewModel by activityViewModels()

    private lateinit var adapterIngredientsAdapter : OneRecipeIngredientsListAdapter


    companion object{
        fun newInstance() : OneRecipeFragment {
            return OneRecipeFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.one_recipe_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val oneRecipe = viewModel.getOneRecipe()

        val one_recipe_IV = view.findViewById<ImageView>(R.id.one_recipe_IV)
        val one_recipe_title_TV = view.findViewById<TextView>(R.id.one_recipe_title_TV)
        val one_recipe_score_TV = view.findViewById<TextView>(R.id.one_recipe_score_TV)
        val one_recipe_ready_time_TV = view.findViewById<TextView>(R.id.one_recipe_ready_time_TV)

        initAdapters(view)

        if (oneRecipe.imageURL != null) {
            Glide.glideFetch(oneRecipe.imageURL, one_recipe_IV)
        }

        one_recipe_title_TV.text = oneRecipe.title
        one_recipe_score_TV.text = oneRecipe.spoonacularScore.toString()
        one_recipe_ready_time_TV.text = "Ready in ${oneRecipe.readyInMinutes.toString()} minutes"
        adapterIngredientsAdapter.submitList(oneRecipe.nutrition.ingredients)
    }

    private fun initAdapters(view: View) {
        adapterIngredientsAdapter = OneRecipeIngredientsListAdapter(viewModel)
    }
}