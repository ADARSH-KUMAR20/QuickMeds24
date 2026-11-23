package com.quickmeds24.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * DeliveredOrder
 * Represents a successfully delivered order archived in local persistent storage.
 */
data class DeliveredOrder(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("pharmacy_name") val pharmacyName: String,
    @SerializedName("pharmacy_sector") val pharmacySector: String,
    @SerializedName("pharmacy_address") val pharmacyAddress: String = "",
    @SerializedName("items_summary") val itemsSummary: String,
    @SerializedName("item_count") val itemCount: Int,
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("rider_pay") val riderPay: Double = 30.0,
    @SerializedName("delivered_time_ist") val deliveredTimeIst: String,
    @SerializedName("delivery_otp") val deliveryOtp: String = "4821",
    @SerializedName("payment_method") val paymentMethod: String = "UPI (Google Pay)"
)

