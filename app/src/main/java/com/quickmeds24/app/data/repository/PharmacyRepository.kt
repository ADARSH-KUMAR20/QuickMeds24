package com.quickmeds24.app.data.repository

import android.content.Context
import androidx.collection.LruCache
import com.google.gson.annotations.SerializedName
import com.quickmeds24.app.data.model.Coordinates
import com.quickmeds24.app.data.model.DeliveryLocation
import com.quickmeds24.app.data.model.Medicine
import com.quickmeds24.app.data.model.Pharmacy
import com.quickmeds24.app.utils.GeoLocationUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class PharmacyDataResponse(
    @SerializedName("selected_location") val selectedLocation: DeliveryLocation,
    @SerializedName("available_locations") val availableLocations: List<DeliveryLocation>,
    @SerializedName("pharmacies") val pharmacies: List<Pharmacy>
)

data class MedicineSearchResult(
    val medicine: Medicine,
    val pharmacy: Pharmacy,
    val storeName: String = pharmacy.name,
    val storeAddress: String = pharmacy.address,
    val sectorTag: String = extractSectorTag(pharmacy),
    val distanceKm: Double = pharmacy.distanceKm,
    val deliveryMinutes: Int = pharmacy.estimatedDeliveryMinutes,
    val isNearestStoreFallback: Boolean = false,
    val fallbackReason: String? = null
)

fun extractSectorTag(pharmacy: Pharmacy): String {
    val addr = pharmacy.address
    return when {
        addr.contains("Alpha 1", ignoreCase = true) -> "Alpha 1"
        addr.contains("Alpha 2", ignoreCase = true) -> "Alpha 2"
        addr.contains("Knowledge Park 2", ignoreCase = true) -> "Knowledge Park 2"
        addr.contains("Knowledge Park 3", ignoreCase = true) -> "Knowledge Park 3"
        addr.contains("Sector 22", ignoreCase = true) -> "Sector 22"
        addr.contains("Sector 76", ignoreCase = true) -> "Sector 76"
        addr.contains("Sector 50", ignoreCase = true) -> "Sector 50"
        addr.contains("Sector 137", ignoreCase = true) -> "Sector 137"
        addr.contains("Beta 1", ignoreCase = true) -> "Beta 1"
        addr.contains("Beta 2", ignoreCase = true) -> "Beta 2"
        addr.contains("Gamma 1", ignoreCase = true) -> "Gamma 1"
        addr.contains("Gamma 2", ignoreCase = true) -> "Gamma 2"
        addr.contains("Delta 1", ignoreCase = true) -> "Delta 1"
        addr.contains("Delta 2", ignoreCase = true) -> "Delta 2"
        addr.contains("Pari Chowk", ignoreCase = true) -> "Pari Chowk"
        addr.contains("Omega 1", ignoreCase = true) -> "Omega 1"
        addr.contains("Pi 1", ignoreCase = true) -> "Pi 1"
        addr.contains("Chi 2", ignoreCase = true) -> "Chi 2"
        addr.contains("Jagat Farm", ignoreCase = true) -> "Jagat Farm"
        addr.contains("Surajpur", ignoreCase = true) -> "Surajpur"
        addr.contains("Ecotech 3", ignoreCase = true) -> "Ecotech 3"
        else -> pharmacy.branch
    }
}

data class PagedSearchResult(
    val items: List<MedicineSearchResult>,
    val totalCount: Int,
    val page: Int,
    val hasMore: Boolean
)

