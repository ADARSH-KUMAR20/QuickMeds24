package com.quickmeds24.app.data.model

import com.google.gson.annotations.SerializedName

data class Pharmacy(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("branch") val branch: String,
    @SerializedName("address") val address: String,
    @SerializedName("rating") val rating: Double,
    @SerializedName("reviews_count") val reviewsCount: Int,
    @SerializedName("distance_km") val distanceKm: Double,
    @SerializedName("estimated_delivery_minutes") val estimatedDeliveryMinutes: Int,
    @SerializedName("is_open") val isOpen: Boolean,
    @SerializedName("license_no") val licenseNo: String,
    @SerializedName("coordinates") val coordinates: Coordinates,
    @SerializedName("medicines") val medicines: List<Medicine> = emptyList()
)

