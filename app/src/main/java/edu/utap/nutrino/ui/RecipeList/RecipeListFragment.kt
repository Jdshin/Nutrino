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
import android.widget.FrameLayout
import android.widget.LinearLayout
import edu.utap.nutrino.ui.MainViewModel
import kotlinx.android.synthetic.main.single_rv_frag.*

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
            val clearButLayout = view.findViewById<FrameLayout>(R.id.clear_cart_container)
            clearButLayout.visibility = View.GONE
            viewModel.observeRecipes().observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })
        }
        else if (this.tag == MainActivity.savedRecipeFragTag) {
            val clearButLayout = view.findViewById<FrameLayout>(R.id.clear_cart_container)
            clearButLayout.visibility = View.GONE
            viewModel.observeSavedRecipes().observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.single_rv_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter(view)
    }
}