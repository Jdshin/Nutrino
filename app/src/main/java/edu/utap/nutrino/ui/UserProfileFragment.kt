package edu.utap.nutrino.ui

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.nutrino.BuildConfig
import edu.utap.nutrino.MainActivity
import edu.utap.nutrino.R
import kotlinx.android.synthetic.main.fragment_rv.*
import kotlinx.android.synthetic.main.user_profile_frag.*
import java.util.*

class UserProfileFragment : Fragment() {

    private val restarantTypes: Array<String> by lazy {
        resources.getStringArray(R.array.diet_type)
    }
    private val order: Array<String> by lazy {
        resources.getStringArray(R.array.order)
    }
    private val limit: Array<String> by lazy {
        resources.getStringArray(R.array.limit)
    }

    companion object {
        fun newInstance() : UserProfileFragment {
            return UserProfileFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.user_profile_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dietTypeAdapter = ArrayAdapter.createFromResource(context!!,
                R.array.diet_type,
                android.R.layout.simple_spinner_item)
        dietTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        diet_type_spinner.adapter = dietTypeAdapter

        val limitAdapter = ArrayAdapter.createFromResource(context!!,
                R.array.limit,
                android.R.layout.simple_spinner_item)
        limitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        limit_spinner.adapter = limitAdapter

        val orderAdapter = ArrayAdapter.createFromResource(context!!,
                R.array.order,
                android.R.layout.simple_spinner_item)
        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        choose_order_spinner.adapter = orderAdapter

        help_button.setOnClickListener {
            giveDietInfo()
        }
    }

    private fun giveDietInfo() {
        // Use the Builder class for convenient dialog construction
        val builder = AlertDialog.Builder(context!!)

        // Choose the correct message based on diet
        var dietSpinnerSelection = diet_type_spinner.selectedItem.toString()
        var dietInfoString = R.string.omnivore_info
        when (dietSpinnerSelection) {
            "Gluten Free" -> dietInfoString = R.string.gluten_free_info
            "Ketogenic" -> dietInfoString = R.string.ketogenic_info
            "Vegetarian" -> dietInfoString = R.string.vegetarian_info
            "Lacto-Vegetarian" -> dietInfoString = R.string.lacto_vegetarian_info
            "Ovo-Vegetarian" -> dietInfoString = R.string.ovo_vegetarian_info
            "Vegan" -> dietInfoString = R.string.vegan_info
            "Pescetarian" -> dietInfoString = R.string.pescetarian_info
            "Paleo" -> dietInfoString = R.string.paleo_info
            "Primal" -> dietInfoString = R.string.primal_info
            "Whole30" -> dietInfoString = R.string.whole30_info
            else -> { // Spinner should be on "Diet Type?"
                dietInfoString = R.string.omnivore_info
            }
        }

        //Toast.makeText(context, dietInfoString, Toast.LENGTH_LONG).show()
        builder.setMessage(dietInfoString)
            .setNegativeButton(R.string.diet_info_cancel,
                DialogInterface.OnClickListener { dialog, id ->
                    // User cancelled the dialog
                })
        // Create the AlertDialog object and return it
        builder.create().show()

        /*

        builder.setMessage(dietInfoString)

        // Create the AlertDialog object and return it
        builder.create()

         */
    }
}