package com.brahatksingh.runtrack.UI.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.brahatksingh.runtrack.R
import com.brahatksingh.runtrack.other.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {

    @Inject
    lateinit var sharedPrefs : SharedPreferences

    @set:Inject
    var firstTime = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!firstTime) {
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.setupFragment,true)
                .build()
            findNavController().navigate(R.id.action_setupFragment_to_runFragment,savedInstanceState,navOptions)
        }

        tvContinue.setOnClickListener {
            val success = writePersonalData()
            if(success) {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            }
            else {
                Toast.makeText(requireContext(),"Please enter fields properly",Toast.LENGTH_SHORT).show()
            }
            findNavController().navigate(R.id.runFragment)

        }
    }

    private fun writePersonalData() : Boolean {
        val name = etName.text.toString()
        val weight = etWeight.text.toString()
        if(name.isEmpty() || weight.isEmpty()) {
            return false
        }

        sharedPrefs.edit().putString(Constants.KEY_NAME,name).putFloat(Constants.KEY_WEIGHT,weight.toFloat())
            .putBoolean(Constants.KEY_FIRST_TIME_TOGGLE,false).apply()

        val toolbarText = "Let's Go, $name!"

        requireActivity().tvToolbarTitle.text = toolbarText

        return true
    }
}