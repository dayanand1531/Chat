package com.example.chat.map

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.R
import com.example.chat.api.model.entity.Message
import com.example.chat.databinding.ItemReceviedLocationBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ReceviedMapViewHolder (val binding: ItemReceviedLocationBinding)  : RecyclerView.ViewHolder(binding.root) {


    fun bind(context: Context, message: Message) {
        // Initialize the MapView
        binding.mvRecevied.onCreate(null)
        binding.mvRecevied.getMapAsync { googleMap ->
            // Set up your map here (e.g., set markers, camera position)
            val latLng = LatLng(message.latitude, message.longitude) // Example coordinates
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            googleMap.addMarker(MarkerOptions().position(latLng).title("Marker in Sydney"))
        }
    }

    fun onDestroy() {
        binding.mvRecevied.onDestroy()
    }

    fun onResume() {
        binding.mvRecevied.onResume()
    }

    fun onPause() {
        binding.mvRecevied.onPause()
    }

    fun onLowMemory() {
        binding.mvRecevied.onLowMemory()
    }

    fun onSaveInstanceState(outState: Bundle) {
        binding.mvRecevied.onSaveInstanceState(outState)
    }
}