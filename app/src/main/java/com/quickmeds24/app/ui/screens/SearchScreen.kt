package com.quickmeds24.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import coil.compose.AsyncImage
import com.quickmeds24.app.data.model.CartItem
import com.quickmeds24.app.data.model.Medicine
import com.quickmeds24.app.data.model.Pharmacy
import com.quickmeds24.app.data.repository.MedicineSearchResult
import com.quickmeds24.app.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    searchResults: List<MedicineSearchResult>,
    cartItems: List<CartItem>,
    selectedPharmacy: Pharmacy? = null,
    onClearPharmacyFilter: () -> Unit = {},
    isSearching: Boolean = false,
    hasMore: Boolean = false,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    onMedicineClick: (MedicineSearchResult) -> Unit,
    onAddToCart: (Medicine, Pharmacy) -> Unit,
    onUpdateQuantity: (String, Int) -> Unit,
    onViewCart: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCartCount = cartItems.sumOf { it.quantity }
    val totalCartAmount = cartItems.fold(0.0) { acc, i -> acc + (i.medicine.price * i.quantity) }

    var recentlyAddedName by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    // Pagination listener: triggers load more only when user scrolls down to the last item
    val shouldLoadMore = remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0)
            totalItems > 0 && lastVisibleItemIndex >= totalItems - 1
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && hasMore && !isSearching) {
            onLoadMore()
        }
    }

    LaunchedEffect(recentlyAddedName) {
        if (recentlyAddedName != null) {
            delay(3000)
            recentlyAddedName = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar with Back & Cart Icon matching Image 6
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Slate800)
                }

                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = { Text("Search 500+ medicines, pharmacies...", fontSize = 13.sp, color = Slate400) },
                    leadingIcon = {
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = BrandBlue
                            )
                        } else {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Slate400)
                        }
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Slate400)
                            }
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Slate50,
                        unfocusedContainerColor = Slate50,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Slate200, RoundedCornerShape(18.dp))
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Cart Icon with live badge (non-clipped, prominent number)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clickable { onViewCart() },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Slate50),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Cart",
                            tint = Slate800,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (totalCartCount > 0) {
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
                                text = totalCartCount.toString(),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            // Animated Visual Feedback Banner: Added item notification
            AnimatedVisibility(
                visible = recentlyAddedName != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .background(AccentGreenLight, RoundedCornerShape(14.dp))
                        .border(1.dp, AccentGreen.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Added \"$recentlyAddedName\" to cart",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGreen
                        )
                    }
                    Text(
                        text = "View Cart ➔",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = AccentGreen,
                        modifier = Modifier.clickable { onViewCart() }
                    )
                }
            }

            // Dedicated Selected Pharmacy Banner (e.g. When user clicked "Wellness Forever Alpha 2")
            if (selectedPharmacy != null) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlueLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .border(1.dp, PrimaryBlue.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PrimaryBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalPharmacy,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedPharmacy.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                val dist = String.format(java.util.Locale.US, "%.1f", selectedPharmacy.distanceKm)
                                Text(
                                    text = "${selectedPharmacy.branch} • $dist km (~${selectedPharmacy.estimatedDeliveryMinutes}m ETA)",
                                    fontSize = 11.sp,
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .border(1.dp, Slate200, RoundedCornerShape(8.dp))
                                .clickable { onClearPharmacyFilter() }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "All Stores ✕",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate700
                            )
                        }
                    }
                }
            }

            // Results summary header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedPharmacy != null) {
                        if (query.isEmpty()) "${selectedPharmacy.name} Catalog (${searchResults.size})"
                        else "Results in ${selectedPharmacy.name} (${searchResults.size})"
                    } else {
                        if (query.isEmpty()) "All Available Medicines (${searchResults.size})"
                        else "Results for \"$query\" (${searchResults.size})"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate700,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedPharmacy != null) "${selectedPharmacy.branch} Store" else "Nearest Pharmacies First",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandBlue,
                    maxLines = 1
                )
            }

            // Product Cards with Pull-to-refresh
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (totalCartCount > 0) 80.dp else 0.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                itemsIndexed(searchResults) { index, result ->
                    val med = result.medicine
                    val pharm = result.pharmacy
                    val inCartItem = cartItems.find { it.medicine.id == med.id }

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Slate200, RoundedCornerShape(18.dp))
                            .clickable { onMedicineClick(result) }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            // Top Row: Product image, Details, and Add/Qty button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Slate50),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = med.imageUrl ?: "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=120",
                                            contentDescription = med.name,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = med.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate900,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${med.brand} • ${med.dosageForm}",
                                            fontSize = 11.sp,
                                            color = Slate500,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(top = 2.dp)
                                        ) {
                                            Text(
                                                text = "₹${"%.2f".format(med.price)}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Slate900
                                            )
                                            if (med.mrp > med.price) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "₹${"%.2f".format(med.mrp)}",
                                                    fontSize = 11.sp,
                                                    color = Slate400,
                                                    style = androidx.compose.ui.text.TextStyle(
                                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                                    )
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "${med.discountPercentage}% OFF",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AccentGreen
                                                )
                                            }
                                        }
                                    }
                                }

                                // Stepper or Add button
                                if (inCartItem != null) {
                                    Row(
                                        modifier = Modifier
                                            .background(PrimaryBlueLight, RoundedCornerShape(14.dp))
                                            .border(1.dp, PrimaryBlue, RoundedCornerShape(14.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = { onUpdateQuantity(med.id, -1) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Remove,
                                                contentDescription = "Decrease",
                                                tint = PrimaryBlue,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Text(
                                            text = inCartItem.quantity.toString(),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = PrimaryBlue,
                                            modifier = Modifier.padding(horizontal = 6.dp)
                                        )
                                        IconButton(
                                            onClick = { onUpdateQuantity(med.id, 1) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Add,
                                                contentDescription = "Increase",
                                                tint = PrimaryBlue,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            onAddToCart(med, pharm)
                                            recentlyAddedName = med.name
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                        shape = RoundedCornerShape(14.dp),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text(
                                            text = "Add",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = Slate100, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Pharmacy & Store Address Section (Highlighting Alpha 1 or Alpha 2)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Slate50, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = null,
                                        tint = if (result.sectorTag.contains("Alpha 1")) BrandBlue else Color(0xFF6366F1),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = result.storeName,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Slate800,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            // Sector Badge
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(
                                                        if (result.sectorTag.contains("Alpha 1")) PrimaryBlueLight else Color(0xFFEEF2FF)
                                                    )
                                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = result.sectorTag,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (result.sectorTag.contains("Alpha 1")) PrimaryBlue else Color(0xFF4F46E5),
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                        }
                                        Text(
                                            text = result.storeAddress,
                                            fontSize = 10.sp,
                                            color = Slate500,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(top = 1.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsBike,
                                        contentDescription = null,
                                        tint = Slate400,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    val formattedDist = String.format(java.util.Locale.US, "%.1f", result.distanceKm)
                                    Text(
                                        text = "$formattedDist km • ~${result.deliveryMinutes}m",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate600,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                // Interactive Load More CTA when more medicines exist
                if (hasMore && !isSearching && searchResults.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            OutlinedButton(
                                onClick = onLoadMore,
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, Slate300),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandBlue)
                            ) {
                                Icon(Icons.Default.ExpandMore, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Load More Medicines (+5)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Loading more pagination indicator
                if (isSearching) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = BrandBlue,
                                strokeWidth = 2.5.dp
                            )
                        }
                    }
                }
            }
        }
        }

        // Floating Sticky View Cart Bar
        if (totalCartCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandBlue),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onViewCart() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
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
                                    text = "$totalCartCount Items in Cart",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "₹${"%.2f".format(totalCartAmount)} Total",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "View Cart",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
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
        }
    }
}
