package com.example.pblmobile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import android.util.Log

class EventAdapter(private val items: List<EventItem>, private val onItemClick: (EventItem) -> Unit) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    // ViewHolder class for the adapter
    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewEvent)
        val titleTextView: TextView = view.findViewById(R.id.textViewEventTitle)
        val dateTextView: TextView = view.findViewById(R.id.textViewEventDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event_card, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = items[position]

        // Base URL for images
        val baseUrl = "https://tkj-3b.com/api/kelompok_1/"
        val imageUrl = baseUrl + event.gambar

        // Log the full image URL for debugging purposes
        Log.d("EventAdapter", "Image URL: $imageUrl")

        // Use Glide to load the image with placeholder, error handling, and smooth transition
        Glide.with(holder.itemView.context)
            .load(imageUrl)  // Full URL for the image
            .apply(RequestOptions()
                .placeholder(R.drawable.loadings) // Placeholder image while loading
                .error(R.drawable.gambartdktersedia)) // Error image in case the image fails to load
            .transition(DrawableTransitionOptions.withCrossFade()) // Smooth image transition
            .into(holder.imageView)

        // Set the event title and date
        holder.titleTextView.text = event.judul
        holder.dateTextView.text = event.waktu

        // Handle item click
        holder.itemView.setOnClickListener {
            onItemClick(event)
        }
    }

    override fun getItemCount() = items.size
}
