package com.example.mymaps

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.models.UserMap

private const val TAG = "MapsAdapter"

class MapsAdapter(private val context: Context, private val data : List<UserMap>, private val onClickListener: OnItemClick) : RecyclerView.Adapter<MapsAdapter.ViewHolder>() {

    interface OnItemClick{
        fun itemClickListener(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.place_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onClickListener.itemClickListener(position)
        }
    }

    override fun getItemCount() = data.size

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val tvLocation: TextView = itemView.findViewById(R.id.tvPlace)
        private val tvPlaces : TextView = itemView.findViewById(R.id.tvNumPlaces)
        @SuppressLint("SetTextI18n")
        fun bind(item : UserMap){
            Log.i(TAG, item.title)
            tvLocation.text = item.title
            tvPlaces.text = "${item.places.size} places"
        }
    }

}
