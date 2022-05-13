package com.example.mymaps

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.models.UserMap
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.*
// Key values for data being passed from parent to child
const val MAP_LOCATION = "map location"
const val NEW_MAP_TITLE = "new title"
const val REQUEST_CODE = "new map"


private const val TAG = "MainActivity"

//File where UserMaps are stored
private const val FILENAME = "usermaps.data"

class MainActivity : AppCompatActivity() {

    private lateinit var editActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Upon startup, maps from data file are read
        val data = deSerializeUserMaps(this).toMutableList()

        val fabNewMap = findViewById<FloatingActionButton>(R.id.fabCreateMap)
        val rvMaps = findViewById<RecyclerView>(R.id.rvLocations)
        rvMaps.layoutManager = LinearLayoutManager(this)

        val adapter = MapsAdapter(this, data, object : MapsAdapter.OnItemClick {
            //When rvMaps item is clicked, opens user map in MapScreen activity
            override fun itemClickListener(position: Int) {
                val i = Intent(this@MainActivity, MapScreen::class.java)
                i.putExtra(MAP_LOCATION, data[position])
                startActivity(i)
            }
        })
        rvMaps.adapter = adapter
        fabNewMap.setOnClickListener {
            dialogWindow()
        }

        editActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            // If the user comes back to this activity from EditActivity
            // with no error or cancellation
            if (result.resultCode == Activity.RESULT_OK) {
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
    private fun dialogWindow() {
        val newMapTitle = LayoutInflater.from(this).inflate(R.layout.new_map_title, null)
        var title: String
        val dialog = AlertDialog.Builder(this)
            .setTitle("Create a New Map")
            .setView(newMapTitle)
            .setPositiveButton("Ok", null)
            .setNegativeButton("Cancel", null)
            .show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            title = newMapTitle.findViewById<EditText>(R.id.etMapName).text.toString()
            if (title.trim().isNotEmpty()) {
                val intent = Intent(this, NewMaps::class.java)
                intent.putExtra(NEW_MAP_TITLE, title)
                editActivityResultLauncher.launch(intent)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Title is Required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}