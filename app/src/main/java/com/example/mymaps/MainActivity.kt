package com.example.mymaps

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.models.Place
import com.example.mymaps.models.UserMap
import com.google.android.material.floatingactionbutton.FloatingActionButton

const val MAP_LOCATION = "map location"
const val REQUEST_CODE = "new map"
private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    private lateinit var editActivityResultLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val data = generateData()
        val fabNewMap = findViewById<FloatingActionButton>(R.id.fabCreateMap)
        val rvLocation = findViewById<RecyclerView>(R.id.rvLocations)
        rvLocation.layoutManager = LinearLayoutManager(this)
        rvLocation.adapter = MapsAdapter(this, data, object : MapsAdapter.onItemClick{
            override fun itemClickListener(position: Int) {
                Log.i(TAG, "Clicked at $position")
                val i = Intent(this@MainActivity, MapScreen::class.java)
                i.putExtra(MAP_LOCATION, data[position])
                startActivity(i)
            }
        })
        fabNewMap.setOnClickListener {
            val intent = Intent(this, ResultTest::class.java)
            editActivityResultLauncher.launch(intent)
        }

         editActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            // If the user comes back to this activity from EditActivity
            // with no error or cancellation
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                // Get the data passed from EditActivity
                if (data != null) {
                    val editedString = data.extras!!.getSerializable("newString") as UserMap
                    Log.i(TAG, editedString.title)
                }
            }
        }

        fun startEditActivity() {

        }
    }

    private fun generateData() : List<UserMap>{
        val list = mutableListOf<UserMap>()
        list.add(UserMap("Favorite places in Monroe", listOf(
                Place("Cookout",
                    "Fast-Food Joint",
                    35.0,-80.5),
                Place("Monroe Aquatic Center",
                "Fun place to swim and play",
                    35.009107258692595, -80.5657304840066),
                Place("CATA",
                    "Where I went to school",
                    34.97316186116537, -80.56763576487599),
                Place("My Home",
                    "Where I grew up",
                    35.03906690180318, -80.37885118782752))))
        list.add(
            UserMap("College Hangout Spots", listOf(
                Place("Norton",
                    "My Dorm",
                    35.31431034535912, -83.1852156889667),
                Place("Live Forgiven Church",
                "Where I went to Church",
                    35.33200042434107, -83.19878991907268),
                Place("Walmart",
                "Where we shopped for groceries",
                    35.361733114874355, -83.20457890908)
            )))
        return list
    }
}