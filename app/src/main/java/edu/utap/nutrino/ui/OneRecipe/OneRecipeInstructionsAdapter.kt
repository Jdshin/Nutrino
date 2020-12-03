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
import edu.utap.nutrino.api.RecipeInstruction
import edu.utap.nutrino.ui.MainViewModel

class OneRecipeInstructionsAdapter(private val viewModel: MainViewModel)
    : ListAdapter<RecipeInstruction, OneRecipeInstructionsAdapter.ViewHolder>(RecipeInstructionDiff()) {

    inner class ViewHolder (recipeView : View) : RecyclerView.ViewHolder(recipeView) {

        //Implement checkbox functionality
        private val instructionCB = recipeView.findViewById<CheckBox>(R.id.one_recipe_step_checkbox)
        private val instructionTV = recipeView.findViewById<TextView>(R.id.one_recipe_step_TV)

        fun bind (recipeInstructions: RecipeInstruction) {
            var instruction = recipeInstructions.instructionString
            instructionTV.text = instruction
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

    class RecipeInstructionDiff : DiffUtil.ItemCallback<RecipeInstruction>() {
        override fun areItemsTheSame(oldItem: RecipeInstruction, newItem: RecipeInstruction): Boolean {
            return oldItem.stepNumber == newItem.stepNumber
        }

        override fun areContentsTheSame(oldItem: RecipeInstruction, newItem: RecipeInstruction): Boolean {
            return oldItem.instructionString == newItem.instructionString
        }
    }
}

