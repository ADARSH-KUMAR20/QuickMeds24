package com.quickmeds24.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.quickmeds24.app.ui.components.BlinkitConfirmationDialog
import com.quickmeds24.app.ui.components.PushNotificationBanner
import com.quickmeds24.app.ui.screens.*
import com.quickmeds24.app.viewmodel.QwikMedsViewModel
import com.quickmeds24.app.viewmodel.RiderTrackingViewModel

@Composable
fun QwikMedsApp(
    appViewModel: QwikMedsViewModel,
    trackingViewModel: RiderTrackingViewModel
) {
    val currentScreen by appViewModel.currentScreen.collectAsState()
    val selectedLocation by appViewModel.selectedLocation.collectAsState()
    val availableLocations by appViewModel.availableLocations.collectAsState()
    val placesPredictions by appViewModel.placesPredictions.collectAsState()
    val pharmacies by appViewModel.pharmacies.collectAsState()
    val candidatePharmacies by appViewModel.candidatePharmacies.collectAsState()
    val searchResults by appViewModel.searchResults.collectAsState()
    val searchQuery by appViewModel.searchQuery.collectAsState()
    val isSearching by appViewModel.isSearching.collectAsState()
    val hasMorePages by appViewModel.hasMorePages.collectAsState()
    val selectedMedicineResult by appViewModel.selectedMedicineResult.collectAsState()
    val cartItems by appViewModel.cartItems.collectAsState()
    val targetedPharmacy by appViewModel.targetedPharmacy.collectAsState()
    val selectedPaymentMethod by appViewModel.selectedPaymentMethod.collectAsState()
    val activeOrderId by appViewModel.activeOrderId.collectAsState()
    val hasActiveOrder by appViewModel.hasActiveOrder.collectAsState()
    val showBlinkitConfirmation by appViewModel.showBlinkitConfirmation.collectAsState()
    val userPhone by appViewModel.userPhone.collectAsState()
    val deliveredOrders by appViewModel.deliveredOrders.collectAsState()
    val isRefreshing by appViewModel.isRefreshing.collectAsState()
    val selectedPharmacyFilter by appViewModel.selectedPharmacyFilter.collectAsState()

    val context = LocalContext.current
    val telemetry by trackingViewModel.telemetry.collectAsState()
    val pushNotification by trackingViewModel.pushNotification.collectAsState()

    // Trigger system notification when rider milestones occur
    LaunchedEffect(pushNotification?.id) {
        pushNotification?.let {
            com.quickmeds24.app.utils.NotificationHelper.showSystemNotification(context, it)
        }
    }

    // Handle Android system back button naturally across all screen flows (identical to in-app back)
    BackHandler(enabled = true) {
        appViewModel.handleBackPressed()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            "splash" -> SplashScreen(
                onGetStarted = { appViewModel.navigateTo("get_started") }
            )

            "get_started" -> GetStartedScreen(
                onContinueWithPhone = { appViewModel.navigateTo("otp") },
                onContinueWithEmail = { appViewModel.navigateTo("otp") }
            )

            "otp" -> OtpScreen(
                phoneNumber = userPhone,
                onVerifySuccess = { appViewModel.navigateTo("location") },
                onBack = { appViewModel.handleBackPressed() }
            )

            "location" -> LocationScreen(
                selectedLocation = selectedLocation,
                availableLocations = availableLocations,
                placesPredictions = placesPredictions,
                onSearchPlaces = { appViewModel.searchLocationPlaces(it) },
                onSelectLocation = { appViewModel.setSelectedLocation(it) },
                onConfirmLocation = { appViewModel.navigateTo("home") },
                onBack = { appViewModel.handleBackPressed() }
            )

            "home" -> HomeScreen(
                selectedLocation = selectedLocation,
                cartCount = cartItems.sumOf { it.quantity },
                cartItems = cartItems,
                pharmacies = pharmacies,
                deliveredOrders = deliveredOrders,
                isRefreshing = isRefreshing,
                onRefresh = { appViewModel.refreshData() },
                hasActiveOrder = hasActiveOrder,
                activeOrderId = activeOrderId,
                telemetry = telemetry,
                onTrackLiveClick = {
                    trackingViewModel.startTracking(activeOrderId)
                    appViewModel.navigateTo("tracking")
                },
                onOrderCompleted = {
                    trackingViewModel.stopTracking()
                    appViewModel.completeOrderAndArchive()
                },
                onSearchClick = { appViewModel.navigateTo("search") },
                onLocationClick = { appViewModel.navigateTo("location") },
                onCartClick = { appViewModel.navigateTo("cart") },
                onPharmacyClick = { pharmacy ->
                    appViewModel.selectPharmacy(pharmacy)
                }
            )

            "search" -> SearchScreen(
                query = searchQuery,
                onQueryChange = { appViewModel.setSearchQuery(it) },
                searchResults = searchResults,
                cartItems = cartItems,
                selectedPharmacy = selectedPharmacyFilter,
                onClearPharmacyFilter = { appViewModel.clearPharmacyFilter() },
                isSearching = isSearching,
                hasMore = hasMorePages,
                isRefreshing = isRefreshing,
                onRefresh = { appViewModel.refreshData() },
                onLoadMore = { appViewModel.loadNextPage() },
                onMedicineClick = { appViewModel.selectMedicineForDetail(it) },
                onAddToCart = { med, pharm -> appViewModel.addToCart(med, pharm) },
                onUpdateQuantity = { id, delta -> appViewModel.updateCartQuantity(id, delta) },
                onViewCart = { appViewModel.navigateTo("cart") },
                onBack = { appViewModel.handleBackPressed() }
            )

            "detail" -> {
                selectedMedicineResult?.let { result ->
                    MedicineDetailScreen(
                        medicine = result.medicine,
                        pharmacy = result.pharmacy,
                        cartCount = cartItems.sumOf { it.quantity },
                        onAddToCart = { med, pharm, qty ->
                            appViewModel.addToCart(med, pharm, qty)
                        },
                        onCartClick = { appViewModel.navigateTo("cart") },
                        onBack = { appViewModel.handleBackPressed() }
                    )
                } ?: run {
                    appViewModel.navigateTo("search")
                }
            }

            "cart" -> CartScreen(
                cartItems = cartItems,
                targetedPharmacy = targetedPharmacy,
                selectedPaymentMethod = selectedPaymentMethod,
                orderId = activeOrderId,
                onPaymentMethodSelected = { appViewModel.selectPaymentMethod(it) },
                onUpdateQuantity = { id, delta -> appViewModel.updateCartQuantity(id, delta) },
                onProceedToPay = { appViewModel.triggerBlinkitModal() },
                onBack = { appViewModel.handleBackPressed() }
            )

            "order_placed" -> OrderPlacedScreen(
                orderId = activeOrderId,
                candidatePharmacies = candidatePharmacies,
                onTrackLiveOrder = {
                    trackingViewModel.startTracking(activeOrderId)
                    appViewModel.navigateTo("tracking")
                },
                onBack = { appViewModel.handleBackPressed() }
            )

            "tracking" -> LiveTrackingScreen(
                telemetry = telemetry,
                destinationArea = selectedLocation.area,
                onMapClick = { trackingViewModel.onMapClicked(activeOrderId) },
                onRefreshClick = { trackingViewModel.onMapClicked(activeOrderId) },
                onResetSimulation = { trackingViewModel.resetSimulation(activeOrderId) },
                onOrderCompleted = {
                    trackingViewModel.stopTracking()
                    appViewModel.completeOrderAndArchive()
                },
                onBack = { appViewModel.handleBackPressed() }
            )

            else -> HomeScreen(
                selectedLocation = selectedLocation,
                cartCount = cartItems.sumOf { it.quantity },
                cartItems = cartItems,
                pharmacies = pharmacies,
                deliveredOrders = deliveredOrders,
                isRefreshing = isRefreshing,
                onRefresh = { appViewModel.refreshData() },
                hasActiveOrder = hasActiveOrder,
                activeOrderId = activeOrderId,
                telemetry = telemetry,
                onTrackLiveClick = {
                    trackingViewModel.startTracking(activeOrderId)
                    appViewModel.navigateTo("tracking")
                },
                onOrderCompleted = {
                    trackingViewModel.stopTracking()
                    appViewModel.completeOrderAndArchive()
                },
                onSearchClick = { appViewModel.navigateTo("search") },
                onLocationClick = { appViewModel.navigateTo("location") },
                onCartClick = { appViewModel.navigateTo("cart") },
                onPharmacyClick = { pharmacy ->
                    appViewModel.selectPharmacy(pharmacy)
                }
            )
        }

        // Real Blinkit-style delivery confirmation popup
        if (showBlinkitConfirmation) {
            BlinkitConfirmationDialog(
                orderId = activeOrderId,
                store = targetedPharmacy,
                itemsCount = cartItems.sumOf { it.quantity },
                totalAmount = cartItems.fold(0.0) { acc, item -> acc + (item.medicine.price * item.quantity) } + appViewModel.riderPay,
                paymentMethod = selectedPaymentMethod.displayName,
                onConfirm = {
                    val newId = appViewModel.confirmOrderAndTrack()
                    trackingViewModel.startTracking(newId, forceReset = true)
                },
                onDismiss = { appViewModel.dismissBlinkitModal() }
            )
        }

        // Heads-Up In-App Push Notification Banner (Order Picked, Driver Arrived)
        PushNotificationBanner(
            notification = pushNotification,
            onDismiss = { trackingViewModel.dismissPushNotification() },
            onClickTrack = {
                trackingViewModel.dismissPushNotification()
                trackingViewModel.startTracking(activeOrderId)
                appViewModel.navigateTo("tracking")
            }
        )
    }
}
