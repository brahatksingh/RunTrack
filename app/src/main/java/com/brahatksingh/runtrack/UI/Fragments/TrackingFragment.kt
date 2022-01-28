package com.brahatksingh.runtrack.UI.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.brahatksingh.runtrack.R
import com.brahatksingh.runtrack.Services.Polyline
import com.brahatksingh.runtrack.Services.Polylines
import com.brahatksingh.runtrack.Services.TrackingService
import com.brahatksingh.runtrack.UI.ViewModels.MainViewModel
import com.brahatksingh.runtrack.other.Constants
import com.brahatksingh.runtrack.other.Constants.ACTION_PAUSE_SERVICE
import com.brahatksingh.runtrack.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val viewModel : MainViewModel by viewModels()

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    private var map : GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        btnToggleRun.setOnClickListener {
            toggleRun()
        }
        mapView.getMapAsync{
            map = it
            addAllPolyLines()
        }

        subscribeToObservers()


    }

    private fun addAllPolyLines() {
        for(polyLines in pathPoints) {
            val polyLineOptions = PolylineOptions().color(Constants.POLYLINE_COLOR).width(Constants.POLYLINE_WIDTH)
                .addAll(polyLines)
            map?.addPolyline(polyLineOptions)
        }
    }

    private fun moveCameraToUser() {
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(
                pathPoints.last().last(),Constants.MAP_ZOOM
            ))
        }
    }

    private fun updateTracking(isTracking : Boolean) {
        this.isTracking = isTracking
        if(!isTracking) {
            btnToggleRun.text = "Start"
            btnFinishRun.visibility = View.VISIBLE
        }
        else {
            btnToggleRun.text = "STOP"
            btnFinishRun.visibility = View.GONE
        }


    }

    private fun toggleRun() {
        if(isTracking) {
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }
        else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }

    }

    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })
    }

    private fun addLatestPolyline() {
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size-2]
            val lastLatLng = pathPoints.last().last()
            val polyLineOptions = PolylineOptions().color(Constants.POLYLINE_COLOR).width(Constants.POLYLINE_WIDTH)
                .add(preLastLatLng).add(lastLatLng)
            map?.addPolyline(polyLineOptions)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    private fun sendCommandToService(action  :String) {
        Intent(requireContext(),TrackingService::class.java).also{
           it.action = action
            requireContext().startService(it)
        }
    }

}