package com.example.hackathon_2023

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap

        val depart = LatLng(42.60080491507166, 9.322923935409024)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(depart, 14f))

        val originalIcon = BitmapFactory.decodeResource(resources, R.drawable.trash_bin)

        val resizedIcon = Bitmap.createScaledBitmap(originalIcon, 100, 100, false)

        // Créer un BitmapDescriptor à partir de l'image redimensionnée
        val customIcon = BitmapDescriptorFactory.fromBitmap(resizedIcon)

        googleMap.addMarker(
            MarkerOptions()
                .position(depart)
                .title("Marker Title")
                .snippet("Marker Description")
                .icon(customIcon)
        )
    }
}
