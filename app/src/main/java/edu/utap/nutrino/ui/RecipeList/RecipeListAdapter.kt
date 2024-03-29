package edu.utap.nutrino.ui.RecipeList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.nutrino.MainActivity
import edu.utap.nutrino.R
import edu.utap.nutrino.api.Recipe
import edu.utap.nutrino.glide.Glide
import edu.utap.nutrino.ui.MainViewModel
import edu.utap.nutrino.ui.OneRecipe.OneRecipeFragment
import kotlinx.coroutines.withContext


class RecipeListAdapter(private val viewModel: MainViewModel)
    : ListAdapter<Recipe, RecipeListAdapter.ViewHolder>(RecipeDiff()) {

    inner class ViewHolder (recipeView : View) : RecyclerView.ViewHolder(recipeView) {
        private var recipeTitleTV = recipeView.findViewById<TextView>(R.id.recipe_title_TV)
        private var recipeIV = recipeView.findViewById<ImageView>(R.id.recipe_pic_IV)
        private var recipeFavIV = recipeView.findViewById<ImageView>(R.id.recipe_fav_but)
        private var recipeCartIV = recipeView.findViewById<ImageView>(R.id.add_to_cart_but)

        fun bind (recipe : Recipe) {

            recipeTitleTV.text = recipe.title

            if (viewModel.recipeInSavedList(recipe)) {
                recipeFavIV.setImageResource(R.drawable.ic_favorite_black_24dp)
            } else {
                recipeFavIV.setImageResource(R.drawable.ic_favorite_border_black_24dp)
            }

            if (viewModel.recipeInShoppingCart(recipe)) {
                recipeCartIV.setImageResource(R.drawable.ic_check_mark)
            } else {
                recipeCartIV.setImageResource(R.drawable.ic_shopping_cart_icon)
            }

            if (recipe.imageURL != null) {
                Glide.glideFetch(recipe.imageURL, recipeIV)
            }

            recipeFavIV.setOnClickListener{
                if (viewModel.recipeInSavedList(recipe)) {
                    viewModel.removeFavRecipe(recipe)
                    recipeFavIV.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                    Toast.makeText(recipeFavIV.context, "Recipe removed", Toast.LENGTH_SHORT).show()
                }
                else {
                    viewModel.addFavRecipe(recipe)
                    recipeFavIV.setImageResource(R.drawable.ic_favorite_black_24dp)
                    Toast.makeText(recipeFavIV.context, "Recipe saved", Toast.LENGTH_SHORT).show()
                }
            }

            recipeCartIV.setOnClickListener{
                if (viewModel.recipeInShoppingCart(recipe)) {
                    viewModel.removeFromShoppingCart(recipe)
                    recipeCartIV.setImageResource(R.drawable.ic_shopping_cart_icon)
                    Toast.makeText(recipeCartIV.context, "Recipe removed from cart", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.addToShoppingCart(recipe)
                    recipeCartIV.setImageResource(R.drawable.ic_check_mark)
                    Toast.makeText(recipeCartIV.context, "Recipe added to cart", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val recipeView = LayoutInflater.from(parent.context)
                .inflate(R.layout.recipe_card, parent, false)
        return ViewHolder(recipeView)
    }

    //https://www.youtube.com/watch?v=NcyJvweG2Ig - How to open fragment from recyclerview onclick

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener { p0 ->
            val activity = p0!!.context as AppCompatActivity
            viewModel.setOneRecipe(getItem(position))
            activity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_container, OneRecipeFragment.newInstance(), getItem(position).key.toString())
                    .addToBackStack(null)
                    .commit()
        }
    }

    class RecipeDiff : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.imageURL == newItem.imageURL
                    && oldItem.title == newItem.title
        }


    }

}

