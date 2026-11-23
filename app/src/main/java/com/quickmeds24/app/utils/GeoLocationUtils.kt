package com.quickmeds24.app.utils

import com.quickmeds24.app.data.model.Coordinates
import kotlin.math.*

/**
 * GeoLocationUtils
 * Computes exact geodesic distance using Haversine formula
 * and derives real delivery time in minutes based on distance.
 */
object GeoLocationUtils {

    /**
     * Calculates the distance in kilometers between two GPS coordinates using the Haversine formula.
     */
    fun calculateDistanceKm(c1: Coordinates, c2: Coordinates): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(c2.latitude - c1.latitude)
        val dLon = Math.toRadians(c2.longitude - c1.longitude)
        val lat1Rad = Math.toRadians(c1.latitude)
        val lat2Rad = Math.toRadians(c2.latitude)

        val a = sin(dLat / 2).pow(2) + sin(dLon / 2).pow(2) * cos(lat1Rad) * cos(lat2Rad)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val dist = earthRadiusKm * c
        return (round(dist * 10.0) / 10.0).coerceAtLeast(0.4)
    }

    /**
     * Calculates delivery time in minutes based on distance:
     * - Under 1 km: ~10 - 12 mins
     * - 1 - 3 km: ~15 - 18 mins
     * - 3 - 5 km: ~18 - 22 mins
     * - 5 - 8 km: ~25 - 30 mins
     * - 8 - 12 km+: ~35 - 45 mins
     */
    fun calculateDeliveryMinutes(distanceKm: Double): Int {
        return when {
            distanceKm <= 1.0 -> 12
            distanceKm <= 3.0 -> 15 + ((distanceKm - 1.0) * 1.8).toInt()
            distanceKm <= 5.0 -> 18 + ((distanceKm - 3.0) * 2.0).toInt()
            distanceKm <= 8.0 -> 24 + ((distanceKm - 5.0) * 2.5).toInt()
            else -> (32 + ((distanceKm - 8.0) * 3.0)).toInt().coerceAtMost(60)
        }
    }
}

