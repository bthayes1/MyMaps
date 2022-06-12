package com.example.mymaps

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.models.UserMap

private const val TAG = "MapsAdapter"

class MapsAdapter(
    private val context: Context,
    private val data : List<UserMap>,
    private val onClickListener: OnItemClick,
    private val editClickListener : OnEditClick,
    private val deleteClickListener : OnDeleteClick)
    : RecyclerView.Adapter<MapsAdapter.ViewHolder>() {

    interface OnItemClick{
        fun itemClickListener(position: Int)
    }

    interface OnDeleteClick {
        fun deleteClickListener(position: Int)
    }

    interface OnEditClick{
        fun editClickListener(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.place_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.itemFrame.setOnClickListener {
            Log.i(TAG, "Item selected: $position")
            onClickListener.itemClickListener(position)
        }
        holder.btnEdit.setOnClickListener {
            editClickListener.editClickListener(position)
        }
        holder.btnDelete.setOnClickListener {
            Log.i(TAG, "Item deleted: $position")
            deleteClickListener.deleteClickListener(position)
        }

    }


    override fun getItemCount() = data.size

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val tvLocation: TextView = itemView.findViewById(R.id.tvPlace)
        private val tvPlaces : TextView = itemView.findViewById(R.id.tvNumPlaces)
        val btnEdit : ImageButton = itemView.findViewById(R.id.btnEdit)
        val itemFrame : FrameLayout = itemView.findViewById(R.id.itemFrame)
        val btnDelete : ImageButton = itemView.findViewById(R.id.btnDelete)
        @SuppressLint("SetTextI18n")
        fun bind(item : UserMap){
            Log.i(TAG, item.title)
            tvLocation.text = item.title
            when(item.places.size > 1){
                true -> tvPlaces.text = "${item.places.size} places"
                false -> tvPlaces.text = "${item.places.size} place"
            }
        }
    }
}
