package com.brahatksingh.runtrack.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.brahatksingh.runtrack.R
import com.brahatksingh.runtrack.db.RunDAO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())

        navHostFragment.findNavController().addOnDestinationChangedListener{ p1,p2,p3 ->
            when(p2.id) {
                R.id.settingsFragment,R.id.runFragment,R.id.statisticsFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                }
                else -> {
                     bottomNavigationView.visibility = View.GONE
                }
            }
            // p2 is the destination
        }
    }
}
