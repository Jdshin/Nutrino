package edu.utap.nutrino.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.color.MaterialColors.getColor
import com.google.firebase.auth.FirebaseAuth
import edu.utap.nutrino.R
import kotlinx.android.synthetic.main.user_profile_frag.*

class UserProfileFragment : Fragment() {

    private val dietTypes: Array<String> by lazy {
        resources.getStringArray(R.array.diet_type)
    }

    companion object {
        fun newInstance() : UserProfileFragment {
            return UserProfileFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_profile_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var displayName = FirebaseAuth.getInstance().currentUser!!.displayName
        if (displayName.isNullOrBlank()) {
            userProfileTV.text = "User Profile"
        }
        else {
            userProfileTV.text = displayName + "'s " + "Profile"
        }

        val dietTypeAdapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.diet_type,
            android.R.layout.simple_spinner_item
        )
        dietTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        diet_type_spinner.adapter = dietTypeAdapter

        help_button.setOnClickListener {
            giveDietInfo()
        }

        val intolerancesAdapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.intolerance_candidates,
            android.R.layout.simple_list_item_multiple_choice
        )
        intolerancesGrid.adapter = intolerancesAdapter
        intolerancesGrid.choiceMode = GridView.CHOICE_MODE_MULTIPLE

        back_button.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        clear_button.setOnClickListener {
            // Uncheck all the checkboxes
            for (i in 0 until intolerancesAdapter.count) {
                intolerancesGrid.setItemChecked(i, false)
            }
            // Set diet type back to default
            diet_type_spinner.setSelection(0)
        }

        save_button.setOnClickListener {
            val builder = AlertDialog.Builder(context!!)
            builder.setMessage("Please confirm that you want to save your profile as it currently is.")
                .setNegativeButton(R.string.diet_update_cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
                .setPositiveButton(R.string.diet_update_confirm,
                    DialogInterface.OnClickListener { dialog, id ->
                        updateProfile()
                    })
            // Create the AlertDialog object and return it
            builder.create().show()
        }

        presentProfile()
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

        builder.setMessage(dietInfoString)
            .setNegativeButton(R.string.diet_info_cancel,
                DialogInterface.OnClickListener { dialog, id ->
                    // User cancelled the dialog
                })
        // Create the AlertDialog object and return it
        builder.create().show()
    }

    private fun updateProfile() {
        Toast.makeText(context, "Your profile has been updated!", Toast.LENGTH_SHORT).show()
    }

    private fun presentProfile() {
    }
}