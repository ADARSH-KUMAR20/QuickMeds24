package com.quickmeds24.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.quickmeds24.app.theme.QuickMedsTheme
import com.quickmeds24.app.viewmodel.QwikMedsViewModel
import com.quickmeds24.app.viewmodel.RiderTrackingViewModel

class MainActivity : ComponentActivity() {

    private val appViewModel: QwikMedsViewModel by viewModels()
    private val trackingViewModel: RiderTrackingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Attach lifecycle observer for automatic 5s active vs 30s-1min background throttling
        lifecycle.addObserver(trackingViewModel)

        // Only start tracking rider if an active order is actively ongoing (prevent infinite background loop when not ordered)
        if (appViewModel.hasActiveOrder.value && appViewModel.activeOrderId.value.isNotEmpty()) {
            trackingViewModel.startTracking(appViewModel.activeOrderId.value)
        }

        // Register system OS back button/gesture callback directly on Activity dispatcher
        // Ensures Android OS back button behaves 100% identical to the app's in-app back buttons
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val handled = appViewModel.handleBackPressed(
                    onExitApp = {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                )
                if (!handled) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        setContent {
            QuickMedsTheme {
                QwikMedsApp(
                    appViewModel = appViewModel,
                    trackingViewModel = trackingViewModel
                )
            }
        }
    }
}
