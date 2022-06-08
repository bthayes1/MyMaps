package com.example.mymaps

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.models.UserMap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.io.*

// Key values for data being passed from parent to child
const val MAP_LOCATION = "map location"
const val NEW_MAP_TITLE = "new title"
const val REQUEST_CODE = "new map"

const val LOCATION_KEY = "location" // Key for extras passed into MapActivity.kt

// Default location if location permission is denied.
private const val DEFAULT_LOCATION_PROVIDER = "default"
private const val DEFAULT_LATITUDE = 45.0
private const val DEFAULT_LONGITUDE = 45.0

private const val TAG = "MainActivity"

//File where UserMaps are stored
private const val FILENAME = "usermaps.data"

class MainActivity : AppCompatActivity() {

    private lateinit var editActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapTitle : String
    private lateinit var data : MutableList<UserMap>
    private lateinit var adapter: MapsAdapter
    private lateinit var rvMaps: RecyclerView
    private lateinit var fabNewMap: FloatingActionButton
    private lateinit var constraintLayout : ConstraintLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Upon startup, maps from data file are read
        data = deSerializeUserMaps(this).toMutableList()

        initViews()

        rvMaps.layoutManager = LinearLayoutManager(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupAdapter()
        fabNewMap.setOnClickListener { dialogWindow(false, 0) }
        newMapsLauncher()
    }

    private fun initViews() {
        fabNewMap = findViewById(R.id.fabCreateMap)
        rvMaps = findViewById(R.id.rvLocations)
        constraintLayout = findViewById(R.id.constraintLayout)
    }

