package edu.utap.nutrino.ui

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.nutrino.MainActivity
import edu.utap.nutrino.R
import kotlinx.android.synthetic.main.user_profile_frag.*
import java.util.*


class UserProfileFragment : Fragment() {

    private val defaultDiet = "Omnivore"
    private lateinit var dietTypeAdapter: ArrayAdapter<CharSequence>
    private lateinit var intolerancesAdapter: ArrayAdapter<CharSequence>

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

        dietTypeAdapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.diet_type,
            android.R.layout.simple_spinner_item
        )
        dietTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        diet_type_spinner.adapter = dietTypeAdapter

        help_button.setOnClickListener {
            giveDietInfo()
        }

        intolerancesAdapter = ArrayAdapter.createFromResource(
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
            diet_type_spinner.setSelection(mapDietToIndex(defaultDiet))
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
            // Show the AlertDialog object
            builder.create().show()
        }

        // Initialize profile from preset settings, if available
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
            else -> { // Spinner should be on "Omnivore"
                dietInfoString = R.string.omnivore_info
            }
        }

        builder.setMessage(dietInfoString)
            .setNegativeButton(R.string.diet_info_cancel,
                DialogInterface.OnClickListener { dialog, id ->
                    // User cancelled the dialog
                })
        builder.create().show()
    }

    private fun mapDietToIndex(diet: String): Int {
        var dietList = resources.getStringArray(R.array.diet_type)
        for (i in dietList.indices) {
            if (dietList[i] == diet) {
                return i
            }
        }
        return -1
    }

    // Write to Firestore DB
    private fun updateProfile() {
        val db = FirebaseFirestore.getInstance()

        // Collect the dietInfo to send
        var dietInfo: HashMap<String, Any> = hashMapOf(
            "dietType" to diet_type_spinner.selectedItem.toString()
        )
        addSelectedIntolerances(dietInfo)

        // Update the Firestore DB with sent info
        db.collection("UserData").document(MainActivity.userEmail).collection("UserDiet")
            .document("UserDiet").set(dietInfo)
            .addOnSuccessListener { _ ->
                Log.d(MainActivity.userProfileFragTag, "Success adding diet info.")
            }
            .addOnFailureListener { e ->
                Log.w(MainActivity.userProfileFragTag, "Error adding diet info", e)
            }

        Toast.makeText(context, "Your profile has been saved!", Toast.LENGTH_SHORT).show()
    }

    // Pull from Firestore DB
    private fun presentProfile() {
        val db = FirebaseFirestore.getInstance()

        db.collection("UserData").document(MainActivity.userEmail).collection("UserDiet")
            .document("UserDiet").get()
            .addOnSuccessListener { result ->
                if (!result.exists()) {
                    // Handle situation where user is new and there is no data available
                    return@addOnSuccessListener
                }

                Log.d(MainActivity.userProfileFragTag, "Success retrieving diet info.")

                // Get diet type from Firebase DB, then have it occupy the spinner
                var dbDietType = result.get("dietType").toString()
                diet_type_spinner.setSelection(mapDietToIndex(dbDietType))

                // Check off each intolerance that is found in the Firebase DB
                var intolList = resources.getStringArray(R.array.intolerance_candidates)
                for (i in intolList.indices) {
                    var key = convertIntol(intolList[i])
                    if (result.get(key) as Boolean) {
                        intolerancesGrid.setItemChecked(i, true)
                    }
                    else {
                        intolerancesGrid.setItemChecked(i, false)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w(MainActivity.userProfileFragTag, "Error retrieving diet info", e)
            }
    }

    // Collect intolerances that are checked off on the screen and form a list of them
    private fun addSelectedIntolerances(dietInfo: HashMap<String, Any>) {
        for (i in 0 until intolerancesAdapter.count) {
            var currIntolString = convertIntol(intolerancesGrid.getItemAtPosition(i) as String)
            dietInfo[currIntolString] = intolerancesGrid.isItemChecked(i)
        }
    }

    private fun convertIntol(original: String): String {
        var modified = original.toLowerCase()
        return modified
    }
}