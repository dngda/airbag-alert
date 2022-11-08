package com.airmineral.airbagalert.ui.auth

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.view.setPadding
import com.airmineral.airbagalert.R
import com.airmineral.airbagalert.base.BaseActivity
import com.airmineral.airbagalert.data.PreferenceProvider
import com.airmineral.airbagalert.data.RetrofitInstance
import com.airmineral.airbagalert.data.model.Incident
import com.airmineral.airbagalert.data.model.Item
import com.airmineral.airbagalert.databinding.ActivityMapsBinding
import com.airmineral.airbagalert.utils.toastLong
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MapsActivity : BaseActivity<ActivityMapsBinding>(), OnMapReadyCallback, LocationListener {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private val preferenceProvider: PreferenceProvider by inject()

    private var desiredLatLng = LatLng(0.0, 0.0)

    override fun getLayoutBinding(): (LayoutInflater) -> ActivityMapsBinding {
        return ActivityMapsBinding::inflate
    }

    override fun setupViews(savedInstanceState: Bundle?) {
        title = "Select Location"

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bs.bottomSheet)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_view) as SupportMapFragment

        val lat = Incident.DUMMY.latitude.toDouble()
        val long = Incident.DUMMY.longitude.toDouble()

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }

        mapFragment.getMapAsync { map ->
            mMap = map
            mMap.uiSettings.isZoomControlsEnabled = true
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            getLocationPermission()

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, long), 17f))

            val oldPos = map.cameraPosition.target
            map.setOnCameraMoveStartedListener {
                if (animateMarker) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                    binding.iconMarker.animate().translationY(-50f).start()
                    binding.iconMarkerShadow.animate().withStartAction {
                        binding.iconMarkerShadow.setPadding(10)
                    }.start()
                }
                hasFetch = false
            }

            map.setOnCameraIdleListener {
                val newPos = map.cameraPosition.target
                if (oldPos != newPos) {

                    binding.iconMarker.animate().translationY(0f).start()
                    binding.iconMarkerShadow.animate().withStartAction {
                        binding.iconMarkerShadow.setPadding(0)
                    }.start()

                    loadLocationData(map, newPos)
                }
                desiredLatLng = newPos
            }
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ -> startActivity(android.content.Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    private fun loadLocationData(map: GoogleMap, newPos: LatLng) {
        getLocation(newPos) { item ->
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            val position = item.position
            val findLocation = LatLng(position.lat, position.lng)

            map.animateCamera(CameraUpdateFactory.newLatLng(findLocation), 200,
                object : GoogleMap.CancelableCallback {
                    override fun onFinish() {
                        hasFetch = true
                        animateMarker = true
                    }

                    override fun onCancel() {
                        animateMarker = true
                    }
                })

            val titlePlace = item.title
            val address = item.address.label

            binding.bs.textTitle.text = titlePlace
            binding.bs.textAddress.text = address
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private var hasFetch = false
    private var animateMarker = true
    private val retrofitInstance = RetrofitInstance.create()
    private var isLocationGranted = false

    private fun getLocation(latLng: LatLng, done: (Item) -> Unit) {
        val at = "${latLng.latitude},${latLng.longitude}"
        if (!hasFetch) {
            animateMarker = false
            binding.progressCircular.visibility = View.VISIBLE

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val places = retrofitInstance.getLocation(at).items
                    runOnUiThread {
                        if (places.isNotEmpty()) {
                            binding.progressCircular.visibility = View.GONE
                            done.invoke(places.first())
                        }
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getLocationPermission() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isLocationGranted = true
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isLocationGranted = true
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (isLocationGranted) {
//            if (!animateMarker) hasFetch = false

            if (!hasFetch) {
                animateMarker = false
                binding.progressCircular.visibility = View.VISIBLE
            }
            toastLong("Memuat lokasi terkini.\nPastikan GPS aktif!")

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                10f,
                this
            )

        } else {
            getLocationPermission()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.maps_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_get_location -> {
                Log.d("MapsActivity", "action_get_location clicked")
                getCurrentLocation()
            }
            R.id.action_save -> {
                preferenceProvider.saveUserLocation(desiredLatLng)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onLocationChanged(location: Location) {
        val lat = location.latitude
        val long = location.longitude
        val latLng = LatLng(lat, long)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        animateMarker = false
    }

    override fun onProviderDisabled(provider: String) {
        buildAlertMessageNoGps()
    }
}