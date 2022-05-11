package com.example.mymaps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.models.Place
import com.example.mymaps.models.UserMap

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val data = generateData()
        val rvLocation = findViewById<RecyclerView>(R.id.rvLocations)
        rvLocation.layoutManager = LinearLayoutManager(this)
        rvLocation.adapter = MapsAdapter(this, data)
    }

    private fun generateData() : List<UserMap>{
        val list = mutableListOf<UserMap>()
        for (i in 0..15) {
            list.add( UserMap(
                "The name is $i",
                listOf(
                Place("My favorite place", "Cool", i.toDouble(), i.toDouble())
                )
                ))
        }
        return list
    }
}