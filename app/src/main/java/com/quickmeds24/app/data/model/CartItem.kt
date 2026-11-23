package com.quickmeds24.app.data.model

data class CartItem(
    val medicine: Medicine,
    val quantity: Int,
    val pharmacyId: String,
    val pharmacyName: String,
    val pharmacyDistance: Double,
    val deliveryMinutes: Int
)

