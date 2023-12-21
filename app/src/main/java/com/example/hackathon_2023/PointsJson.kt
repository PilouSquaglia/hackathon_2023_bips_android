package com.example.hackathon_2023

data class Point(val lat: Double, val lng: Double)

data class PointsJson(val points: List<Point>)