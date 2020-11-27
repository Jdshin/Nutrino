package edu.utap.nutrino.ui.OneRecipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.nutrino.R
import edu.utap.nutrino.api.RecipeIngredients
import edu.utap.nutrino.ui.MainViewModel
import org.w3c.dom.Text

class OneRecipeIngredientsListAdapter(private val viewModel: MainViewModel)
    : ListAdapter<RecipeIngredients, OneRecipeIngredientsListAdapter.ViewHolder>(RecipeIngredientsDiff()) {

    inner class ViewHolder (recipeView : View) : RecyclerView.ViewHolder(recipeView) {

        //Implement checkbox functionality
        private val ingredientCB = recipeView.findViewById<CheckBox>(R.id.one_recipe_step_checkbox)
        private val ingredientTV = recipeView.findViewById<TextView>(R.id.one_recipe_step_TV)

        fun bind (recipeIngredients: RecipeIngredients) {
            var ingredientName = recipeIngredients.name.substring(0,1).toUpperCase() + recipeIngredients.name.substring(1)
            ingredientTV.text = ingredientName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val recipeView = LayoutInflater.from(parent.context)
                .inflate(R.layout.one_recipe_text_card, parent, false)
        return ViewHolder(recipeView)
    }

    //https://www.youtube.com/watch?v=NcyJvweG2Ig - How to open fragment from recyclerview onclick

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RecipeIngredientsDiff : DiffUtil.ItemCallback<RecipeIngredients>() {
        override fun areItemsTheSame(oldItem: RecipeIngredients, newItem: RecipeIngredients): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: RecipeIngredients, newItem: RecipeIngredients): Boolean {
            return oldItem.amount == newItem.amount
                    && oldItem.unit == newItem.unit
        }
    }
}

