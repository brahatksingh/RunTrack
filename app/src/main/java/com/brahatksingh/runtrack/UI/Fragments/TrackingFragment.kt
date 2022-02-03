package com.brahatksingh.runtrack.UI.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.brahatksingh.runtrack.R
import com.brahatksingh.runtrack.Services.Polyline
import com.brahatksingh.runtrack.Services.Polylines
import com.brahatksingh.runtrack.Services.TrackingService
import com.brahatksingh.runtrack.UI.ViewModels.MainViewModel
import com.brahatksingh.runtrack.db.Run
import com.brahatksingh.runtrack.other.Constants
import com.brahatksingh.runtrack.other.Constants.ACTION_PAUSE_SERVICE
import com.brahatksingh.runtrack.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.brahatksingh.runtrack.other.TrackingUtility
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val viewModel : MainViewModel by viewModels()

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    private var map : GoogleMap? = null

    private var currentTimeMillis = 0L

    private var menu  : Menu? = null

    @set:Inject
            var weight = 80f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        btnToggleRun.setOnClickListener {
            toggleRun()
        }
        btnFinishRun.setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSaveToDB()
        }
        mapView.getMapAsync{
            map = it
            addAllPolyLines()
        }

        subscribeToObservers()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu,menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(currentTimeMillis > 0L) {
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    private fun showCancelTrackingDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext(),R.style.AlertDialogTheme)
            .setTitle("Cancel Run")
            .setMessage("Are you sure?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes") { p1,p2->
                stopRun()
            }
            .setNegativeButton("No") { dialogInterface,p1 ->
                dialogInterface.cancel()
            }
            .create()
        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.miCancelTracking -> {
                showCancelTrackingDialog()
            }
            else -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun stopRun() {
        tvTimer.text= "00:00:00:00"
        sendCommandToService(Constants.ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)

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
        if(!isTracking && currentTimeMillis > 1L) {
            btnToggleRun.text = "START"
            btnFinishRun.visibility = View.VISIBLE
        }
        else if(isTracking){
            menu?.getItem(0)?.isVisible = true
            btnToggleRun.text = "STOP"
            btnFinishRun.visibility = View.GONE
        }
    }

    private fun toggleRun() {
        if(isTracking) {
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(Constants.ACTION_PAUSE_SERVICE)
        }
        else {
            sendCommandToService(Constants.ACTION_START_OR_RESUME_SERVICE)
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

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner,Observer{
            currentTimeMillis = it
            val formattedTime = TrackingUtility.getFormatted(currentTimeMillis,true)
            tvTimer.text = formattedTime
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

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.builder()
        for(polyline in pathPoints) {
            for(pos in polyline) {
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()
            )
        )

    }

    private fun endRunAndSaveToDB() {
        map?.snapshot {bmp ->
            var distanceinMeter = 0
            for(polyLine in pathPoints) {
                distanceinMeter += TrackingUtility.calculatePolyLineLength(polyLine).toInt()
            }
            val avgSpeed = round((distanceinMeter/1000f) / (currentTimeMillis/1000f/60/60)*10) / 10f
            val timeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceinMeter/1000f)*weight).toInt()
            val run = Run(bmp,timeStamp,avgSpeed,distanceinMeter,currentTimeMillis,caloriesBurned)
            viewModel.insertRun(run)
            Snackbar.make(requireActivity().findViewById(R.id.rootView),"Run Saved",Snackbar.LENGTH_LONG).show()
            stopRun()
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

    override fun onSaveInstanceState(outState : Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    private fun sendCommandToService(action : String) {
        Intent(requireContext(),TrackingService::class.java).also{
            it.action = action
            requireContext().startService(it)
        }
    }

}