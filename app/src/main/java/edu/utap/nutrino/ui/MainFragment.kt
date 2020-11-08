package edu.utap.nutrino.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

        var getRecipeBut = view.findViewById<Button>(R.id.getRecipeBut)
        var mealPlanBut = view.findViewById<Button>(R.id.mealPlanBut)
        var profileBut = view.findViewById<Button>(R.id.profileBut)
        var settingsBut = view.findViewById<Button>(R.id.settingsBut)
        initClickListeners(getRecipeBut, mealPlanBut, profileBut, settingsBut)

    }

    private fun initClickListeners(getRecipeBut : Button, mealPlanBut : Button, profileBut : Button, settingsBut : Button) {
        getRecipeBut.setOnClickListener{
            viewModel.netRecipes(getString(R.string.Spoonacular_API_KEY))
        }

        mealPlanBut.setOnClickListener{

        }

        profileBut.setOnClickListener{

        }

        settingsBut.setOnClickListener{

        }
    }
}