package com.quickmeds24.app.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quickmeds24.app.data.model.DeliveredOrder

/**
 * UserPreferencesManager
 * Local persistent storage equivalent to iOS UserDefaults using Android SharedPreferences.
 * Stores user login details, selected location, active order state, delivered past orders, and preferences locally.
 */
class UserPreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "qwikmeds_user_defaults"
        private const val KEY_PHONE = "user_phone"
        private const val KEY_NAME = "user_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_LOCATION_ID = "location_id"
        private const val KEY_LOCATION_AREA = "location_area"
        private const val KEY_LOCATION_ADDRESS = "location_address"
        private const val KEY_ACTIVE_ORDER_ID = "active_order_id"
        private const val KEY_HAS_ACTIVE_ORDER = "has_active_order"
        private const val KEY_ORDER_PHARMACY_NAME = "order_pharmacy_name"
        private const val KEY_ORDER_PHARMACY_SECTOR = "order_pharmacy_sector"
        private const val KEY_DELIVERY_OTP = "delivery_otp"
        private const val KEY_PAYMENT_METHOD = "payment_method"
        private const val KEY_DELIVERED_ORDERS = "delivered_orders_json"
    }

    var userPhone: String
        get() = prefs.getString(KEY_PHONE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_PHONE, value).apply()

    var userName: String
        get() = prefs.getString(KEY_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_NAME, value).apply()

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var locationId: String
        get() = prefs.getString(KEY_LOCATION_ID, "loc-alpha-1") ?: "loc-alpha-1"
        set(value) = prefs.edit().putString(KEY_LOCATION_ID, value).apply()

    var locationArea: String
        get() = prefs.getString(KEY_LOCATION_AREA, "Alpha 1") ?: "Alpha 1"
        set(value) = prefs.edit().putString(KEY_LOCATION_AREA, value).apply()

    var locationAddress: String
        get() = prefs.getString(KEY_LOCATION_ADDRESS, "Flat 402, Tower B, Golf Vista, Alpha 1, Greater Noida - 201310")
            ?: "Flat 402, Tower B, Golf Vista, Alpha 1, Greater Noida - 201310"
        set(value) = prefs.edit().putString(KEY_LOCATION_ADDRESS, value).apply()

    var activeOrderId: String
        get() = prefs.getString(KEY_ACTIVE_ORDER_ID, "") ?: ""
        set(value) = prefs.edit().putString(KEY_ACTIVE_ORDER_ID, value).apply()

    var hasActiveOrder: Boolean
        get() = prefs.getBoolean(KEY_HAS_ACTIVE_ORDER, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_ACTIVE_ORDER, value).apply()

    var orderPharmacyName: String
        get() = prefs.getString(KEY_ORDER_PHARMACY_NAME, "Sharma Medical Store") ?: "Sharma Medical Store"
        set(value) = prefs.edit().putString(KEY_ORDER_PHARMACY_NAME, value).apply()

    var orderPharmacySector: String
        get() = prefs.getString(KEY_ORDER_PHARMACY_SECTOR, "Alpha 2") ?: "Alpha 2"
        set(value) = prefs.edit().putString(KEY_ORDER_PHARMACY_SECTOR, value).apply()

    var deliveryOtp: String
        get() = prefs.getString(KEY_DELIVERY_OTP, "4821") ?: "4821"
        set(value) = prefs.edit().putString(KEY_DELIVERY_OTP, value).apply()

    var paymentMethod: String
        get() = prefs.getString(KEY_PAYMENT_METHOD, "UPI_GPAY") ?: "UPI_GPAY"
        set(value) = prefs.edit().putString(KEY_PAYMENT_METHOD, value).apply()

    fun clearActiveOrder() {
        prefs.edit()
            .putBoolean(KEY_HAS_ACTIVE_ORDER, false)
            .putString(KEY_ACTIVE_ORDER_ID, "")
            .apply()
    }

    /**
     * Retrieves the list of past delivered orders from local storage.
     * Seeded with realistic past delivered orders if empty so the user can immediately verify.
     */
    fun getDeliveredOrders(): List<DeliveredOrder> {
        val json = prefs.getString(KEY_DELIVERED_ORDERS, null)
        if (json.isNullOrBlank()) {
            return listOf(
                DeliveredOrder(
                    orderId = "QM-7291",
                    pharmacyName = "Sharma Medical Store",
                    pharmacySector = "Alpha 1",
                    pharmacyAddress = "Shop 14, Commercial Belt, Alpha 1, Greater Noida",
                    itemsSummary = "Telma 40mg (1x), Paracetamol 650mg (1x)",
                    itemCount = 2,
                    totalAmount = 145.0,
                    riderPay = 30.0,
                    deliveredTimeIst = "Delivered at 08:35 PM IST",
                    deliveryOtp = "4821",
                    paymentMethod = "Google Pay UPI"
                ),
                DeliveredOrder(
                    orderId = "QM-6184",
                    pharmacyName = "Wellness Forever Alpha 2",
                    pharmacySector = "Alpha 2",
                    pharmacyAddress = "Shop 8, Main Sector Market, Alpha 2, Greater Noida",
                    itemsSummary = "Dolo 650mg (1x), Cetirizine 10mg (1x)",
                    itemCount = 2,
                    totalAmount = 98.0,
                    riderPay = 30.0,
                    deliveredTimeIst = "Delivered at 04:12 PM IST",
                    deliveryOtp = "3190",
                    paymentMethod = "PhonePe UPI"
                )
            )
        }
        return try {
            val type = object : TypeToken<List<DeliveredOrder>>() {}.type
            gson.fromJson<List<DeliveredOrder>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Prepends a newly delivered order to local storage and persists it.
     */
    fun saveDeliveredOrder(order: DeliveredOrder) {
        val current = getDeliveredOrders().toMutableList()
        // Avoid duplicate by orderId
        current.removeAll { it.orderId == order.orderId }
        current.add(0, order)
        val json = gson.toJson(current)
        prefs.edit().putString(KEY_DELIVERED_ORDERS, json).apply()
    }

    fun clearDeliveredOrders() {
        prefs.edit().remove(KEY_DELIVERED_ORDERS).apply()
    }
}

