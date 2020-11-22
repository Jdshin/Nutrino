package edu.utap.nutrino.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.nutrino.R
import kotlinx.android.synthetic.main.fragment_rv.*

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
        viewModel.observeRecipes().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_rv, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter(view)
    }
}