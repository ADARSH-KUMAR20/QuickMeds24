package com.quickmeds24.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.quickmeds24.app.data.model.CartItem
import com.quickmeds24.app.data.model.PaymentMethod
import com.quickmeds24.app.data.model.Pharmacy
import com.quickmeds24.app.theme.*
import com.quickmeds24.app.utils.UpiPaymentHelper

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    targetedPharmacy: Pharmacy?,
    selectedPaymentMethod: PaymentMethod,
    orderId: String = "QM-8847",
    onPaymentMethodSelected: (PaymentMethod) -> Unit,
    onUpdateQuantity: (String, Int) -> Unit,
    onProceedToPay: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val itemsTotal = cartItems.fold(0.0) { acc, item -> acc + (item.medicine.price * item.quantity) }
    val deliveryFee = 0.0
    val riderPay = 30.0 // Rider pay ₹30 as requested
    val totalPayable = itemsTotal + deliveryFee + riderPay

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // Header matching Image 8
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Slate800)
                }
                Text(
                    text = "Your Cart (${cartItems.sumOf { it.quantity }})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Cart Items
                items(cartItems) { item ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Slate200, RoundedCornerShape(18.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Slate50),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = item.medicine.imageUrl ?: "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=120",
                                        contentDescription = item.medicine.name,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.medicine.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${item.medicine.brand} • ${item.medicine.dosageForm}",
                                        fontSize = 11.sp,
                                        color = Slate400,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "₹${"%.2f".format(item.medicine.price * item.quantity)}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }

                            // Stepper [- 1 +]
                            Row(
                                modifier = Modifier
                                    .background(Slate50, RoundedCornerShape(12.dp))
                                    .border(1.dp, Slate200, RoundedCornerShape(12.dp)),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { onUpdateQuantity(item.medicine.id, -1) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                                Text(
                                    text = item.quantity.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                                IconButton(
                                    onClick = { onUpdateQuantity(item.medicine.id, 1) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                // Targeted Pharmacy with full address
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryBlueLight.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
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
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AccentGreenLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                val sector = targetedPharmacy?.let { com.quickmeds24.app.data.repository.extractSectorTag(it) } ?: "Alpha 1"
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = targetedPharmacy?.name ?: "Sharma Medical Store",
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
                                            .background(PrimaryBlue)
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = sector, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                                    }
                                }
                                Text(
                                    text = targetedPharmacy?.address ?: "Shop 14, Commercial Belt, Alpha 1, Greater Noida",
                                    fontSize = 10.sp,
                                    color = Slate500,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                val distFormatted = String.format(java.util.Locale.US, "%.1f", targetedPharmacy?.distanceKm ?: 0.7)
                                Text(
                                    text = "$distFormatted km • Delivery in ~15 min",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate600
                                )
                            }
                        }
                    }
                }

                // GPay, PhonePe, Paytm, COD Payment Selectors with Deep Linking
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(18.dp))
                            .border(1.dp, Slate200, RoundedCornerShape(18.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Select Payment Mode",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate800
                            )
                            Text(
                                text = "100% Secure UPI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGreen
                            )
                        }

                        PaymentMethod.values().forEach { method ->
                            val isSelected = method == selectedPaymentMethod
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) PrimaryBlueLight else Slate50)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) PrimaryBlue else Slate200,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onPaymentMethodSelected(method) }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = method.iconEmoji, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = method.displayName,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isSelected) PrimaryBlue else Slate800
                                        )
                                        Text(
                                            text = method.subtitle,
                                            fontSize = 10.sp,
                                            color = Slate500
                                        )
                                    }
                                }
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onPaymentMethodSelected(method) },
                                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                                )
                            }
                        }
                    }
                }

                // Bill Details
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate50),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Items Total", fontSize = 12.sp, color = Slate600)
                                Text(text = "₹${"%.2f".format(itemsTotal)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "Delivery Partner Fee (Rider Pay)", fontSize = 12.sp, color = Slate600)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                                }
                                Text(text = "₹30.00", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                            }
                            HorizontalDivider(color = Slate200, thickness = 1.dp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Total Payable", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate900)
                                Text(text = "₹${"%.2f".format(totalPayable)}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = PrimaryBlue)
                            }
                        }
                    }
                }
            }
        }

        // Proceed to Pay Button with GPay / UPI Deep Link Trigger
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, Slate100)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    UpiPaymentHelper.launchUpiPayment(
                        context = context,
                        paymentMethod = selectedPaymentMethod,
                        orderId = orderId,
                        amount = totalPayable,
                        onSuccessOrFallback = {
                            onProceedToPay()
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = if (selectedPaymentMethod == PaymentMethod.COD) "Place COD Order • ₹${"%.2f".format(totalPayable)}"
                    else "Pay via ${selectedPaymentMethod.displayName.split(" ").first()} • ₹${"%.2f".format(totalPayable)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
