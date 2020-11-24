package edu.utap.nutrino.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import edu.utap.nutrino.R
import edu.utap.nutrino.glide.Glide
import org.w3c.dom.Text

class OneRecipeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.one_recipe)
        val activityThatCalled = intent
        val callingBundle = activityThatCalled.extras

        val oneRecipeIV = findViewById<ImageView>(R.id.one_recipe_IV)
        val oneRecipeTV = findViewById<TextView>(R.id.one_recipe_summary_TV)

        val recipeImageUrl = callingBundle?.getString(RecipeListAdapter.recipeImageUrlKey)
        if (recipeImageUrl != null) {
            Glide.glideFetch(recipeImageUrl, oneRecipeIV)
        }
        if (callingBundle == null) {
            Log.i("calling bundle", " is null")
        }
        if (callingBundle != null) {
            oneRecipeTV.text = callingBundle.getString("summary") ?: "No summary available"
            Log.i("Recipe summary: ", callingBundle.getString("summary")!!)
        }


    }

}