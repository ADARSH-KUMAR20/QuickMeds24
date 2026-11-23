package com.quickmeds24.app.data.model

import com.google.gson.annotations.SerializedName

data class MedicineSubstitute(
    @SerializedName("name") val name: String,
    @SerializedName("manufacturer") val manufacturer: String,
    @SerializedName("price") val price: Double
)

data class Medicine(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("generic_name") val genericName: String,
    @SerializedName("brand") val brand: String,
    @SerializedName("category") val category: String,
    @SerializedName("dosage_form") val dosageForm: String,
    @SerializedName("price") val price: Double,
    @SerializedName("mrp") val mrp: Double,
    @SerializedName("discount_percentage") val discountPercentage: Int = 0,
    @SerializedName("in_stock") val inStock: Boolean = true,
    @SerializedName("stock_count") val stockCount: Int = 10,
    @SerializedName("prescription_required") val prescriptionRequired: Boolean = false,
    @SerializedName("description") val description: String = "",
    @SerializedName("composition") val composition: String = "",
    @SerializedName("manufacturer") val manufacturer: String = "",
    @SerializedName("image") val imageUrl: String? = null,
    @SerializedName("tags") val tags: List<String> = emptyList(),
    @SerializedName("substitutes") val substitutes: List<MedicineSubstitute> = emptyList()
)

