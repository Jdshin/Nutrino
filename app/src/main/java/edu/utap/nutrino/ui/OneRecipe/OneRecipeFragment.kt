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
import androidx.recyclerview.widget.RecyclerView
import edu.utap.nutrino.R
import edu.utap.nutrino.glide.Glide
import edu.utap.nutrino.ui.MainViewModel

class OneRecipeFragment : Fragment() {

    private val viewModel : MainViewModel by activityViewModels()

    private lateinit var adapterIngredientsAdapter : OneRecipeIngredientsAdapter
    private lateinit var adapterInstructionsAdapter : OneRecipeInstructionsAdapter

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

        val one_recipe_IV = view.findViewById<ImageView>(R.id.one_recipe_IV)
        val one_recipe_title_TV = view.findViewById<TextView>(R.id.one_recipe_title_TV)
        val one_recipe_score_TV = view.findViewById<TextView>(R.id.one_recipe_score_TV)
        val one_recipe_ready_time_TV = view.findViewById<TextView>(R.id.one_recipe_ready_time_TV)
        val oneRecipeFavIV = view.findViewById<ImageView>(R.id.one_recipe_fav_IV)
        val oneRecipeCartIV = view.findViewById<ImageView>(R.id.one_recipe_cart_but)

        val oneRecipe = viewModel.getOneRecipe()
        val savedRecipes = viewModel.observeSavedRecipes().value
        val shoppingCartRecipes = viewModel.observeShoppingCartRecipes().value
        initAdapters(view)

        one_recipe_title_TV.text = oneRecipe.title
        one_recipe_score_TV.text = "Rating${"\n"}${oneRecipe.spoonacularScore.toString()}"
        one_recipe_ready_time_TV.text = "Cook Time${"\n"}${oneRecipe.readyInMinutes.toString()} minutes"
        if (oneRecipe.imageURL != null) {
            Glide.glideFetch(oneRecipe.imageURL, one_recipe_IV)
        }

        if (savedRecipes != null && savedRecipes.contains(oneRecipe)) {
            oneRecipeFavIV.setImageResource(R.drawable.ic_favorite_black_24dp)
        } else {
            oneRecipeFavIV.setImageResource(R.drawable.ic_favorite_border_black_24dp)
        }

        if (shoppingCartRecipes != null && shoppingCartRecipes.contains(oneRecipe)) {
            oneRecipeCartIV.setImageResource(R.drawable.ic_check_mark)
        } else {
            oneRecipeCartIV.setImageResource(R.drawable.ic_shopping_cart_icon)
        }

        oneRecipeFavIV.setOnClickListener{
            if (savedRecipes != null && !savedRecipes.contains(oneRecipe)) {
                viewModel.addFavRecipe(oneRecipe)
                oneRecipeFavIV.setImageResource(R.drawable.ic_favorite_black_24dp)
            }
            else {
                viewModel.removeFavRecipe(oneRecipe)
                oneRecipeFavIV.setImageResource(R.drawable.ic_favorite_border_black_24dp)
            }
            viewModel.getFavRecipes()
        }

        oneRecipeCartIV.setOnClickListener{
            if (shoppingCartRecipes != null && shoppingCartRecipes.contains(oneRecipe)) {
                viewModel.removeFromShoppingCart(oneRecipe)
                oneRecipeCartIV.setImageResource(R.drawable.ic_shopping_cart_icon)
            }
            else {
                viewModel.addToShoppingCart(oneRecipe)
                oneRecipeCartIV.setImageResource(R.drawable.ic_check_mark)
            }
            viewModel.updateShoppingList()
        }

        adapterIngredientsAdapter.submitList(oneRecipe.nutrition!!.ingredients)
        adapterInstructionsAdapter.submitList(oneRecipe.analyzedInstructions?.get(0)?.recipeSteps)
    }

    private fun initAdapters(view: View) {
        adapterIngredientsAdapter = OneRecipeIngredientsAdapter(viewModel)
        adapterInstructionsAdapter = OneRecipeInstructionsAdapter(viewModel)

        val ingredientsRV = view.findViewById<RecyclerView>(R.id.one_recipe_ingredients_RV)
        val instructionsRV = view.findViewById<RecyclerView>(R.id.one_recipe_instructions_RV)

        ingredientsRV.layoutManager = LinearLayoutManager(view.context)
        instructionsRV.layoutManager = LinearLayoutManager(view.context)

        ingredientsRV.adapter = adapterIngredientsAdapter
        instructionsRV.adapter = adapterInstructionsAdapter
    }
}