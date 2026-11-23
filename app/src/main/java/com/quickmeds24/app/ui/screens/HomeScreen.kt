package com.quickmeds24.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmeds24.app.data.model.*
import com.quickmeds24.app.theme.*
import com.quickmeds24.app.ui.components.PreviousOrdersDialog
import com.quickmeds24.app.ui.components.QwikMedsHeader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectedLocation: DeliveryLocation,
    cartCount: Int,
    cartItems: List<CartItem> = emptyList(),
    pharmacies: List<Pharmacy>,
    deliveredOrders: List<DeliveredOrder> = emptyList(),
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    hasActiveOrder: Boolean = false,
    activeOrderId: String = "",
    telemetry: TrackingTelemetry? = null,
    onTrackLiveClick: () -> Unit = {},
    onOrderCompleted: () -> Unit = {},
    onSearchClick: () -> Unit,
    onLocationClick: () -> Unit,
    onCartClick: () -> Unit,
    onPharmacyClick: (Pharmacy) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPreviousOrdersDialog by remember { mutableStateOf(false) }
    var displayedPharmacyCount by remember { mutableStateOf(5) }
    var isLoadingFarPharmacies by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val totalCartAmount = cartItems.fold(0.0) { acc, i -> acc + (i.medicine.price * i.quantity) }

    // Reset visible count when pharmacies change (e.g. location switch)
    LaunchedEffect(pharmacies) {
        displayedPharmacyCount = 5
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Header matching Image 5 with exact QwikMeds clover logo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QwikMedsHeader(
                    emblemSize = 28.dp,
                    textSize = 20.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Previous Orders Icon Button (Placed right next to Cart Icon)
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Slate50)
                            .clickable { showPreviousOrdersDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = "Previous Orders",
                            tint = Slate800,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Cart icon with badge (non-clipped, prominent count number)
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { onCartClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Slate50),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = "Cart",
                                tint = Slate800,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        if (cartCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = (-2).dp)
                                    .defaultMinSize(minWidth = 22.dp, minHeight = 22.dp)
                                    .clip(CircleShape)
                                    .background(AccentRed)
                                    .border(1.5.dp, Color.White, CircleShape)
                                    .padding(horizontal = 4.dp, vertical = 1.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cartCount.toString(),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }

            // Location Dropdown Bar
            Row(
                modifier = Modifier
                    .clickable { onLocationClick() }
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${selectedLocation.area}, ${selectedLocation.city}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate800
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Slate500,
                    modifier = Modifier.size(16.dp)
                )
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (cartCount > 0) 80.dp else 0.dp),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                // Live Order Tracking Card: automatically removed once order is delivered
                val isDelivered = telemetry?.status == TrackingStatus.DELIVERED
                if (hasActiveOrder && !isDelivered) {
                    item {
                        val currentEta = telemetry?.etaMinutes ?: 12
                        val currentDistance = telemetry?.distanceRemainingKm ?: 1.8
                        val istDeliveryTime = com.quickmeds24.app.utils.TimeUtils.formatIstDeliveryTime(currentEta)
                        val statusDesc = when {
                            telemetry?.isMedicinePickedUp == true -> "Rx Picked from Alpha 2! Delivering to Alpha 1"
                            else -> "Heading in Alpha 2 to pick up Rx"
                        }

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = PrimaryBlueLight),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(96.dp)
                                .border(1.5.dp, PrimaryBlue, RoundedCornerShape(20.dp))
                                .clickable { onTrackLiveClick() }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(PrimaryBlue),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.TwoWheeler,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(AccentGreen)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "LIVE ORDER #$activeOrderId",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = PrimaryBlue,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        val distFormatted = String.format(java.util.Locale.US, "%.1f", currentDistance)
                                        Text(
                                            text = "Delivery by $istDeliveryTime ($distFormatted km)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate900,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = statusDesc,
                                            fontSize = 11.sp,
                                            color = Slate600,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Button(
                                    onClick = onTrackLiveClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text(text = "Track ➔", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Search Bar matching Image 5
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Slate200, RoundedCornerShape(18.dp))
                            .clickable { onSearchClick() }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Slate400,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Search medicines, brands...",
                                fontSize = 13.sp,
                                color = Slate400,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // 15 Minutes Promo Banner with Scooter Rider matching Image 5
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryBlueLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Get Your Medicines",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate700
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "in 15 Minutes",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = PrimaryBlue
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = AccentAmber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Text(
                                    text = "From nearby pharmacies",
                                    fontSize = 11.sp,
                                    color = Slate500
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TwoWheeler,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(38.dp)
                                )
                            }
                        }
                    }
                }

                // Nearby Pharmacies Section Header matching Image 5
                item {
                    val visibleCount = pharmacies.take(displayedPharmacyCount).size
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Nearby Pharmacies ($visibleCount of ${pharmacies.size})",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text(
                                text = "Sorted by nearest GPS coordinates",
                                fontSize = 11.sp,
                                color = Slate500
                            )
                        }
                        Text(
                            text = "See All",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue,
                            modifier = Modifier.clickable { onSearchClick() }
                        )
                    }
                }

                // Nearby Pharmacy Cards with balanced flex, text truncation, and clean sector badge
                val visiblePharmacies = pharmacies.take(displayedPharmacyCount)
                items(visiblePharmacies, key = { it.id }) { pharmacy ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Slate200, RoundedCornerShape(18.dp))
                            .clickable { onPharmacyClick(pharmacy) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AccentGreenLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalPharmacy,
                                    contentDescription = null,
                                    tint = AccentGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                val sectorLabel = com.quickmeds24.app.data.repository.extractSectorTag(pharmacy)

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = pharmacy.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (sectorLabel.contains("Alpha 1")) PrimaryBlueLight else Color(0xFFEEF2FF)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = sectorLabel,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (sectorLabel.contains("Alpha 1")) PrimaryBlue else Color(0xFF4F46E5),
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }

                                Text(
                                    text = pharmacy.address,
                                    fontSize = 10.sp,
                                    color = Slate500,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(top = 2.dp)
                                )

                                val formattedRating = String.format(java.util.Locale.US, "%.1f", pharmacy.rating)
                                val formattedDist = String.format(java.util.Locale.US, "%.1f", pharmacy.distanceKm)
                                Text(
                                    text = "★ $formattedRating (${pharmacy.reviewsCount}) • $formattedDist km • ~${pharmacy.estimatedDeliveryMinutes}m",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate600,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AccentGreenLight)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Open",
                                    color = AccentGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Load more pharmacies from farther distance coordinates
                if (displayedPharmacyCount < pharmacies.size) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Slate50),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Slate200, RoundedCornerShape(16.dp))
                                .clickable(enabled = !isLoadingFarPharmacies) {
                                    isLoadingFarPharmacies = true
                                    coroutineScope.launch {
                                        delay(450)
                                        displayedPharmacyCount = (displayedPharmacyCount + 5).coerceAtMost(pharmacies.size)
                                        isLoadingFarPharmacies = false
                                    }
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 14.dp, horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isLoadingFarPharmacies) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = PrimaryBlue,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Fetching pharmacies from farther coordinates...",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = PrimaryBlue
                                        )
                                    }
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddLocationAlt,
                                            contentDescription = null,
                                            tint = PrimaryBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "Load More Nearby Pharmacies (+5 More)",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryBlue
                                            )
                                            val nextSectors = pharmacies.drop(displayedPharmacyCount).take(3).map { com.quickmeds24.app.data.repository.extractSectorTag(it) }.distinct().joinToString(", ")
                                            if (nextSectors.isNotEmpty()) {
                                                Text(
                                                    text = "Fetch from $nextSectors...",
                                                    fontSize = 10.sp,
                                                    color = Slate500
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

        // Floating Sticky "View Cart" Bar on Home Screen
        if (cartCount > 0) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { onCartClick() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "$cartCount Items • ₹${"%.2f".format(totalCartAmount)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "From ${cartItems.firstOrNull()?.pharmacyName ?: "Sharma Medical Store"}",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "View Cart",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Previous / Delivered Orders Modal Dialog
        if (showPreviousOrdersDialog) {
            PreviousOrdersDialog(
                orders = deliveredOrders,
                onDismiss = { showPreviousOrdersDialog = false }
            )
        }
    }
}
