package com.quickmeds24.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.quickmeds24.app.data.model.Medicine
import com.quickmeds24.app.data.model.Pharmacy
import com.quickmeds24.app.theme.*
import kotlinx.coroutines.delay

@Composable
fun MedicineDetailScreen(
    medicine: Medicine,
    pharmacy: Pharmacy,
    cartCount: Int,
    onAddToCart: (Medicine, Pharmacy, Int) -> Unit,
    onCartClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var quantity by remember { mutableStateOf(1) }
    var isProductDetailsOpen by remember { mutableStateOf(true) }
    var isSubstitutesOpen by remember { mutableStateOf(false) }
    var showAddedFeedback by remember { mutableStateOf(false) }

    LaunchedEffect(showAddedFeedback) {
        if (showAddedFeedback) {
            delay(3000)
            showAddedFeedback = false
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top App Bar with Back, Share, and Live Cart Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Slate800)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Slate800)
                        }

                        // Live Cart Badge Icon
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Slate50)
                                .clickable { onCartClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = "Cart",
                                tint = Slate800,
                                modifier = Modifier.size(20.dp)
                            )
                            if (cartCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(17.dp)
                                        .clip(CircleShape)
                                        .background(AccentRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cartCount.toString(),
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Big Medicine Packaging Image matching Image 7
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Slate50),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = medicine.imageUrl ?: "https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=400",
                        contentDescription = medicine.name,
                        modifier = Modifier.size(160.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Title & Price matching Image 7
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = medicine.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Slate900
                    )
                    Text(
                        text = "${medicine.brand} • ${medicine.dosageForm}",
                        fontSize = 13.sp,
                        color = Slate500,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "₹${"%.2f".format(medicine.price)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (medicine.mrp > medicine.price) {
                            Text(
                                text = "₹${"%.2f".format(medicine.mrp)}",
                                fontSize = 14.sp,
                                color = Slate400,
                                style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Targeted Pharmacy Banner matching Image 7
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .border(1.dp, Slate200, RoundedCornerShape(18.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = AccentGreen)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            val sector = com.quickmeds24.app.data.repository.extractSectorTag(pharmacy)
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
                                            if (pharmacy.address.contains("Alpha 1")) PrimaryBlue else Color(0xFF6366F1)
                                        )
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = sector,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                }
                            }
                            Text(
                                text = pharmacy.address,
                                fontSize = 10.sp,
                                color = Slate500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                            val distFormatted = String.format(java.util.Locale.US, "%.1f", pharmacy.distanceKm)
                            Text(
                                text = "$distFormatted km away • Delivery in ~${pharmacy.estimatedDeliveryMinutes} min",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate600,
                                maxLines = 1
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate400)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Accordion 1: Product Details matching Image 7
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .border(1.dp, Slate200, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isProductDetailsOpen = !isProductDetailsOpen }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Product Details", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                            Icon(
                                imageVector = if (isProductDetailsOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Slate600
                            )
                        }

                        if (isProductDetailsOpen) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "Generic: ${medicine.genericName}", fontSize = 12.sp, color = Slate700)
                                Text(text = "Composition: ${medicine.composition}", fontSize = 12.sp, color = Slate700)
                                Text(text = "Manufacturer: ${medicine.manufacturer}", fontSize = 12.sp, color = Slate700)
                                Text(text = medicine.description, fontSize = 11.sp, color = Slate500)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Accordion 2: Substitute Brands matching Image 7
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .border(1.dp, Slate200, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isSubstitutesOpen = !isSubstitutesOpen }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Substitute Brands (3)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                            Icon(
                                imageVector = if (isSubstitutesOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Slate600
                            )
                        }

                        if (isSubstitutesOpen) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                medicine.substitutes.forEach { sub ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(text = sub.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate800)
                                            Text(text = sub.manufacturer, fontSize = 10.sp, color = Slate400)
                                        }
                                        Text(text = "₹${"%.2f".format(sub.price)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Animated Confirmation Toast Banner (Clear user feedback)
        AnimatedVisibility(
            visible = showAddedFeedback,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp, start = 20.dp, end = 20.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AccentGreen),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Added $quantity item(s) to Cart!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "View Cart ➔",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.clickable { onCartClick() }
                    )
                }
            }
        }

        // Bottom Bar: Quantity Stepper & Add to Cart Action
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, Slate100)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Stepper [- 1 +]
            Row(
                modifier = Modifier
                    .background(Slate50, RoundedCornerShape(14.dp))
                    .border(1.dp, Slate200, RoundedCornerShape(14.dp))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (quantity > 1) quantity-- },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                }
                Text(
                    text = quantity.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(
                    onClick = { quantity++ },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                }
            }

            // Interactive Button: Transforms upon adding so user immediately understands!
            Button(
                onClick = {
                    onAddToCart(medicine, pharmacy, quantity)
                    showAddedFeedback = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showAddedFeedback) AccentGreen else PrimaryBlue
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ) {
                if (showAddedFeedback) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Added to Cart! ✓", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Add to Cart", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
