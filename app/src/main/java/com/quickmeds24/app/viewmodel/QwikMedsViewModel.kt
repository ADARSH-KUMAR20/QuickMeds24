package com.quickmeds24.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.quickmeds24.app.data.api.PlacesRepository
import com.quickmeds24.app.data.local.UserPreferencesManager
import com.quickmeds24.app.data.model.*
import com.quickmeds24.app.data.repository.MedicineSearchResult
import com.quickmeds24.app.data.repository.PharmacyRepository
import com.quickmeds24.app.utils.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class QwikMedsViewModel(application: Application) : AndroidViewModel(application) {
    private val pharmacyRepository = PharmacyRepository(application.applicationContext)
    private val placesRepository = PlacesRepository()
    val userPrefs = UserPreferencesManager(application.applicationContext)

    // Current Screen Name and BackStack
    private val navigationStack = mutableListOf("splash")
    private val _currentScreen = MutableStateFlow("splash")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Location (Restored from UserDefaults or default Alpha 1)
    private val _selectedLocation = MutableStateFlow(
        DeliveryLocation(
            id = userPrefs.locationId,
            area = userPrefs.locationArea,
            landmark = "Commercial Belt & Metro Station",
            city = "Greater Noida",
            state = "Uttar Pradesh",
            pincode = "201310",
            fullAddress = userPrefs.locationAddress,
            coordinates = Coordinates(28.4735, 77.5098)
        )
    )
    val selectedLocation: StateFlow<DeliveryLocation> = _selectedLocation.asStateFlow()

    private val _availableLocations = MutableStateFlow<List<DeliveryLocation>>(emptyList())
    val availableLocations: StateFlow<List<DeliveryLocation>> = _availableLocations.asStateFlow()

    private val _placesPredictions = MutableStateFlow<List<DeliveryLocation>>(emptyList())
    val placesPredictions: StateFlow<List<DeliveryLocation>> = _placesPredictions.asStateFlow()

    // Pharmacies & Catalog (50+ Pharmacies, 500+ medicines each)
    private val _pharmacies = MutableStateFlow<List<Pharmacy>>(emptyList())
    val pharmacies: StateFlow<List<Pharmacy>> = _pharmacies.asStateFlow()

    // Fast Paginated Search State (Showing 5 initially, then paginate +5 more)
    private val _searchResults = MutableStateFlow<List<MedicineSearchResult>>(emptyList())
    val searchResults: StateFlow<List<MedicineSearchResult>> = _searchResults.asStateFlow()

    // Selected Pharmacy Filter (When user clicks a specific pharmacy like Wellness Forever to see its medicines)
    private val _selectedPharmacyFilter = MutableStateFlow<Pharmacy?>(null)
    val selectedPharmacyFilter: StateFlow<Pharmacy?> = _selectedPharmacyFilter.asStateFlow()

    // Clean Start: No prefilled search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages: StateFlow<Boolean> = _hasMorePages.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    // Selected Medicine Detail
    private val _selectedMedicineResult = MutableStateFlow<MedicineSearchResult?>(null)
    val selectedMedicineResult: StateFlow<MedicineSearchResult?> = _selectedMedicineResult.asStateFlow()

    // Cart Items: CLEAN START - NO PREFILLED ITEMS
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Targeted Pharmacy for current order
    private val _targetedPharmacy = MutableStateFlow<Pharmacy?>(null)
    val targetedPharmacy: StateFlow<Pharmacy?> = _targetedPharmacy.asStateFlow()

    // Candidate Pharmacies for Zomato-style broadcasting request animation
    private val _candidatePharmacies = MutableStateFlow<List<Pharmacy>>(emptyList())
    val candidatePharmacies: StateFlow<List<Pharmacy>> = _candidatePharmacies.asStateFlow()

    // Payment and Order
    private val _selectedPaymentMethod = MutableStateFlow(PaymentMethod.UPI_GPAY)
    val selectedPaymentMethod: StateFlow<PaymentMethod> = _selectedPaymentMethod.asStateFlow()

    // Active Order Tracking & State
    private val _activeOrderId = MutableStateFlow(userPrefs.activeOrderId.ifEmpty { "QM-8847" })
    val activeOrderId: StateFlow<String> = _activeOrderId.asStateFlow()

    private val _hasActiveOrder = MutableStateFlow(userPrefs.hasActiveOrder)
    val hasActiveOrder: StateFlow<Boolean> = _hasActiveOrder.asStateFlow()

    private val _showBlinkitConfirmation = MutableStateFlow(false)
    val showBlinkitConfirmation: StateFlow<Boolean> = _showBlinkitConfirmation.asStateFlow()

    // Delivered / Previous Orders list stored locally in UserDefaults
    private val _deliveredOrders = MutableStateFlow<List<DeliveredOrder>>(userPrefs.getDeliveredOrders())
    val deliveredOrders: StateFlow<List<DeliveredOrder>> = _deliveredOrders.asStateFlow()

    // Temporary snapshot for active order so it can be cleanly archived upon delivery
    private var pendingOrderSummary: String = ""
    private var pendingOrderItemCount: Int = 0
    private var pendingOrderTotal: Double = 0.0
    private var pendingPharmacyName: String = ""
    private var pendingPharmacySector: String = ""
    private var pendingPharmacyAddress: String = ""

    // Delivery Partner Fee / Rider Pay fixed at ₹30.00
    val riderPay: Double = 30.0

    // Auth: CLEAN START - NO HARDCODED PREFILL
    val userPhone = MutableStateFlow(userPrefs.userPhone)
    val otpDigits = MutableStateFlow(listOf("", "", "", "", "", ""))

    // Pull-to-refresh reload state
    val isRefreshing = MutableStateFlow(false)

    init {
        loadInitialData()
        setupDebouncedSearch()
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = pharmacyRepository.getPharmacyData(_selectedLocation.value)
            _pharmacies.value = data.pharmacies
            _availableLocations.value = data.availableLocations

            // Set default targeted pharmacy in Alpha 1 or Alpha 2
            val defaultStore = data.pharmacies.find { it.address.contains("Alpha 1") }
                ?: data.pharmacies.firstOrNull()
            _targetedPharmacy.value = defaultStore

            // Initial search load with page size = 5 (Starts with first 5 medicines)
            executeSearch("", page = 1)
        }
    }

    /**
     * Pull-to-refresh mechanism: re-queries nearest coordinates, updates inventory & pharmacies.
     */
    fun refreshData() {
        viewModelScope.launch(Dispatchers.IO) {
            isRefreshing.value = true
            pharmacyRepository.clearCache()
            val data = pharmacyRepository.getPharmacyData(_selectedLocation.value)
            _pharmacies.value = data.pharmacies
            executeSearch(_searchQuery.value, page = 1)
            kotlinx.coroutines.delay(600)
            isRefreshing.value = false
        }
    }

    /**
     * Coroutine Flow Debouncer: triggers search only after 250ms of user typing idle.
     * Keeps Main Thread completely free for smooth UI rendering.
     */
    private fun setupDebouncedSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(250L)
                .distinctUntilChanged()
                .flowOn(Dispatchers.Default)
                .collectLatest { query ->
                    executeSearch(query, page = 1)
                }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectPharmacy(pharmacy: Pharmacy) {
        _selectedPharmacyFilter.value = pharmacy
        _searchQuery.value = ""
        viewModelScope.launch(Dispatchers.IO) {
            executeSearch("", page = 1)
        }
        navigateTo("search")
    }

    fun clearPharmacyFilter() {
        _selectedPharmacyFilter.value = null
        _searchQuery.value = ""
        viewModelScope.launch(Dispatchers.IO) {
            executeSearch("", page = 1)
        }
    }

    /**
     * Executes paginated search with exactly 5 items per page with dynamic distance calculations.
     */
    private suspend fun executeSearch(query: String, page: Int) {
        _isSearching.value = true
        _currentPage.value = page

        val paged = pharmacyRepository.searchMedicinesPaged(
            query = query,
            page = page,
            pageSize = 5,
            userLocation = _selectedLocation.value,
            pharmacyId = _selectedPharmacyFilter.value?.id
        )

        if (page == 1) {
            _searchResults.value = paged.items
        } else {
            _searchResults.value = _searchResults.value + paged.items
        }

        _hasMorePages.value = paged.hasMore
        _isSearching.value = false
    }

    /**
     * Loads next 5 items when user reaches bottom of the search screen list.
     */
    fun loadNextPage() {
        if (!_hasMorePages.value || _isSearching.value) return
        viewModelScope.launch(Dispatchers.IO) {
            val nextPage = _currentPage.value + 1
            executeSearch(_searchQuery.value, page = nextPage)
        }
    }

    fun searchLocationPlaces(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val results = placesRepository.searchPlaces(query)
            _placesPredictions.value = results
        }
    }

    fun navigateTo(screen: String) {
        if (_currentScreen.value == screen) return

        // Push screen to navigation stack, avoiding duplicate adjacent screen entries
        if (navigationStack.isEmpty() || navigationStack.last() != screen) {
            navigationStack.add(screen)
        }
        _currentScreen.value = screen
    }

    fun handleBackPressed(onExitApp: (() -> Unit)? = null): Boolean {
        // 1. If Blinkit confirmation modal is open, back button dismisses it
        if (_showBlinkitConfirmation.value) {
            _showBlinkitConfirmation.value = false
            return true
        }

        // 2. If backstack has multiple screens, pop the top screen and show previous
        if (navigationStack.size > 1) {
            navigationStack.removeAt(navigationStack.lastIndex)
            val previousScreen = navigationStack.last()
            _currentScreen.value = previousScreen
            return true
        }

        // 3. Fallback screen-to-screen back navigation when stack has 1 screen
        return when (_currentScreen.value) {
            "tracking" -> {
                _currentScreen.value = "order_placed"
                navigationStack.clear()
                navigationStack.add("order_placed")
                true
            }
            "order_placed" -> {
                _currentScreen.value = "cart"
                navigationStack.clear()
                navigationStack.add("cart")
                true
            }
            "cart" -> {
                _currentScreen.value = "home"
                navigationStack.clear()
                navigationStack.add("home")
                true
            }
            "detail" -> {
                _currentScreen.value = "search"
                navigationStack.clear()
                navigationStack.add("search")
                true
            }
            "search" -> {
                _currentScreen.value = "home"
                navigationStack.clear()
                navigationStack.add("home")
                true
            }
            "home" -> {
                _currentScreen.value = "location"
                navigationStack.clear()
                navigationStack.add("location")
                true
            }
            "location" -> {
                _currentScreen.value = "otp"
                navigationStack.clear()
                navigationStack.add("otp")
                true
            }
            "otp" -> {
                _currentScreen.value = "get_started"
                navigationStack.clear()
                navigationStack.add("get_started")
                true
            }
            "get_started" -> {
                _currentScreen.value = "splash"
                navigationStack.clear()
                navigationStack.add("splash")
                true
            }
            "splash" -> {
                onExitApp?.invoke()
                false
            }
            else -> {
                _currentScreen.value = "home"
                navigationStack.clear()
                navigationStack.add("home")
                true
            }
        }
    }

    fun canGoBack(): Boolean = true

    fun setSelectedLocation(location: DeliveryLocation) {
        _selectedLocation.value = location
        userPrefs.locationId = location.id
        userPrefs.locationArea = location.area
        userPrefs.locationAddress = location.fullAddress

        // Recalculate dynamic distance from new coordinates for all 50+ registered pharmacies
        viewModelScope.launch(Dispatchers.IO) {
            pharmacyRepository.clearCache()
            val data = pharmacyRepository.getPharmacyData(location)
            _pharmacies.value = data.pharmacies
            executeSearch(_searchQuery.value, page = 1)
        }
    }

    fun saveUserPhone(phone: String) {
        userPhone.value = phone
        userPrefs.userPhone = phone
        userPrefs.isLoggedIn = true
    }

    fun selectMedicineForDetail(result: MedicineSearchResult) {
        _selectedMedicineResult.value = result
        navigateTo("detail")
    }

    fun addToCart(medicine: Medicine, pharmacy: Pharmacy, qty: Int = 1) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.medicine.id == medicine.id }
        if (index >= 0) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + qty)
        } else {
            current.add(CartItem(medicine, qty, pharmacy.id, pharmacy.name, pharmacy.distanceKm, pharmacy.estimatedDeliveryMinutes))
        }
        _cartItems.value = current
        _targetedPharmacy.value = pharmacy
    }

    fun updateCartQuantity(medicineId: String, delta: Int) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.medicine.id == medicineId }
        if (index >= 0) {
            val newQty = current[index].quantity + delta
            if (newQty > 0) {
                current[index] = current[index].copy(quantity = newQty)
            } else {
                current.removeAt(index)
            }
            _cartItems.value = current
        }
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
        userPrefs.paymentMethod = method.name
    }

    fun triggerBlinkitModal() {
        _showBlinkitConfirmation.value = true
    }

    fun dismissBlinkitModal() {
        _showBlinkitConfirmation.value = false
    }

    fun confirmOrderAndTrack(): String {
        _showBlinkitConfirmation.value = false
        val newId = "QM-${(1000..9999).random()}"
        _activeOrderId.value = newId
        _hasActiveOrder.value = true

        val currentCart = _cartItems.value
        val targetPharm = _targetedPharmacy.value

        // Capture snapshot before cart is cleared
        pendingPharmacyName = targetPharm?.name ?: "Wellness Forever Alpha 2"
        pendingPharmacySector = if (targetPharm?.address?.contains("Alpha 2") == true) "Alpha 2" else "Alpha 1"
        pendingPharmacyAddress = targetPharm?.address ?: "Shop 8, Main Sector Market, Alpha 2, Greater Noida"
        pendingOrderItemCount = if (currentCart.isNotEmpty()) currentCart.sumOf { it.quantity } else 2
        val itemsTotal = currentCart.sumOf { it.medicine.price * it.quantity }
        pendingOrderTotal = if (itemsTotal > 0) itemsTotal + riderPay else 145.0
        pendingOrderSummary = if (currentCart.isNotEmpty()) {
            currentCart.joinToString(", ") { "${it.medicine.name} (${it.quantity}x)" }
        } else "Telma 40mg (1x), Dolo 650mg (1x)"

        // Persist order details locally in UserDefaults (SharedPreferences)
        userPrefs.activeOrderId = newId
        userPrefs.hasActiveOrder = true
        userPrefs.orderPharmacyName = pendingPharmacyName
        userPrefs.orderPharmacySector = pendingPharmacySector
        userPrefs.deliveryOtp = "4821"

        // Candidate pharmacies in Alpha 1 and Alpha 2 for broadcast
        val candidates = _pharmacies.value.filter { p ->
            p.address.contains("Alpha 1", ignoreCase = true) || p.address.contains("Alpha 2", ignoreCase = true)
        }.take(3)
        _candidatePharmacies.value = candidates.ifEmpty { _pharmacies.value.take(3) }

        // Clear cart now that order is placed
        _cartItems.value = emptyList()

        navigateTo("order_placed")
        return newId
    }

    /**
     * Completes and archives delivered order, saving to Previous Orders and removing live track section.
     */
    fun completeOrderAndArchive() {
        val completedOrder = DeliveredOrder(
            orderId = _activeOrderId.value.ifEmpty { "QM-8847" },
            pharmacyName = pendingPharmacyName.ifEmpty { userPrefs.orderPharmacyName },
            pharmacySector = pendingPharmacySector.ifEmpty { userPrefs.orderPharmacySector },
            pharmacyAddress = pendingPharmacyAddress.ifEmpty { "Main Sector Market, Greater Noida" },
            itemsSummary = pendingOrderSummary.ifEmpty { "Medicines delivered successfully" },
            itemCount = if (pendingOrderItemCount > 0) pendingOrderItemCount else 2,
            totalAmount = if (pendingOrderTotal > 0) pendingOrderTotal else 145.0,
            riderPay = riderPay,
            deliveredTimeIst = "Delivered at ${TimeUtils.formatCurrentIstTime()} IST",
            deliveryOtp = "4821",
            paymentMethod = _selectedPaymentMethod.value.displayName
        )
        userPrefs.saveDeliveredOrder(completedOrder)
        _deliveredOrders.value = userPrefs.getDeliveredOrders()

        _hasActiveOrder.value = false
        userPrefs.hasActiveOrder = false
        userPrefs.clearActiveOrder()
        navigateTo("home")
    }

    fun clearDeliveredOrders() {
        userPrefs.clearDeliveredOrders()
        _deliveredOrders.value = emptyList()
    }
}
