package com.quickmeds24.app.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

data class PharmacyDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("branch") val branch: String,
    @SerializedName("rating") val rating: Double,
    @SerializedName("distance_km") val distanceKm: Double,
    @SerializedName("is_open") val isOpen: Boolean
)

data class MedicineDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("generic_name") val genericName: String,
    @SerializedName("brand") val brand: String,
    @SerializedName("price") val price: Double,
    @SerializedName("in_stock") val inStock: Boolean
)

data class NearbySearchResponse(
    @SerializedName("area") val area: String,
    @SerializedName("targeted_pharmacy") val targetedPharmacy: PharmacyDto,
    @SerializedName("medicines") val medicines: List<MedicineDto>
)

/**
 * NearbyPharmaciesApi
 * Retrofit API interface using Kotlin Coroutines `suspend` functions for background thread IO execution.
 */
interface NearbyPharmaciesApi {

    @GET("api/v1/pharmacies/nearby")
    suspend fun getNearbyPharmacies(
        @Query("area") area: String = "Alpha 1",
        @Query("lat") latitude: Double = 28.4735,
        @Query("lng") longitude: Double = 77.5098
    ): List<PharmacyDto>

    @GET("api/v1/medicines/search")
    suspend fun searchMedicines(
        @Query("q") query: String,
        @Query("area") area: String = "Alpha 1"
    ): NearbySearchResponse
}

