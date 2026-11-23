package com.quickmeds24.app.data.model

import com.google.gson.annotations.SerializedName

data class Coordinates(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)

data class Rider(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("rating") val rating: Double,
    @SerializedName("vehicle_number") val vehicleNumber: String,
    @SerializedName("avatar") val avatarUrl: String
)

data class TargetedPharmacy(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("branch") val branch: String,
    @SerializedName("distance_km") val distanceKm: Double,
    @SerializedName("address") val address: String,
    @SerializedName("coordinates") val coordinates: Coordinates
)

enum class TrackingStatus {
    ORDER_PLACED,
    RIDER_ASSIGNED,
    HEADING_TO_PHARMACY,
    ARRIVED_AT_PHARMACY,
    MEDICINES_PICKED_UP,
    ON_THE_WAY,
    NEARBY,
    DELIVERED
}

data class TrackingTelemetry(
    @SerializedName("orderId") val orderId: String,
    @SerializedName("status") val status: TrackingStatus,
    @SerializedName("statusText") val statusText: String,
    @SerializedName("statusStepIndex") val statusStepIndex: Int,
    @SerializedName("rider") val rider: Rider,
    @SerializedName("targetedPharmacy") val targetedPharmacy: TargetedPharmacy,
    @SerializedName("currentLocation") val currentLocation: Coordinates,
    @SerializedName("routeCoordinates") val routeCoordinates: List<Coordinates> = emptyList(),
    @SerializedName("isMedicinePickedUp") val isMedicinePickedUp: Boolean,
    @SerializedName("pickedUpTime") val pickedUpTime: String?,
    @SerializedName("deliveryOtp") val deliveryOtp: String, // e.g. "4821"
    @SerializedName("etaMinutes") val etaMinutes: Int,
    @SerializedName("distanceRemainingKm") val distanceRemainingKm: Double,
    @SerializedName("lastUpdatedTimestamp") val lastUpdatedTimestamp: Long,
    @SerializedName("updateFrequencySeconds") val updateFrequencySeconds: Int,
    @SerializedName("isContinuousActive") val isContinuousActive: Boolean,
    @SerializedName("speedKmh") val speedKmh: Int = 32,
    @SerializedName("totalSecondsRemaining") val totalSecondsRemaining: Int = 720
)

data class InAppPushNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: PushType,
    val timestamp: Long = System.currentTimeMillis()
)

enum class PushType {
    ORDER_PICKED,
    DRIVER_ARRIVED,
    ORDER_CONFIRMED
}
