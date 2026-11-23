package com.quickmeds24.app.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickmeds24.app.data.model.TrackingTelemetry
import com.quickmeds24.app.data.repository.RiderTrackingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * RiderTrackingViewModel
 * Implements Android Lifecycle Observer to dynamically modulate WebSocket & GPS polling:
 * - On Resume (User on screen): 5s continuous high-frequency stream.
 * - On Pause / Stop (Backgrounded): throttles to 30s or 60s to prevent battery drain and UI thread lag.
 */
class RiderTrackingViewModel(
    private val repository: RiderTrackingRepository = RiderTrackingRepository()
) : ViewModel(), DefaultLifecycleObserver {

    val telemetry: StateFlow<TrackingTelemetry?> = repository.telemetryFlow
    val pushNotification: StateFlow<com.quickmeds24.app.data.model.InAppPushNotification?> = repository.pushNotificationFlow

    fun dismissPushNotification() {
        repository.dismissPushNotification()
    }

    fun startTracking(orderId: String, forceReset: Boolean = false) {
        if (orderId.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.setScreenVisibility(true, orderId)
            repository.startTracking(orderId, forceReset)
        }
    }

    fun stopTracking() {
        repository.stopTracking()
    }

    fun isTrackingActive(): Boolean = repository.isTrackingActive()

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        // User on screen: High-frequency updates ONLY if tracking is actively engaged
        if (repository.isTrackingActive()) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.setScreenVisibility(true)
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        // User off screen: Throttles only if tracking is active
        if (repository.isTrackingActive()) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.setScreenVisibility(false)
            }
        }
    }

    fun onMapClicked(orderId: String) {
        // Force immediate location update and activate continuous mode
        viewModelScope.launch(Dispatchers.IO) {
            repository.triggerManualRefresh(orderId)
        }
    }

    fun setBackgroundCadence(seconds: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setBackgroundThrottledSeconds(seconds)
        }
    }

    fun resetSimulation(orderId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.resetSimulation(orderId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopTracking()
    }
}

