package edu.utap.nutrino.ui

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.nutrino.MainActivity
import edu.utap.nutrino.R
import edu.utap.nutrino.api.Recipe
import edu.utap.nutrino.glide.Glide


class RecipeListAdapter(private val viewModel: MainViewModel)
    : ListAdapter<Recipe, RecipeListAdapter.ViewHolder>(RecipeDiff()) {

    companion object IntentStrings{
        //Intent Strings
        val recipeTitleKey = "title"
        val recipeScoreKey = "score"
        val recipeReadyTimeKey = "readyTime"
        val recipeSummaryKey = "summary"
        val recipeImageUrlKey = "imageUrl"
    }

    inner class ViewHolder (recipeView : View) : RecyclerView.ViewHolder(recipeView) {
        private var recipeTitleTV = recipeView.findViewById<TextView>(R.id.recipe_title_TV)
        private var recipeIV = recipeView.findViewById<ImageView>(R.id.recipe_pic_IV)
        private var recipeFavIV = recipeView.findViewById<ImageView>(R.id.recipe_fav_but)

        fun bind (recipe : Recipe) {
            recipeTitleTV.text = recipe.title
            if (recipe.imageURL != null) {
                Glide.glideFetch(recipe.imageURL, recipeIV)
            }
            recipeFavIV.setOnClickListener{
                viewModel.addFavRecipe(recipe)
                recipeFavIV.setImageResource(R.drawable.ic_favorite_black_24dp)
            }
            recipeIV.setOnClickListener{
                viewModel.doOneRecipe(it.context, recipe)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val recipeView = LayoutInflater.from(parent.context)
                .inflate(R.layout.recipe_card, parent, false)
        return ViewHolder(recipeView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
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

