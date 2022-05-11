package com.example.mymaps

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.models.UserMap

const val TAG = "MapsAdapter"

class MapsAdapter(val context: Context, val data : List<UserMap>) : RecyclerView.Adapter<MapsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.place_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount() = data.size

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val tvLocation = itemView.findViewById<TextView>(R.id.tvPlace)
        fun bind(item : UserMap){
            Log.i(TAG, item.title)
            tvLocation.text = item.title
        }
    }

}
