package com.brahatksingh.runtrack.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.brahatksingh.runtrack.R
import com.brahatksingh.runtrack.UI.MainActivity
import com.brahatksingh.runtrack.other.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext context : Context) = FusedLocationProviderClient(context)


    @Provides
    @ServiceScoped
    fun provideMainActivityPendingIntent(@ApplicationContext app : Context)
    = PendingIntent.getActivity(app,0,
        Intent(app, MainActivity::class.java).also { it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT },PendingIntent.FLAG_UPDATE_CURRENT
    )

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(@ApplicationContext app : Context, intent : PendingIntent) = NotificationCompat.Builder(app,Constants.NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
        .setContentTitle("Tracking Your Run")
        .setContentText("00:00:00")
        .setContentIntent(intent)

}