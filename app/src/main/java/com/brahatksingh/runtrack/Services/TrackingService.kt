package com.brahatksingh.runtrack.Services

import android.content.Intent
import android.provider.SyncStateContract
import androidx.lifecycle.LifecycleService
import com.brahatksingh.runtrack.other.Constants
import timber.log.Timber

class TrackingService : LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                Constants.ACTION_START_OR_RESUME_SERVICE -> {
                    Timber.d("Started or Resumed Service")
                }
                Constants.ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused Service")
                }
                Constants.ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped Service")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}