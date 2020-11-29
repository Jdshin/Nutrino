package edu.utap.nutrino.ui.RecipeList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.nutrino.MainActivity
import edu.utap.nutrino.R
import android.util.Log
import edu.utap.nutrino.ui.MainViewModel
import kotlinx.android.synthetic.main.recipe_search_results_frag.*

class RecipeListFragment : Fragment() {

    private val viewModel : MainViewModel by activityViewModels()
    private lateinit var adapter: RecipeListAdapter

    companion object {
        fun newInstance() : RecipeListFragment {
            return RecipeListFragment()
        }
    }

    private fun initAdapter(view: View) {
        adapter = RecipeListAdapter(viewModel)
        recipe_list_RV.adapter = adapter
        recipe_list_RV.layoutManager = LinearLayoutManager(view.context)
        if (this.tag == MainActivity.recipeResultsFragTag) {
            viewModel.observeRecipes().observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })
        }
        else if (this.tag == MainActivity.savedRecipeFragTag) {
            viewModel.observeSavedRecipes().observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })
        }

        Log.i("TAG USED: ", this.tag.toString())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.recipe_search_results_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter(view)
    }
}