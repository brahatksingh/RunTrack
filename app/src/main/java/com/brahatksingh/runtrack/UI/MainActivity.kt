package com.brahatksingh.runtrack.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.brahatksingh.runtrack.R
import com.brahatksingh.runtrack.db.RunDAO
import com.brahatksingh.runtrack.other.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigateToTrackingFragmentIfNeeded(intent)

        setSupportActionBar(toolbar)
        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
        bottomNavigationView.setOnNavigationItemReselectedListener {
            // NO ACTION
        }
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent  : Intent?) {
        if(intent?.action == Constants.ACTION_SHOW_TRACKING_FRAGMENT) {
            navHostFragment.findNavController().navigate(R.id.action_global_trackFragment)
        }
    }
}
