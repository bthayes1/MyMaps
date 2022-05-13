package com.example.mymaps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.mymaps.databinding.ActivityMapScreenBinding
import com.example.mymaps.models.UserMap
import com.google.android.gms.maps.model.LatLngBounds

private const val TAG = "MapScreen"

class MapScreen : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var myMap : UserMap
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myMap = intent.getSerializableExtra(MAP_LOCATION) as UserMap
        supportActionBar?.title = myMap.title

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * The camera will zoom in to maximum zoom to include all place markers
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val builder = LatLngBounds.Builder()
        for (place in myMap.places){
            Log.i(TAG, "The latitude is ${place.latitude}, longitude is: ${place.longitude}")
            val latLng = LatLng(place.latitude, place.longitude)
            builder.include(latLng)
            mMap.addMarker(MarkerOptions().position(latLng).title(place.name).snippet(place.description))
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 1000, 1000, 75))
    }
}