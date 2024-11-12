package com.example.chat.map

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.R
import com.example.chat.api.model.entity.Message
import com.example.chat.databinding.ItemSendLocationBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class SendMapViewHolder(val binding: ItemSendLocationBinding)  : RecyclerView.ViewHolder(binding.root) {

    fun bind(context: Context, message: Message) {
        // Initialize the MapView
        binding.mapView.onCreate(null)
        binding.mapView.getMapAsync { googleMap ->
            // Set up your map here (e.g., set markers, camera position)
            val latLng = LatLng(message.latitude, message.longitude) // Example coordinates
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            googleMap.addMarker(MarkerOptions().position(latLng).title("Marker in Sydney"))
        }
    }

    fun onDestroy() {
        binding.mapView.onDestroy()
    }

    fun onResume() {
        binding.mapView.onResume()
    }

    fun onPause() {
        binding.mapView.onPause()
    }

    fun onLowMemory() {
        binding.mapView.onLowMemory()
    }

    fun onSaveInstanceState(outState: Bundle) {
        binding.mapView.onSaveInstanceState(outState)
    }
}