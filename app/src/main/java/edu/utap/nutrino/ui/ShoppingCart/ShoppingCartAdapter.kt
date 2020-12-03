package edu.utap.nutrino.ui.ShoppingCart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.nutrino.R
import edu.utap.nutrino.api.RecipeIngredient
import edu.utap.nutrino.ui.MainViewModel

class ShoppingCartAdapter(private val viewModel: MainViewModel)
    : RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder>() {

    private var shoppingCartItems = mutableListOf<String>()

    inner class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

        private val ingredient_TV = itemView.findViewById<TextView>(R.id.one_recipe_step_TV)

        fun bind (ingredientName: String) {
            ingredient_TV.text = ingredientName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val recipeView = LayoutInflater.from(parent.context)
            .inflate(R.layout.one_recipe_text_card, parent, false)
        return ViewHolder(recipeView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(shoppingCartItems[holder.adapterPosition])
    }

    override fun getItemCount(): Int {
        return shoppingCartItems.size
    }

    fun addAll(items: List<String>) {
        shoppingCartItems.addAll(items)
    }

    fun removeAll() {
        shoppingCartItems.clear()
    }

    fun removeItemAt(position: Int) {
        shoppingCartItems.removeAt(position)
    }
}

