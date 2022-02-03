package com.brahatksingh.runtrack.UI.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brahatksingh.runtrack.R
import com.brahatksingh.runtrack.other.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    @Inject
    lateinit var sharedPrefs : SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFieldsFromSharedPreds()
        btnApplyChanges.setOnClickListener {
            val success = applyChangesToSharePrefs()
            if(success) {
                Toast.makeText(requireContext(),"Saved Changes", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(requireContext(),"Please enter fields properly", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadFieldsFromSharedPreds() {
        val name = sharedPrefs.getString(Constants.KEY_NAME,"")
        val weight = sharedPrefs.getFloat(Constants.KEY_WEIGHT,80f)
        etName.setText(name)
        etWeight.setText(weight.toString())
    }

    private fun applyChangesToSharePrefs() : Boolean {
        val name = etName.text.toString()
        val weight = etWeight.text.toString()
        if(name.isEmpty() || weight.isEmpty()) {
            return false
        }
        sharedPrefs.edit().putString(Constants.KEY_NAME,name).putFloat(Constants.KEY_WEIGHT,weight.toFloat()).apply()
        val toolbartext = "Let's go $name"
        requireActivity().tvToolbarTitle.text = toolbartext
        return true

    }
}

