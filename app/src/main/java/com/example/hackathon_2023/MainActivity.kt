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
import com.google.maps.android.PolyUtil
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

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

        // Load points from JSON
        val points = loadPointsFromJson()

        for (point in points) {
            addMarker(point)
        }

        println("######################")
        println(points)

        // Example coordinates for the route
        val origin = points[0]
        val destination = points[points.size - 1]

        // Draw the route
        drawRoute(points)

        // Move the camera to the origin
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 14f))
    }

    private fun drawRoute(points: List<LatLng>) {
        for (i in 0 until points.size - 1) {
            val origin = points[i]
            val destination = points[i + 1]

            // Get directions between the current and next point
            val directions = getDirections(origin, destination)

            // Draw the detailed route on the map
            drawDetailedRoute(directions)
        }
    }

    private fun getDirections(origin: LatLng, destination: LatLng): DirectionsResult {
        val apiKey = "AIzaSyBHYb9-kS4yMnYYDgdovD2EUpr8G_iFexQ"
        val context = GeoApiContext.Builder().apiKey(apiKey).build()

        try {
            return DirectionsApi.newRequest(context)
                .mode(TravelMode.DRIVING)
                .origin(com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                .destination(com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                .await()
        } catch (e: Exception) {
            // Gérer l'exception
            e.printStackTrace()
            // En cas d'erreur, retourner une instance vide de DirectionsResult
            return DirectionsResult()
        }
    }

    private fun drawDetailedRoute(directionsResult: DirectionsResult) {
        val route = directionsResult.routes[0]
        val leg = route.legs[0]

        val polylineOptions = PolylineOptions()
            .width(5f)
            .color(Color.BLUE)

        for (step in leg.steps) {
            val points = step.polyline.decodePath()
            for (point in points) {
                val convertedPoint = LatLng(point.lat, point.lng)
                polylineOptions.add(convertedPoint)
            }
        }

        googleMap.addPolyline(polylineOptions)
    }

    private fun decodePolyline(encodedPath: String): List<LatLng> {
        val polyUtilPoints = PolyUtil.decode(encodedPath)
        return polyUtilPoints.map { LatLng(it.latitude, it.longitude) }
    }

    private fun loadPointsFromJson(): List<LatLng> {
        val points = ArrayList<LatLng>()
        val jsonString = loadJsonFromAsset("route.json")

        Log.d("JsonContent", jsonString)

        try {
            val jsonObject = JSONObject(jsonString)
            val pointsArray = jsonObject.getJSONArray("points")

            for (i in 0 until pointsArray.length()) {
                val jsonPoint = pointsArray.getJSONObject(i)
                val lat = jsonPoint.getDouble("lat")
                val lng = jsonPoint.getDouble("lng")
                points.add(LatLng(lat, lng))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return points
    }

    private fun loadJsonFromAsset(fileName: String): String {
        var json: String? = null
        try {
            val inputStream = assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }

        return json ?: ""
    }

    private fun addMarker(point: LatLng) {
        val originalIcon = BitmapFactory.decodeResource(resources, R.drawable.trash_bin)
        val resizedIcon = Bitmap.createScaledBitmap(originalIcon, 100, 100, false)
        val customIcon = BitmapDescriptorFactory.fromBitmap(resizedIcon)

        googleMap.addMarker(
            MarkerOptions()
                .position(point)
                .title("Marker Title")
                .snippet("Marker Description")
                .icon(customIcon)
        )
    }

}
