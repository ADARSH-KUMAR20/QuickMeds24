package com.quickmeds24.app.data.model

import com.google.gson.annotations.SerializedName

data class DeliveryLocation(
    @SerializedName("id") val id: String,
    @SerializedName("area") val area: String,
    @SerializedName("landmark") val landmark: String? = null,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("pincode") val pincode: String,
    @SerializedName("full_address") val fullAddress: String,
    @SerializedName("coordinates") val coordinates: Coordinates
)

