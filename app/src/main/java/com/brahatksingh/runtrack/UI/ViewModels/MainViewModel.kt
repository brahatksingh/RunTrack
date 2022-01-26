package com.brahatksingh.runtrack.UI.ViewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.brahatksingh.runtrack.Repositories.MainRepository

class MainViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

    init {

    }

}