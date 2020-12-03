package edu.utap.nutrino.ui.ShoppingCart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.nutrino.R
import edu.utap.nutrino.ui.MainViewModel
import kotlinx.android.synthetic.main.single_rv_frag.*

class ShoppingCartFragment : Fragment() {

    private val viewModel : MainViewModel by activityViewModels()
    private lateinit var adapter : ShoppingCartAdapter

    companion object {
        fun newInstance() : ShoppingCartFragment {
            return ShoppingCartFragment()
        }
    }

    private fun initAdapter(view: View) {
        adapter = ShoppingCartAdapter(viewModel)
        recipe_list_RV.adapter = adapter
        recipe_list_RV.layoutManager = LinearLayoutManager(view.context)
        viewModel.observeShoppingCart().observe(viewLifecycleOwner, Observer {
            adapter.addAll(it)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.single_rv_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter(view)
    }
}