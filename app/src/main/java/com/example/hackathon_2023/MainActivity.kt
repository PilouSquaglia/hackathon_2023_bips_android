package com.example.hackathon_2023

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.maps.android.PolyUtil

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var polyline: Polyline

    private lateinit var btnZoomIn: Button
    private lateinit var btnZoomOut: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        btnZoomIn = findViewById(R.id.btnZoomIn)
        btnZoomOut = findViewById(R.id.btnZoomOut)

        btnZoomIn.setOnClickListener {
            googleMap.animateCamera(CameraUpdateFactory.zoomIn())
        }

        btnZoomOut.setOnClickListener {
            googleMap.animateCamera(CameraUpdateFactory.zoomOut())
        }
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

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // Example coordinates for the route
        val origin = LatLng(42.59513880243619, 9.32103294032586)
        val destination = LatLng(42.59584862940876, 9.328090874946884)

        val originalIcon = BitmapFactory.decodeResource(resources, R.drawable.trash_bin)

        val resizedIcon = Bitmap.createScaledBitmap(originalIcon, 100, 100, false)

        val customIcon = BitmapDescriptorFactory.fromBitmap(resizedIcon)

        googleMap.addMarker(
            MarkerOptions()
                .position(origin)
                .title("Marker Title")
                .snippet("Marker Description")
                .icon(customIcon)
        )

        // Add markers for the points
        googleMap.addMarker(MarkerOptions().position(destination).title("Destination"))

        // Move the camera to the origin
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 14f))

        // Draw the route
        getDirections(origin, destination)
    }

    private fun getDirections(origin: LatLng, destination: LatLng) {
        val apiKey = "AIzaSyBHYb9-kS4yMnYYDgdovD2EUpr8G_iFexQ" // Replace with your actual API key
        val context = GeoApiContext.Builder().apiKey(apiKey).build()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val directionsResult = DirectionsApi.newRequest(context)
                    .mode(TravelMode.DRIVING)
                    .origin(com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                    .destination(com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                    .await()
                drawDetailedRoute(directionsResult)
            } catch (e: Exception) {
                // Handle exception
                e.printStackTrace()
            }
        }
    }

    private fun drawDetailedRoute(directionsResult: DirectionsResult) {
        val route = directionsResult.routes[0]
        val leg = route.legs[0]

        val polylineOptions = PolylineOptions()
            .width(5f)
            .color(Color.RED)

        for (step in leg.steps) {
            val points = step.polyline.decodePath()
            val convertedPoints = ArrayList<LatLng>()

            // Convertir les points de com.google.maps.model.LatLng à com.google.android.gms.maps.model.LatLng
            for (point in points) {
                convertedPoints.add(LatLng(point.lat, point.lng))
            }

            polylineOptions.addAll(convertedPoints)
        }

        polyline = googleMap.addPolyline(polylineOptions)
    }


}
