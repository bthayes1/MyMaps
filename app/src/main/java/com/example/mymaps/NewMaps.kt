package com.example.mymaps

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.mymaps.databinding.ActivityNewMapsBinding
import com.example.mymaps.models.Place
import com.example.mymaps.models.UserMap
import com.google.android.gms.maps.model.Marker
import com.google.android.material.snackbar.Snackbar

private const val TAG = "NewMaps"
private const val DEFAULT_ZOOM = 12f

class NewMaps : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var markers : MutableList<Marker> = mutableListOf()
    private lateinit var binding: ActivityNewMapsBinding
    private lateinit var newTitle : String
    private lateinit var location : Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        newTitle = intent.getStringExtra(NEW_MAP_TITLE).toString()
        location = intent.getParcelableExtra(LOCATION_KEY)!!
        supportActionBar?.title = newTitle

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapFragment.view?.let {
            Snackbar.make(it, "Long Click to add marker", Snackbar.LENGTH_INDEFINITE)
                .setAction("Ok",{})
                .setActionTextColor(Color.WHITE)
                .show()
        }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * To delete a placed marker, tap the infowindow of the marker
     * A long click will add a new marker and prompt the user to populate title and description
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnInfoWindowClickListener { markerToDelete ->
            markerToDelete.remove()
            markers.remove(markerToDelete)
        }
        
        mMap.setOnMapLongClickListener { LatLng ->
            dialogWindow(LatLng)
        }
        // Add a marker in Sydney and move the camera
        val current = LatLng(location.latitude, location.longitude)
        mMap.addMarker(MarkerOptions().position(current).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, DEFAULT_ZOOM))
    }

    private fun dialogWindow(latLng: LatLng) {
        val newLocView = LayoutInflater.from(this).inflate(R.layout.new_marker_view, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Create a Marker")
            .setView(newLocView)
            .setPositiveButton("Ok", null)
            .setNegativeButton("Cancel", null)
            .show()


        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val title = newLocView.findViewById<EditText>(R.id.etTitle).text.toString()
            val description = newLocView.findViewById<EditText>(R.id.etDescription).text.toString()
            if (title.trim().isNotEmpty() && description.trim().isNotEmpty()) {
                val marker = mMap.addMarker(
                    MarkerOptions().position(latLng).title(title).snippet(description))
                if (marker != null) {
                    markers.add(marker)
                    dialog.dismiss()
                }
            }
            else{
                Toast.makeText(this, "Title and Description are Required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**Add the save button to the status bar*/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_map, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**Add the save button to the status bar
     * If a place has been added, the save button can be pressed
     * Once save button is pressed, app will return to MainActivity*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.miSave){
            if (markers.isEmpty()){
                Toast.makeText(this, "No data has been added", Toast.LENGTH_SHORT).show()
                return true
            }
            val places = markers.map{marker -> Place(marker.title.toString(),
                marker.snippet.toString(),
                marker.position.latitude,
                marker.position.longitude)}

            val newMap = UserMap(newTitle, places)
            val intent = Intent()
            intent.putExtra(REQUEST_CODE, newMap)
            // Activity finished ok, return the data
            Log.i(TAG, "New Map saved: ${newMap.title}")
            setResult(RESULT_OK, intent) // set result code and bundle data for response
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}