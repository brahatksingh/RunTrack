package com.brahatksingh.runtrack.Services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.brahatksingh.runtrack.R
import com.brahatksingh.runtrack.UI.MainActivity
import com.brahatksingh.runtrack.other.Constants
import com.brahatksingh.runtrack.other.TrackingUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingService : LifecycleService() {

    var isFirstRun = true

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
    }

    private fun postInitValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun addPathPoint(location: Location?) {
        location?.let {
            val position = LatLng(location.latitude,location.longitude)
            pathPoints.value?.apply {
                last().add(position)
                pathPoints.postValue(this)
            }
        }
    }

    private fun pauseService() {
        isTracking.postValue(false)
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking : Boolean) {
        if(isTracking) {
            if(TrackingUtility.hasLocationPermissions(this)) {
                val request = LocationRequest().apply {
                    interval = Constants.LOCATION_UPDATE_INTERVAL
                    fastestInterval = Constants.FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,locationCallback, Looper.getMainLooper()
                )
            }
        }
        else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if(isTracking.value!!) {
                result?.locations?.let {locations ->
                    for(child in locations) {
                        addPathPoint(child)
                        Timber.d("NEW LOCATION : ${child.latitude} + ${child.longitude}")
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                Constants.ACTION_START_OR_RESUME_SERVICE -> {
                    if(isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    }
                    else {
                        Timber.d("Resuming Service")
                        startForegroundService()
                    }
                    Timber.d("Started or Resumed Service")
                }
                Constants.ACTION_PAUSE_SERVICE -> {
                    pauseService()
                    Timber.d("Paused Service")
                }
                Constants.ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped Service")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        addEmptyPolyline()
        isTracking.postValue(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notification is a system service. We get a reference of that notification manager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this,Constants.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            .setContentTitle("Tracking Your Run")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(Constants.NOTIFICATION_ID,notificationBuilder.build())

        // This is also a service .

    }

    override fun onCreate() {
        super.onCreate()
        postInitValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this, Observer {
            updateLocationTracking(it)
        })
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(this,0,Intent(this,MainActivity::class.java).also {
        it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
    },FLAG_UPDATE_CURRENT)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID,Constants.NOTIFICATION_CHANNEL_NAME,NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }
}

// startForegroundService first gets a reference of NotificationManager from AndroidOS.
// Using it we create a Notification channel.
// We construct notification using NotificationCompat.
// To completely make a notification, we needed a PendingIntent. That is done by getMainActivityPendingIntent.
// So if we click the notification it won't automatically navigate to Tracking Fragment. So we added a Action to the Intent in Pending Intent.
// This action is checked in MainActivity onCreate and there can be 2 cases by which it is checked.
// Case 1 : Somehow the MainActivity was destroyed and onCreate is called . onCreate checks for action using an auxiliary function we made.
// Case 2 : The MainActivity is not destroyed and we navigate to it by using notification. Then to check the action we override the onNewIntent and check it for action(about navigating to Tracking Activity).