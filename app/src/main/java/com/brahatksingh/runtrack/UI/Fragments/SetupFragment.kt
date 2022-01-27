package com.brahatksingh.runtrack.UI.Fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brahatksingh.runtrack.R
import kotlinx.android.synthetic.main.fragment_setup.*

class SetupFragment : Fragment(R.layout.fragment_setup) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvContinue.setOnClickListener {
            val action = SetupFragmentDirections.actionSetupFragmentToRunFragment()
            findNavController().navigate(action)

        }
    }
}