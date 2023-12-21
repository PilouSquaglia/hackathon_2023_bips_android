package com.example.hackathon_2023

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var polyline: Polyline

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
        val point2 = LatLng(42.605, 9.325)  // Remplacez ces coordonnées par celles de votre deuxième point
        val point3 = LatLng(42.61, 9.33)    // Remplacez ces coordonnées par celles de votre troisième point

        // Charger l'image originale depuis les ressources
        val originalIcon = BitmapFactory.decodeResource(resources, R.drawable.trash_bin)

        // Redimensionner l'image à la taille souhaitée
        val resizedIcon = Bitmap.createScaledBitmap(originalIcon, 100, 100, false)

        // Créer un BitmapDescriptor à partir de l'image redimensionnée
        val customIcon = BitmapDescriptorFactory.fromBitmap(resizedIcon)

        // Ajouter le marqueur avec l'icône redimensionnée pour le point de départ
        googleMap.addMarker(
            MarkerOptions()
                .position(depart)
                .title("Marker Title")
                .snippet("Marker Description")
                .icon(customIcon)
        )

        // Ajouter des marqueurs pour les points 2 et 3
        googleMap.addMarker(MarkerOptions().position(point2).title("Point 2"))
        googleMap.addMarker(MarkerOptions().position(point3).title("Point 3"))

        // Centrer la carte sur le premier point
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(depart, 14f))

        // Tracer l'itinéraire
        drawRoute(depart, point2, point3)
    }

    private fun drawRoute(point1: LatLng, point2: LatLng, point3: LatLng) {
        // Ajouter des polylignes pour connecter les points
        val color = ContextCompat.getColor(this, R.color.red)
        polyline = googleMap.addPolyline(
            PolylineOptions()
                .add(point1, point2, point3)
                .width(5f)
                .color(color)
                .geodesic(true)
                .startCap(RoundCap())
                .endCap(RoundCap())
                .jointType(JointType.ROUND)
                .pattern(listOf(Dot(), Gap(10f)))
        )
    }
}
