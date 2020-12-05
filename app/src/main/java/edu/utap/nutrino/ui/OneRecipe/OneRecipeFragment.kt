package edu.utap.nutrino.ui.OneRecipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
        initAdapters(view)

        one_recipe_title_TV.text = oneRecipe.title
        one_recipe_score_TV.text = "Rating${"\n"}${oneRecipe.spoonacularScore.toString()}"
        one_recipe_ready_time_TV.text = "Cook Time${"\n"}${oneRecipe.readyInMinutes.toString()} minutes"
        if (oneRecipe.imageURL != null) {
            Glide.glideFetch(oneRecipe.imageURL, one_recipe_IV)
        }

        if (viewModel.recipeInSavedList(recipe = oneRecipe)) {
            oneRecipeFavIV.setImageResource(R.drawable.ic_favorite_black_24dp)
        } else {
            oneRecipeFavIV.setImageResource(R.drawable.ic_favorite_border_black_24dp)
        }

        if (viewModel.recipeInShoppingCart(oneRecipe)) {
            oneRecipeCartIV.setImageResource(R.drawable.ic_check_mark)
        } else {
            oneRecipeCartIV.setImageResource(R.drawable.ic_shopping_cart_icon)
        }

        oneRecipeFavIV.setOnClickListener{
            if (viewModel.recipeInSavedList(oneRecipe)) {
                viewModel.removeFavRecipe(oneRecipe)
                oneRecipeFavIV.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                Toast.makeText(this.context, "Recipe unsaved", Toast.LENGTH_SHORT).show()
            }
            else {
                viewModel.addFavRecipe(oneRecipe)
                oneRecipeFavIV.setImageResource(R.drawable.ic_favorite_black_24dp)
                Toast.makeText(this.context, "Recipe saved", Toast.LENGTH_SHORT).show()
            }
        }

        oneRecipeCartIV.setOnClickListener{
            if (viewModel.recipeInShoppingCart(oneRecipe)) {
                viewModel.removeFromShoppingCart(oneRecipe)
                oneRecipeCartIV.setImageResource(R.drawable.ic_shopping_cart_icon)
                Toast.makeText(this.context, "Recipe removed from cart", Toast.LENGTH_SHORT).show()
            }
            else {
                viewModel.addToShoppingCart(oneRecipe)
                oneRecipeCartIV.setImageResource(R.drawable.ic_check_mark)
                Toast.makeText(this.context, "Recipe added to cart", Toast.LENGTH_SHORT).show()
            }
            viewModel.updateShoppingList()
        }

        adapterIngredientsAdapter.submitList(oneRecipe.nutrition!!.ingredients)
        if (oneRecipe.analyzedInstructions != null && oneRecipe.analyzedInstructions.isNotEmpty()) {
            adapterInstructionsAdapter.submitList(oneRecipe.analyzedInstructions?.get(0)?.recipeSteps)
        }
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