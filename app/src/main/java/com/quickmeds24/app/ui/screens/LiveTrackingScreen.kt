package com.quickmeds24.app.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.quickmeds24.app.data.model.TrackingTelemetry
import com.quickmeds24.app.theme.*
import com.quickmeds24.app.ui.components.InteractiveMapView
import kotlinx.coroutines.delay

@Composable
fun LiveTrackingScreen(
    telemetry: TrackingTelemetry?,
    destinationArea: String,
    onMapClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onResetSimulation: () -> Unit,
    onOrderCompleted: () -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val t = telemetry ?: return

    // Realistic countdown timer counting down second by second
    var secondsRemaining by remember(t.etaMinutes) { mutableStateOf(t.etaMinutes * 60) }
    LaunchedEffect(t.etaMinutes) {
        secondsRemaining = t.etaMinutes * 60
        while (secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining--
        }
    }

    val displayMinutes = secondsRemaining / 60
    val displaySeconds = secondsRemaining % 60
    val timerString = String.format("%02d:%02d", displayMinutes, displaySeconds)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Header matching Image 10
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
                text = "Order #${t.orderId}",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            val istTime = com.quickmeds24.app.utils.TimeUtils.formatIstDeliveryTime(t.etaMinutes)

            // Order Delivered Success & Completion Banner (Removes track section when completed)
            if (t.status == com.quickmeds24.app.data.model.TrackingStatus.DELIVERED) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = AccentGreenLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, AccentGreen, RoundedCornerShape(18.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(38.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Order Delivered Successfully! 🎉",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGreen
                        )
                        Text(
                            text = "Rider Rohit delivered your order at Flat 402, Alpha 1. Marked as delivered order.",
                            fontSize = 11.sp,
                            color = Slate600,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                        Button(
                            onClick = onOrderCompleted,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                        ) {
                            Text(text = "Complete & Return to Home", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Lightning Delivery Badge with Live Ticking Timer in Delhi/Kolkata timing
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlueLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (t.status == com.quickmeds24.app.data.model.TrackingStatus.DELIVERED)
                                "Delivered at Doorstep"
                            else if (t.isMedicinePickedUp)
                                "Delivery by $istTime"
                            else
                                "Picking up (By $istTime)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }

                    // Live Ticking Clock Badge
                    Box(
                        modifier = Modifier
                            .background(PrimaryBlue, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (t.status == com.quickmeds24.app.data.model.TrackingStatus.DELIVERED) "DELIVERED" else "$istTime ($timerString)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Alpha 2 -> Pharmacy -> Alpha 1 Journey Banner
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Slate50),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate200, RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.DirectionsBike,
                        contentDescription = null,
                        tint = if (t.isMedicinePickedUp) AccentGreen else Color(0xFFF59E0B),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (t.isMedicinePickedUp)
                            "Rider Rohit picked medicine from Alpha 2 store. Delivering to you in $destinationArea"
                        else
                            "Rider Rohit en route in Alpha 2 to collect your medicine packet",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate700
                    )
                }
            }

            // 4 Steps Horizontal Progress Bar matching Image 10
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Confirmed
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Confirmed", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate800)
                }

                // Preparing
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (t.statusStepIndex >= 1) PrimaryBlue else Slate100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = if (t.statusStepIndex >= 1) Color.White else Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Preparing", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate800)
                }

                // On the Way (Highlighted scooter in image 10)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (t.statusStepIndex >= 2) PrimaryBlue else Slate100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.TwoWheeler,
                            contentDescription = null,
                            tint = if (t.statusStepIndex >= 2) Color.White else Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "On the Way", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }

                // Delivered
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (t.statusStepIndex >= 3) AccentGreen else Slate100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Flag,
                            contentDescription = null,
                            tint = if (t.statusStepIndex >= 3) Color.White else Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Delivered", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                }
            }

            // Interactive Live Map View matching Image 10 with Rapido continuous tracking
            InteractiveMapView(
                currentLocation = t.currentLocation,
                routeCoordinates = t.routeCoordinates,
                targetedPharmacy = t.targetedPharmacy,
                destinationArea = destinationArea,
                isContinuous = t.isContinuousActive,
                frequencySec = t.updateFrequencySeconds,
                isMedicinePickedUp = t.isMedicinePickedUp,
                speedKmh = t.speedKmh,
                onMapClick = onMapClick,
                onRefreshClick = onRefreshClick
            )

            // Targeted Pharmacy & Pickup Status Card (Explicit User Requirement)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Slate50),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate200, RoundedCornerShape(18.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Targeted Store", fontSize = 10.sp, color = Slate500, fontWeight = FontWeight.Bold)
                            Text(
                                text = t.targetedPharmacy.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            val distFormatted = String.format(java.util.Locale.US, "%.1f", t.targetedPharmacy.distanceKm)
                            Text(
                                text = "${t.targetedPharmacy.branch} • $distFormatted km",
                                fontSize = 11.sp,
                                color = Slate500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Divider(color = Slate200)

                    // Did rider pick the medicine yet or not?
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Medicine Picked Up?", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Slate600)
                        if (t.isMedicinePickedUp) {
                            Box(
                                modifier = Modifier
                                    .background(AccentGreenLight, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "YES • Picked at ${t.pickedUpTime ?: "11:28 AM"}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentGreen
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .background(AccentAmberLight, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = "En route to Store", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentAmber)
                            }
                        }
                    }

                    Divider(color = Slate200)

                    // Delivery OTP for Rider (Explicit User Requirement)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Delivery OTP for Rider", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Slate600)
                        Box(
                            modifier = Modifier
                                .background(PrimaryBlueLight, RoundedCornerShape(8.dp))
                                .border(1.dp, PrimaryBlue, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "OTP: ${t.deliveryOtp}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = PrimaryBlue
                            )
                        }
                    }
                }
            }

            // Rider Profile Card matching Image 10
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate200, RoundedCornerShape(18.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Slate100),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = t.rider.avatarUrl,
                                    contentDescription = t.rider.name,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = t.rider.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                                Text(
                                    text = "★ ${t.rider.rating} • ${t.rider.vehicleNumber}",
                                    fontSize = 11.sp,
                                    color = Slate500
                                )
                                Text(
                                    text = "Your rider is on the way",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryBlue,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        // Call & Chat Action Buttons matching Image 10
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${t.rider.phone}"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlueLight)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = "Call", tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                            }

                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${t.rider.phone}"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlueLight)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = "Chat", tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = Slate100)
                    Spacer(modifier = Modifier.height(12.dp))

                    // ETA & Distance Metrics matching Image 10
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            text = if (t.status == com.quickmeds24.app.data.model.TrackingStatus.DELIVERED)
                                "Delivered"
                            else
                                "Delivery by ${com.quickmeds24.app.utils.TimeUtils.formatIstDeliveryTime(t.etaMinutes)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                        Text(
                            text = "|",
                            color = Slate300,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${t.distanceRemainingKm} km away",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                    }
                }
            }

            // Quick Simulation Reset button
            TextButton(
                onClick = onResetSimulation,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Default.RestartAlt, contentDescription = null, tint = Slate500, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Reset Rider Simulation", color = Slate600, fontSize = 11.sp)
            }
        }
    }
}

