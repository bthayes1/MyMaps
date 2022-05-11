package com.example.mymaps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.mymaps.models.Place
import com.example.mymaps.models.UserMap

class ResultTest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_test)

        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val etTest = findViewById<EditText>(R.id.etTest)

        btnSubmit.setOnClickListener {
                // Prepare data intent
                val data = Intent()
                val newLoc = generateData()
                // Pass relevant data back as a result
                data.putExtra("newString", newLoc)
                // Activity finished ok, return the data
                setResult(RESULT_OK, data) // set result code and bundle data for response
                finish() // closes the activity, pass data to parent

        }
    }
    private fun generateData() : UserMap{
           return UserMap("Deployment Hangouts", listOf(
            Place("Tent",
                "Where I slept",
                26.9,-80.5),
            Place("DFAC",
                "Where I ate",
                29.69664643893253, 47.42288423605212),
            Place("PX",
                "Where I shopped",
                29.69712057121958, 47.426853845421604)))
    }


}