    private fun newMapsLauncher() {
        editActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // If the user comes back to this activity from EditActivity
                // with no error or cancellation
                val results = result.data
                // Get the data passed from EditActivity
                if (results != null) {
                    val newMap = results.extras!!.getSerializable(REQUEST_CODE) as UserMap
                    Log.i(TAG, "${newMap.title}, ${newMap.places[0].name}")
                    data.add(newMap)
                    adapter.notifyItemInserted(data.size - 1) // add new UserMap to rvMaps
                    serializeUserMaps(this, data) //save new data to file
                }
            }
        }
    }

    private fun setupAdapter() {
        adapter = MapsAdapter(this, data, object : MapsAdapter.OnItemClick {
            //When rvMaps item is clicked, opens user map in MapScreen activity
            override fun itemClickListener(position: Int) {
                val i = Intent(this@MainActivity, MapScreen::class.java)
                i.putExtra(MAP_LOCATION, data[position])
                startActivity(i)
                activityTransition()
            }
        }, object : MapsAdapter.OnEditClick {
            override fun editClickListener(position: Int) {
                Log.i(TAG, "Edit Clicked")
                dialogWindow(true, position)
            }
        }, object : MapsAdapter.OnDeleteClick{
            override fun deleteClickListener(position: Int) {
                deleteMap(position)
                // Need to persist data and add undo function
            }
        })
        rvMaps.adapter = adapter
    }

    private fun deleteMap(position: Int) {
        // delete data
        val deletedData = data[position] // saves the data that is deleted in variable
        data.removeAt(position)
        adapter.notifyItemRemoved(position)
        serializeUserMaps(this, data)
        // Create snackbar to allow user to restore data
        Snackbar.make(constraintLayout, "Item was deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                data.add(position, deletedData)
                adapter.notifyItemInserted(position)
                Log.i(TAG, "onClick: Undo was pressed. Data was added at position: $position")
            }
            .show()
    }

    // Function is used when any new data is created
    private fun serializeUserMaps(context: Context, data: MutableList<UserMap>) {
        ObjectOutputStream(FileOutputStream(getDataFile(context))).use { it.writeObject(data) }
    }

    // Function is used when saved data is read
    private fun deSerializeUserMaps(context: Context): List<UserMap> {
        val datafile = getDataFile(context)
        if (!datafile.exists()) {
            return emptyList()
        }
        ObjectInputStream(FileInputStream(datafile)).use {
            return it.readObject() as List<UserMap>
        }
    }

    private fun getDataFile(context: Context): File {
        return File(context.filesDir, FILENAME)
    }

    /** The following method is called when fabNewMap is pressed
     * A dialog window will appear and allow the user to enter a new map name
     * Once a valid name is entered, app will enter NewMaps activity**/
    private fun dialogWindow(isEdit : Boolean, position: Int){
        val newMapTitle = LayoutInflater.from(this).inflate(R.layout.new_map_title, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Enter Map Title")
            .setView(newMapTitle)
            .setPositiveButton("Ok", null)
            .setNegativeButton("Cancel", null)
            .show()
        val btnPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        val btnNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        btnPositive.setTextColor(ContextCompat.getColor(this, R.color.textcolor))
        btnNegative.setTextColor(ContextCompat.getColor(this, R.color.textcolor))
        btnPositive.setOnClickListener {
            mapTitle = newMapTitle.findViewById<EditText>(R.id.etMapName).text.toString()
            if (mapTitle.trim().isNotEmpty()) {
                when (isEdit){
                    true -> {
                        data[position] = UserMap(mapTitle, data[position].places)
                        Log.i(TAG, data[position].title)
                        adapter.notifyItemChanged(position)
                    }
                    else -> requestPermission()
                }
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Title is Required", Toast.LENGTH_SHORT).show()
            }
        }
    }
    /** The request permission launcher is used to prompt the user to make their
     * choice on allowing permission. The @suppressLint exists because the
     * fusedLocationClient does not have its expected permissions added. This
     * is handled by the (isGranted) check, because the program will never call fusedLocationClient
     * w/o checking if location permissions have been granted
     */

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Find current location and launch map
                fusedLocationClient.lastLocation.addOnCompleteListener {location ->
                    when (location.result){
                        null -> {
                            Toast.makeText(this, "Location not Found", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            startMap(location.result)
                        }
                    }
                }
            } else {
                // Permission is denied and map will start with default coordinates
                Log.i(TAG, "requestPermissionLauncher: Permission Denied")
                val defaultLoc = Location(DEFAULT_LOCATION_PROVIDER)
                defaultLoc.latitude = DEFAULT_LATITUDE
                defaultLoc.longitude = DEFAULT_LONGITUDE
                startMap(defaultLoc)
            }
        }

    /**
     * Checks whether permission has been granted and proceeds accordingly
     */
    private fun requestPermission() {
        when{
            // Check if permission has been granted, If it has, launch map activity
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED -> {
                Log.i(TAG, "requestPermission: Access granted, find current loc and launch")
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            // Check if the user needs to see additional dialog providing rationale for request
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) ->{
                dialogUI()
                Log.i(TAG, "requestPermission: User needs rationale")
            }
            // If rational is not needed, ask the user for permission
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                Log.i(TAG, "requestPermission: User has not been asked, asking now")
            }
        }
    }

    /**
     * Dialog designed to give user extra information on why Location is being used.
     */
    private fun dialogUI() {
        val infoView = LayoutInflater.from(this).inflate(R.layout.infoview, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Location Permissions")
            .setView(infoView)
            .setPositiveButton("OK", null)
            .show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            dialog.dismiss()
            Log.i(TAG,"dialogUI: requestPermissionLauncher.launch" )
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    private fun startMap(location: Location){
        // Will navigate to map activity either with default coordinates, or current coordinates.
        Log.i(TAG, "StartMap: ${location.latitude}, ${location.longitude}")
        val intent = Intent(this, NewMaps::class.java)
        intent.putExtra(LOCATION_KEY, location)
        intent.putExtra(NEW_MAP_TITLE, mapTitle)
        editActivityResultLauncher.launch(intent)
        activityTransition()
    }

    private fun activityTransition(){
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

}