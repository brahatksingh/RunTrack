package com.brahatksingh.runtrack.UI.Fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.brahatksingh.runtrack.R
import com.brahatksingh.runtrack.UI.ViewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run) {

    private val viewModel : MainViewModel by viewModels()


}