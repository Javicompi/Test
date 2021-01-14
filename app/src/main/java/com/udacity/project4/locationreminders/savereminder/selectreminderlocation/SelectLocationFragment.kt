package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    private val TAG = SelectLocationFragment::class.java.simpleName

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()

    private lateinit var binding: FragmentSelectLocationBinding

    private lateinit var map: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var marker: Marker
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val REQUEST_LOCATION_PERMISSION = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this
        binding.saveLocationButton.setOnClickListener {

        }

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

//        TODO: add the map setup implementation
//        TODO: zoom to the user location after taking his permission
//        TODO: add style to the map
//        TODO: put a marker to location that the user selected


//        TODO: call this function after the user confirms on the selected location
        binding.saveLocationButton.setOnClickListener {
            onLocationSelected()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun onLocationSelected() {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
        Log.d(TAG, "Marker: ${marker.title}, ${marker.position.latitude}, ${marker.position.longitude}")
        val latitude = marker.position.latitude.toFloat()
        val longitude = marker.position.longitude.toFloat()
        val location = marker.title
        val action = SelectLocationFragmentDirections
            .actionSelectLocationFragmentToSaveReminderFragment(location, latitude, longitude)
        findNavController().navigate(action)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_style))
        setOnMapClick(map)
        enableLocation()
    }

    private fun setOnMapClick(googleMap: GoogleMap) {
        googleMap.setOnMapClickListener { latLong ->
            val snippet = String.format(
                Locale.getDefault(),
                getString(R.string.lat_long_snippet),
                latLong.latitude,
                latLong.longitude
            )
            googleMap.clear()
            marker = googleMap.addMarker(
                MarkerOptions()
                    .position(latLong)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
            )
            marker.showInfoWindow()
            enableSaveButton()
        }
        googleMap.setOnPoiClickListener { poi ->
            googleMap.clear()
            marker = googleMap.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            marker.showInfoWindow()
            enableSaveButton()
        }
    }

    private fun enableLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Location permission not granted, requesting permissions")
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            Log.d(TAG, "Location permission granted, requesting location")
            map.isMyLocationEnabled = true
            getLocation(map)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "On request permission Result")
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d(TAG, "Permission granted, enable location")
                enableLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(googleMap: GoogleMap) {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            var lastKnownLocation: Location
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.result!!
                    Log.d(TAG, "Last location found: ${lastKnownLocation.latitude}, ${lastKnownLocation.longitude}")
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(lastKnownLocation.latitude,
                            lastKnownLocation.longitude), 15.0f))
                } else {
                    Log.d(TAG, "Current location is null. Using defaults.")
                    Log.e(TAG, "Exception: %s", task.exception)
                    val defaultLocation = LatLng(37.4030185, -122.3212949)
                    googleMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(defaultLocation, 15.0f))
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun enableSaveButton() {
        binding.saveLocationButton.apply {
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            isEnabled = true
        }
    }
}
