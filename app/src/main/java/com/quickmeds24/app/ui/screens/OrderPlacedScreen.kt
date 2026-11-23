package com.quickmeds24.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmeds24.app.data.model.Coordinates
import com.quickmeds24.app.data.model.Pharmacy
import com.quickmeds24.app.theme.*
import kotlinx.coroutines.delay

enum class OrderDispatchStage {
    BROADCASTING,      // 0 - 3s: Pinging candidate pharmacies with stock
    STORE_ACCEPTED,    // 3 - 5.5s: Sharma Medical Store confirms order
    RIDER_ASSIGNED,    // 5.5 - 7.5s: Rohit Kumar assigned
    RIDER_PICKING      // 7.5s+: Heading to pharmacy to pick up medicines
}

@Composable
fun OrderPlacedScreen(
    orderId: String,
    candidatePharmacies: List<Pharmacy> = emptyList(),
    onTrackLiveOrder: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var stage by remember { mutableStateOf(OrderDispatchStage.BROADCASTING) }
    var secondsElapsed by remember { mutableStateOf(0) }

    // Multi-stage progression simulating real-world server dispatching like Zomato/Blinkit
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            secondsElapsed++
            when (secondsElapsed) {
                3 -> stage = OrderDispatchStage.STORE_ACCEPTED
                5 -> stage = OrderDispatchStage.RIDER_ASSIGNED
                7 -> stage = OrderDispatchStage.RIDER_PICKING
            }
        }
    }

    // Radar pulsing infinite transition
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val radarPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )
    val radarAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.offset(x = (-8).dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Slate800)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Live Order Dispatch",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Status Banner
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (stage) {
                        OrderDispatchStage.BROADCASTING -> PrimaryBlueLight.copy(alpha = 0.6f)
                        else -> AccentGreenLight.copy(alpha = 0.6f)
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Pulsing Radar / Success Circle
                    Box(
                        modifier = Modifier.size(90.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (stage == OrderDispatchStage.BROADCASTING) {
                            // Animated Radar Waves
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = PrimaryBlue.copy(alpha = radarAlpha),
                                    radius = (size.minDimension / 2f) * radarPulse,
                                    style = Stroke(width = 3.dp.toPx())
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sensors,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(AccentGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(38.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = when (stage) {
                            OrderDispatchStage.BROADCASTING -> "Broadcasting to Pharmacies..."
                            OrderDispatchStage.STORE_ACCEPTED -> "Sharma Medical Accepted!"
                            OrderDispatchStage.RIDER_ASSIGNED -> "Rider Rohit Kumar Assigned!"
                            OrderDispatchStage.RIDER_PICKING -> "Rider Heading to Pharmacy!"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Slate900
                    )
                    Text(
                        text = "Order #$orderId • Live Dispatch Server",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Zomato-Style Candidate Pharmacies Broadcast Status
            Text(
                text = "Nearby Pharmacies with Stock",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Slate800,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            val displayPharmacies = if (candidatePharmacies.isNotEmpty()) candidatePharmacies else listOf(
                Pharmacy("sharma", "Sharma Medical Store", "Alpha 1", "Shop 14, Commercial Belt, Alpha 1, Greater Noida", 4.8, 428, 0.7, 15, true, "LIC-1", Coordinates(28.4738, 77.5092)),
                Pharmacy("apollo", "Apollo Pharmacy Alpha 1", "Alpha 1", "Plot 8, Pocket A, Alpha 1, Greater Noida", 4.7, 312, 1.1, 18, true, "LIC-2", Coordinates(28.4750, 77.5110)),
                Pharmacy("wellness", "Wellness Forever Alpha 2", "Alpha 2", "Shop 8, Main Sector Market, Alpha 2, Greater Noida", 4.9, 512, 1.8, 22, true, "LIC-3", Coordinates(28.4775, 77.5145))
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                displayPharmacies.forEachIndexed { idx, pharm ->
                    val isFirstStore = idx == 0
                    val isAccepted = isFirstStore && stage >= OrderDispatchStage.STORE_ACCEPTED

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAccepted) AccentGreenLight else Slate50
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isAccepted) 1.5.dp else 1.dp,
                                color = if (isAccepted) AccentGreen else Slate200,
                                shape = RoundedCornerShape(16.dp)
                            )
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
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isAccepted) AccentGreen else PrimaryBlueLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = null,
                                        tint = if (isAccepted) Color.White else PrimaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    val sector = com.quickmeds24.app.data.repository.extractSectorTag(pharm)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = pharm.name,
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
                                                    if (pharm.address.contains("Alpha 1")) PrimaryBlue else Color(0xFF6366F1)
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
                                        text = pharm.address,
                                        fontSize = 10.sp,
                                        color = Slate500,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Dynamic Status Pill
                            if (isAccepted) {
                                Row(
                                    modifier = Modifier
                                        .background(AccentGreen, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "ACCEPTED",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            } else if (stage == OrderDispatchStage.BROADCASTING) {
                                Row(
                                    modifier = Modifier
                                        .background(AccentAmberLight, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(10.dp),
                                        strokeWidth = 1.5.dp,
                                        color = AccentAmber
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Requesting...",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentAmber
                                    )
                                }
                            } else {
                                Text(
                                    text = "Released",
                                    fontSize = 10.sp,
                                    color = Slate400,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Assigned Rider & Medicine Pickup Card
            if (stage >= OrderDispatchStage.RIDER_ASSIGNED) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlueLight.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PrimaryBlue.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
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
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = "Rohit Kumar", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Slate900)
                                    Text(text = "★ 4.9 • 1,420 Deliveries • Super Rider", fontSize = 11.sp, color = Slate600)
                                }
                            }
                            // Pickup OTP
                            Box(
                                modifier = Modifier
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .border(1.dp, Slate200, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "DELIVERY OTP", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Slate500)
                                    Text(text = "4821", fontSize = 14.sp, fontWeight = FontWeight.Black, color = BrandNavy)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Slate200)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (stage == OrderDispatchStage.RIDER_ASSIGNED) "Rider heading to Sharma Medical Store..."
                                else "Picking up your medicines from Sharma Medical...",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandBlue
                            )
                            Text(text = "~2 min away", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentGreen)
                        }
                    }
                }
            }
        }

        // Bottom CTA: Track Live Order on Map
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Button(
                onClick = onTrackLiveOrder,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (stage >= OrderDispatchStage.RIDER_ASSIGNED) "Track Rider on Map ➔" else "View Live Tracking",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
