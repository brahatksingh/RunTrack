package com.brahatksingh.runtrack.UI.Fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.brahatksingh.runtrack.R
import com.brahatksingh.runtrack.UI.ViewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val viewModel : MainViewModel by viewModels()
}