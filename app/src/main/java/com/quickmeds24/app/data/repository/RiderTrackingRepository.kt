package com.quickmeds24.app.data.repository

import com.quickmeds24.app.data.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * RiderTrackingRepository
 * Simulates high-precision, continuous Rapido/Uber-style GPS telemetry:
 * - 20+ fine-grained road waypoints from Alpha 2 (Rider Origin) -> Alpha 2 Pharmacy (Pickup) -> Alpha 1 (Customer).
 * - Smooth 1.2s cadence simulating continuous vehicle movement.
 * - Dynamic ETA countdown timer and real-time distance remaining.
 * - Automatic triggers for "Order Picked" and "Driver Arrived" Push Notifications.
 */
class RiderTrackingRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val _telemetryFlow = MutableStateFlow<TrackingTelemetry?>(null)
    val telemetryFlow: StateFlow<TrackingTelemetry?> = _telemetryFlow.asStateFlow()

    private val _pushNotificationFlow = MutableStateFlow<InAppPushNotification?>(null)
    val pushNotificationFlow: StateFlow<InAppPushNotification?> = _pushNotificationFlow.asStateFlow()

    private var activeJob: Job? = null
    private var isTrackingActive: Boolean = false
    private var isUserOnScreen: Boolean = true
    private var customBackgroundIntervalSec: Long = 10L
    private var currentWaypointIndex: Int = 0

    private var hasPushedPickedUp = false
    private var hasPushedArrived = false

    fun isTrackingActive(): Boolean = isTrackingActive

    // Rapido-Style Continuous Road Path: Alpha 2 North -> Wellness Forever (Alpha 2) -> Roundabout -> Flat 402, Alpha 1
    private val waypoints = listOf(
        Waypoint(28.4830, 77.5190, TrackingStatus.RIDER_ASSIGNED, "Rider Rohit assigned in Alpha 2. Heading to store", 1, 12, 2.4, 30, false),
        Waypoint(28.4824, 77.5185, TrackingStatus.HEADING_TO_PHARMACY, "Rider moving along Alpha 2 Sector Road", 1, 11, 2.3, 32, false),
        Waypoint(28.4818, 77.5180, TrackingStatus.HEADING_TO_PHARMACY, "Passing Pocket F market complex", 1, 11, 2.2, 34, false),
        Waypoint(28.4812, 77.5174, TrackingStatus.HEADING_TO_PHARMACY, "Crossing Alpha 2 central avenue", 1, 10, 2.0, 31, false),
        Waypoint(28.4805, 77.5168, TrackingStatus.HEADING_TO_PHARMACY, "Approaching Main Sector Market, Alpha 2", 1, 10, 1.9, 28, false),
        Waypoint(28.4798, 77.5162, TrackingStatus.HEADING_TO_PHARMACY, "Turning into Wellness Forever commercial lane", 1, 9, 1.8, 25, false),
        Waypoint(28.4790, 77.5156, TrackingStatus.HEADING_TO_PHARMACY, "Outside Shop 8, Main Sector Market", 1, 9, 1.6, 20, false),
        Waypoint(28.4782, 77.5150, TrackingStatus.HEADING_TO_PHARMACY, "Slowing down at Wellness Forever entrance", 1, 8, 1.5, 14, false),
        Waypoint(28.4775, 77.5145, TrackingStatus.ARRIVED_AT_PHARMACY, "Arrived at Alpha 2 Pharmacy. Collecting medicines", 1, 8, 1.5, 0, false),
        // Waypoint 9: Medicine Picked Up!
        Waypoint(28.4775, 77.5145, TrackingStatus.MEDICINES_PICKED_UP, "Medicines verified & picked up from Alpha 2! OTP: 4821", 2, 7, 1.4, 20, true),
        Waypoint(28.4770, 77.5140, TrackingStatus.ON_THE_WAY, "Rider Rohit departing Alpha 2 market", 2, 7, 1.3, 32, true),
        Waypoint(28.4765, 77.5133, TrackingStatus.ON_THE_WAY, "Accelerating along connecting road to Alpha 1", 2, 6, 1.2, 36, true),
        Waypoint(28.4760, 77.5126, TrackingStatus.ON_THE_WAY, "Approaching Alpha Sector Roundabout", 2, 5, 1.1, 30, true),
        Waypoint(28.4755, 77.5120, TrackingStatus.ON_THE_WAY, "Crossing roundabout into Sector Alpha 1", 2, 5, 0.9, 28, true),
        Waypoint(28.4750, 77.5114, TrackingStatus.ON_THE_WAY, "Entered Alpha 1 Main Gate avenue", 2, 4, 0.8, 31, true),
        Waypoint(28.4746, 77.5110, TrackingStatus.ON_THE_WAY, "Passing Alpha 1 Commercial Belt", 2, 3, 0.6, 27, true),
        Waypoint(28.4742, 77.5106, TrackingStatus.NEARBY, "Turning into Golf Vista residential driveway", 2, 2, 0.4, 22, true),
        Waypoint(28.4739, 77.5102, TrackingStatus.NEARBY, "Passing Tower B main security barrier", 2, 1, 0.2, 16, true),
        Waypoint(28.4737, 77.5100, TrackingStatus.NEARBY, "Rider Rohit reached Tower B lobby", 2, 1, 0.1, 8, true),
        // Waypoint 19: Driver Arrived!
        Waypoint(28.4735, 77.5098, TrackingStatus.DELIVERED, "Driver Arrived! Please share Delivery OTP: 4821", 3, 0, 0.0, 0, true)
    )

    private val riderInfo = Rider(
        id = "rider-rohit-07",
        name = "Rohit Kumar",
        phone = "+91 98765 12049",
        rating = 4.9,
        vehicleNumber = "UP 16 BY 4821",
        avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde"
    )

    private val pharmacyInfo = TargetedPharmacy(
        id = "pharm-alpha2-wellness",
        name = "Wellness Forever Alpha 2",
        branch = "Main Sector Market, Alpha 2",
        distanceKm = 1.8,
        address = "Shop 8, Main Sector Market, Alpha 2, Greater Noida - 201310",
        coordinates = Coordinates(28.4775, 77.5145)
    )

    fun startTracking(orderId: String = "QM-8847", forceReset: Boolean = false) {
        if (orderId.isBlank()) return
        isTrackingActive = true
        val lastOrderId = _telemetryFlow.value?.orderId
        // Always reset simulation cleanly when a new order ID is placed or previous order finished
        if (forceReset || lastOrderId != orderId || currentWaypointIndex >= waypoints.size - 1) {
            currentWaypointIndex = 0
            hasPushedPickedUp = false
            hasPushedArrived = false
            _pushNotificationFlow.value = null
        }
        restartPollingCadence(orderId)
    }

    fun setScreenVisibility(isOnScreen: Boolean, orderId: String = "QM-8847") {
        this.isUserOnScreen = isOnScreen
        if (isTrackingActive && orderId.isNotBlank()) {
            restartPollingCadence(orderId)
        }
    }

    fun setBackgroundThrottledSeconds(seconds: Long, orderId: String = "QM-8847") {
        this.customBackgroundIntervalSec = seconds
        if (!isUserOnScreen && isTrackingActive && orderId.isNotBlank()) {
            restartPollingCadence(orderId)
        }
    }

    fun triggerManualRefresh(orderId: String = "QM-8847") {
        this.isUserOnScreen = true
        if (isTrackingActive && orderId.isNotBlank()) {
            restartPollingCadence(orderId)
        }
    }

    fun resetSimulation(orderId: String = "QM-8847") {
        if (!isTrackingActive) return
        currentWaypointIndex = 0
        hasPushedPickedUp = false
        hasPushedArrived = false
        _pushNotificationFlow.value = null
        restartPollingCadence(orderId)
    }

    fun dismissPushNotification() {
        _pushNotificationFlow.value = null
    }

    private fun restartPollingCadence(orderId: String) {
        activeJob?.cancel()
        if (!isTrackingActive || orderId.isBlank()) return
        activeJob = CoroutineScope(ioDispatcher).launch {
            while (isActive && isTrackingActive) {
                // Continuous Rapido mode: 1200ms per micro-waypoint; Throttled: custom seconds
                val interval = if (isUserOnScreen) 1_300L else (customBackgroundIntervalSec * 1_000L)
                emitNextTelemetry(orderId, interval / 1000)
                delay(interval)
            }
        }
    }

    private fun emitNextTelemetry(orderId: String, currentFreqSec: Long) {
        val wp = waypoints[currentWaypointIndex]
        val allCoordinates = waypoints.map { Coordinates(it.lat, it.lng) }

        // Trigger in-app Push Notifications based on delivery milestone
        if (wp.pickedUp && !hasPushedPickedUp && currentWaypointIndex >= 9) {
            hasPushedPickedUp = true
            _pushNotificationFlow.value = InAppPushNotification(
                id = "push-picked-${System.currentTimeMillis()}",
                title = "Order Picked Up! 🛵",
                message = "Rider Rohit collected your medicines from Wellness Forever Alpha 2. On the way to Flat 402, Alpha 1!",
                type = PushType.ORDER_PICKED
            )
        } else if (wp.status == TrackingStatus.DELIVERED && !hasPushedArrived) {
            hasPushedArrived = true
            _pushNotificationFlow.value = InAppPushNotification(
                id = "push-arrived-${System.currentTimeMillis()}",
                title = "Driver Arrived! 📍",
                message = "Rider Rohit has arrived outside Flat 402, Tower B, Alpha 1. Please share Delivery OTP: 4821 with rider.",
                type = PushType.DRIVER_ARRIVED
            )
        }

        _telemetryFlow.value = TrackingTelemetry(
            orderId = orderId,
            status = wp.status,
            statusText = wp.statusText,
            statusStepIndex = wp.step,
            rider = riderInfo,
            targetedPharmacy = pharmacyInfo,
            currentLocation = Coordinates(wp.lat, wp.lng),
            routeCoordinates = allCoordinates,
            isMedicinePickedUp = wp.pickedUp,
            pickedUpTime = if (wp.pickedUp) "11:28 AM" else null,
            deliveryOtp = "4821",
            etaMinutes = wp.eta,
            distanceRemainingKm = wp.dist,
            lastUpdatedTimestamp = System.currentTimeMillis(),
            updateFrequencySeconds = currentFreqSec.toInt(),
            isContinuousActive = isUserOnScreen,
            speedKmh = wp.speedKmh,
            totalSecondsRemaining = wp.eta * 60
        )

        // Advance to next micro-waypoint along the Rapido path
        if (currentWaypointIndex < waypoints.size - 1) {
            currentWaypointIndex++
        }
    }

    fun stopTracking() {
        isTrackingActive = false
        activeJob?.cancel()
        activeJob = null
        _telemetryFlow.value = null
        _pushNotificationFlow.value = null
    }

    private data class Waypoint(
        val lat: Double,
        val lng: Double,
        val status: TrackingStatus,
        val statusText: String,
        val step: Int,
        val eta: Int,
        val dist: Double,
        val speedKmh: Int,
        val pickedUp: Boolean
    )
}