class PharmacyRepository(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    @Volatile
    private var cachedData: PharmacyDataResponse? = null

    // High-performance In-Memory Search Cache (150 query entries)
    private val searchCache = LruCache<String, List<MedicineSearchResult>>(150)

    suspend fun getPharmacyData(userLocation: DeliveryLocation? = null): PharmacyDataResponse = withContext(ioDispatcher) {
        val rawData = cachedData ?: run {
            val allPharmacies = PharmacyDataGenerator.generateAllPharmacies()
            val locations = PharmacyDataGenerator.LOCATIONS
            val response = PharmacyDataResponse(
                selectedLocation = locations.first(),
                availableLocations = locations,
                pharmacies = allPharmacies
            )
            cachedData = response
            response
        }

        val userCoords = userLocation?.coordinates ?: rawData.selectedLocation.coordinates
        val updatedPharmacies = rawData.pharmacies.map { pharm ->
            val dist = GeoLocationUtils.calculateDistanceKm(userCoords, pharm.coordinates)
            val mins = GeoLocationUtils.calculateDeliveryMinutes(dist)
            pharm.copy(
                distanceKm = dist,
                estimatedDeliveryMinutes = mins
            )
        }.sortedBy { it.distanceKm }

        PharmacyDataResponse(
            selectedLocation = userLocation ?: rawData.selectedLocation,
            availableLocations = rawData.availableLocations,
            pharmacies = updatedPharmacies
        )
    }

    /**
     * Fast cached search with query caching, dynamic coordinate-based Haversine distance,
     * nearest-pharmacies-first multi-store interleaving (Alpha 1, Alpha 2, Knowledge Park 2,
     * Sector 22, Sector 76), and targeted single-pharmacy filtering when a store is selected.
     */
    suspend fun searchMedicines(
        query: String,
        categoryFilter: String? = null,
        userLocation: DeliveryLocation? = null,
        pharmacyId: String? = null
    ): List<MedicineSearchResult> = withContext(ioDispatcher) {
        val cleanQuery = query.trim().lowercase()
        val userCoords = userLocation?.coordinates ?: Coordinates(28.4735, 77.5098)
        val cacheKey = "$cleanQuery|${categoryFilter ?: "ALL"}|${pharmacyId ?: "ALL"}|${userCoords.latitude},${userCoords.longitude}"

        // Check LruCache for instant 0ms hit
        searchCache.get(cacheKey)?.let { return@withContext it }

        val data = getPharmacyData(userLocation)
        val sortedPharmacies = data.pharmacies // already sorted by nearest coordinates ascending

        val results = ArrayList<MedicineSearchResult>(500)

        fun matchesQuery(med: Medicine): Boolean {
            if (cleanQuery.isEmpty()) return true
            return med.name.lowercase().contains(cleanQuery) ||
                   med.genericName.lowercase().contains(cleanQuery) ||
                   med.brand.lowercase().contains(cleanQuery) ||
                   med.tags.any { it.lowercase().contains(cleanQuery) }
        }

        // Case 1: Specific Pharmacy Selected (e.g. User clicked "Wellness Forever Alpha 2")
        if (!pharmacyId.isNullOrBlank()) {
            val targetedPharm = sortedPharmacies.find { it.id == pharmacyId }
            if (targetedPharm != null) {
                val sector = extractSectorTag(targetedPharm)
                for (med in targetedPharm.medicines) {
                    if (matchesQuery(med)) {
                        results.add(
                            MedicineSearchResult(
                                medicine = med,
                                pharmacy = targetedPharm,
                                storeName = targetedPharm.name,
                                storeAddress = targetedPharm.address,
                                sectorTag = sector,
                                distanceKm = targetedPharm.distanceKm,
                                deliveryMinutes = targetedPharm.estimatedDeliveryMinutes,
                                isNearestStoreFallback = false
                            )
                        )
                    }
                }
            }
            searchCache.put(cacheKey, results)
            return@withContext results
        }

        // Case 2: Multi-Store Interleaved Search across all 50+ registered pharmacies
        // Groups matching medicines per pharmacy, then interleaves 2 medicines per pharmacy
        // so that scrolling down moves smoothly from Alpha 1 -> Alpha 2 (Wellness Forever) ->
        // Knowledge Park 2 -> Sector 22 -> Sector 76!
        val pharmMatches = mutableListOf<Pair<Pharmacy, List<Medicine>>>()
        for (pharm in sortedPharmacies) {
            val matched = pharm.medicines.filter { matchesQuery(it) }
            if (matched.isNotEmpty()) {
                pharmMatches.add(pharm to matched)
            }
        }

        if (pharmMatches.isNotEmpty()) {
            val maxMeds = pharmMatches.maxOf { it.second.size }
            val seenMedNames = mutableSetOf<String>()
            val chunkSize = 2

            for (startIdx in 0 until maxMeds step chunkSize) {
                for ((pharm, medList) in pharmMatches) {
                    val endIdx = (startIdx + chunkSize).coerceAtMost(medList.size)
                    if (startIdx < medList.size) {
                        val sector = extractSectorTag(pharm)
                        val isFarStore = pharm.distanceKm > 2.5

                        for (i in startIdx until endIdx) {
                            val med = medList[i]
                            val key = "${med.name.lowercase()}|${pharm.id}"
                            if (!seenMedNames.contains(key)) {
                                seenMedNames.add(key)
                                results.add(
                                    MedicineSearchResult(
                                        medicine = med,
                                        pharmacy = pharm,
                                        storeName = pharm.name,
                                        storeAddress = pharm.address,
                                        sectorTag = sector,
                                        distanceKm = pharm.distanceKm,
                                        deliveryMinutes = pharm.estimatedDeliveryMinutes,
                                        isNearestStoreFallback = isFarStore,
                                        fallbackReason = if (isFarStore) {
                                            "Available at ${pharm.name} (${pharm.distanceKm} km • ${pharm.estimatedDeliveryMinutes} min)"
                                        } else null
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Cache the result
        searchCache.put(cacheKey, results)
        results
    }

    /**
     * Paginated search maintaining lightweight UI memory footprint (5 items per page).
     */
    suspend fun searchMedicinesPaged(
        query: String,
        page: Int = 1,
        pageSize: Int = 5,
        userLocation: DeliveryLocation? = null,
        pharmacyId: String? = null
    ): PagedSearchResult = withContext(ioDispatcher) {
        val allResults = searchMedicines(
            query = query,
            userLocation = userLocation,
            pharmacyId = pharmacyId
        )
        val fromIndex = (page - 1) * pageSize
        val toIndex = (fromIndex + pageSize).coerceAtMost(allResults.size)

        val pagedItems = if (fromIndex < allResults.size) {
            allResults.subList(fromIndex, toIndex)
        } else {
            emptyList()
        }

        PagedSearchResult(
            items = pagedItems,
            totalCount = allResults.size,
            page = page,
            hasMore = toIndex < allResults.size
        )
    }

    fun clearCache() {
        searchCache.evictAll()
    }
}
