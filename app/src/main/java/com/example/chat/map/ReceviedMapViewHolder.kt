package com.example.chat.map

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.R
import com.example.chat.api.model.entity.Message
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ReceviedMapViewHolder (view: View)  : RecyclerView.ViewHolder(view) {
    val mapView: MapView = view.findViewById(R.id.mvRecevied)

    fun bind(context: Context, message: Message) {
        // Initialize the MapView
        mapView.onCreate(null)
        mapView.getMapAsync { googleMap ->
            // Set up your map here (e.g., set markers, camera position)
            val latLng = LatLng(message.latitude, message.longitude) // Example coordinates
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            googleMap.addMarker(MarkerOptions().position(latLng).title("Marker in Sydney"))
        }
    }

    fun onDestroy() {
        mapView.onDestroy()
    }

    fun onResume() {
        mapView.onResume()
    }

    fun onPause() {
        mapView.onPause()
    }

    fun onLowMemory() {
        mapView.onLowMemory()
    }

    fun onSaveInstanceState(outState: Bundle) {
        mapView.onSaveInstanceState(outState)
    }
}