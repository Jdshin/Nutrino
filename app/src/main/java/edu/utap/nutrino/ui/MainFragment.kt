package edu.utap.nutrino.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import edu.utap.nutrino.R

class MainFragment : Fragment() {

    private val viewModel : MainViewModel by activityViewModels()

    companion object{
        fun newInstance() : MainFragment {
            return MainFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment,container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.netRecipes(getString(R.string.Spoonacular_API_KEY))
    }
}