package com.example.mymaps

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.models.UserMap

private const val TAG = "MapsAdapter"

class MapsAdapter(val context: Context, val data : List<UserMap>, val onClickListener: onItemClick) : RecyclerView.Adapter<MapsAdapter.ViewHolder>() {

    interface onItemClick{
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
        val tvLocation = itemView.findViewById<TextView>(R.id.tvPlace)
        fun bind(item : UserMap){
            Log.i(TAG, item.title)
            tvLocation.text = item.title
        }
    }

}
