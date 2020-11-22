package edu.utap.nutrino.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.nutrino.R
import edu.utap.nutrino.api.Recipe
import edu.utap.nutrino.glide.AppGlideModule
import edu.utap.nutrino.glide.Glide


class RecipeListAdapter(private val viewModel: MainViewModel)
    : ListAdapter<Recipe, RecipeListAdapter.ViewHolder>(RecipeDiff()) {

    inner class ViewHolder (recipeView : View) : RecyclerView.ViewHolder(recipeView) {
        private var recipeTitleTV = recipeView.findViewById<TextView>(R.id.recipe_title_TV)
        private var recipeIV = recipeView.findViewById<ImageView>(R.id.recipe_pic_IV)

        fun bind (recipe : Recipe) {
            recipeTitleTV.text = recipe.title
            if (recipe.imageURL != null) {
                Glide.glideFetch(recipe.imageURL, recipeIV)
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

