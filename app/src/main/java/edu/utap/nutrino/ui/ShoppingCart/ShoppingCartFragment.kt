package edu.utap.nutrino.ui.ShoppingCart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
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
        viewModel.updateShoppingList()
        viewModel.observeShoppingCart().observe(viewLifecycleOwner, Observer {
            adapter.removeAll()
            adapter.addAll(it)
            adapter.notifyDataSetChanged()
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.single_rv_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter(view)
        val clearButLayout = view.findViewById<FrameLayout>(R.id.clear_cart_container)
        clearButLayout.visibility = View.VISIBLE
        val clearBut = view.findViewById<Button>(R.id.clear_cart_button)
        clearBut.setOnClickListener{
            viewModel.clearShoppingCart()
            Toast.makeText(this.context, "Cleared shopping cart", Toast.LENGTH_SHORT).show()
        }
    }
}