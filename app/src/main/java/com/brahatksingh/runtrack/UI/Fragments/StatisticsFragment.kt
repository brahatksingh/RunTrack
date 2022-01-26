package com.brahatksingh.runtrack.UI.Fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.brahatksingh.runtrack.R
import com.brahatksingh.runtrack.UI.ViewModels.MainViewModel
import com.brahatksingh.runtrack.UI.ViewModels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private val viewModel : StatisticsViewModel by viewModels()
}