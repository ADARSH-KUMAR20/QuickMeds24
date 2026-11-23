package com.quickmeds24.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmeds24.app.data.model.Coordinates
import com.quickmeds24.app.data.model.TargetedPharmacy
import com.quickmeds24.app.theme.*

/**
 * InteractiveMapView
 * Renders a Google Maps-inspired area for Greater Noida (Alpha 2 to Alpha 1):
 * - Google Maps land, roads, and green spaces palette
 * - Visual route split:
 *     Phase 1: Rider in Alpha 2 -> Heading to Alpha 2 Pharmacy to pick up medicine
 *     Phase 2: Alpha 2 Pharmacy -> Delivering to Customer in Alpha 1
 * - Live animated rider pulse beacon with vehicle badge
 * - Google Map-style controls: Live GPS, Recenter, Map Layer attribution
 */
@Composable
fun InteractiveMapView(
    currentLocation: Coordinates,
    routeCoordinates: List<Coordinates>,
    targetedPharmacy: TargetedPharmacy,
    destinationArea: String,
    isContinuous: Boolean,
    frequencySec: Int,
    isMedicinePickedUp: Boolean,
    speedKmh: Int = 32,
    onMapClick: () -> Unit,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulse animation for rider position
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 16f,
        targetValue = 42f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    // Accurate bounding box encompassing Alpha 2 North (28.4830, 77.5190) -> Alpha 2 Store (28.4775, 77.5145) -> Alpha 1 Customer (28.4735, 77.5098)
    val minLat = 28.4715
    val maxLat = 28.4850
    val minLng = 77.5070
    val maxLng = 77.5215

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(310.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFFE8ECE9))
            .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(22.dp))
            .clickable { onMapClick() }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            fun toScreenX(lng: Double): Float {
                val normalized = ((lng - minLng) / (maxLng - minLng)).coerceIn(0.0, 1.0)
                return (normalized * (width * 0.82f) + (width * 0.09f)).toFloat()
            }

            fun toScreenY(lat: Double): Float {
                val normalized = ((lat - minLat) / (maxLat - minLat)).coerceIn(0.0, 1.0)
                return (height - (normalized * (height * 0.78f) + (height * 0.11f))).toFloat()
            }

            // 1. Google Maps Land & Park polygons
            // Park 1: Alpha 1 City Park (lower-left zone)
            drawRoundRect(
                color = Color(0xFFD2E6D0),
                topLeft = Offset(width * 0.05f, height * 0.65f),
                size = Size(width * 0.32f, height * 0.28f),
                cornerRadius = CornerRadius(24f, 24f)
            )

            // Park 2: Alpha 2 Greenbelt (upper-right zone)
            drawRoundRect(
                color = Color(0xFFD5E9D3),
                topLeft = Offset(width * 0.62f, height * 0.08f),
                size = Size(width * 0.34f, height * 0.25f),
                cornerRadius = CornerRadius(24f, 24f)
            )

            // 2. Google Maps Road Network
            // Arterial background road casing (soft grey)
            val roadCasingPath = Path().apply {
                // Main Alpha 2 to Alpha 1 Sector Highway
                moveTo(width * 0.95f, height * 0.12f)
                lineTo(width * 0.55f, height * 0.45f)
                lineTo(width * 0.18f, height * 0.85f)

                // Cross street: Commercial Belt
                moveTo(0f, height * 0.50f)
                lineTo(width, height * 0.40f)

                // Sector cross streets
                moveTo(width * 0.30f, 0f)
                lineTo(width * 0.35f, height)

                moveTo(width * 0.72f, 0f)
                lineTo(width * 0.78f, height)
            }
            drawPath(
                path = roadCasingPath,
                color = Color(0xFFD8DCDA),
                style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Road surface (pure white Google Maps roads)
            drawPath(
                path = roadCasingPath,
                color = Color.White,
                style = Stroke(width = 11f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Minor residential streets grid
            val minorRoadPath = Path().apply {
                for (i in 1..4) {
                    val y = height * (i * 0.20f)
                    moveTo(width * 0.08f, y)
                    lineTo(width * 0.92f, y)
                }
            }
            drawPath(
                path = minorRoadPath,
                color = Color(0xFFF3F4F6),
                style = Stroke(width = 4f)
            )

            // 3. Draw Route Polyline from Alpha 2 Rider Start -> Alpha 2 Pharmacy -> Alpha 1 Customer
            if (routeCoordinates.isNotEmpty()) {
                val pharmacyCoord = targetedPharmacy.coordinates
                val storeX = toScreenX(pharmacyCoord.longitude)
                val storeY = toScreenY(pharmacyCoord.latitude)

                // Stage 1 Route: Rider Start -> Pharmacy (indices 0 to 4)
                val stage1Path = Path()
                val stage1Points = routeCoordinates.take(5)
                stage1Points.forEachIndexed { idx, c ->
                    val px = toScreenX(c.longitude)
                    val py = toScreenY(c.latitude)
                    if (idx == 0) stage1Path.moveTo(px, py) else stage1Path.lineTo(px, py)
                }

                // Stage 2 Route: Pharmacy -> Customer (indices 4 to end)
                val stage2Path = Path()
                val stage2Points = routeCoordinates.drop(4)
                stage2Points.forEachIndexed { idx, c ->
                    val px = toScreenX(c.longitude)
                    val py = toScreenY(c.latitude)
                    if (idx == 0) stage2Path.moveTo(px, py) else stage2Path.lineTo(px, py)
                }

                // Draw Stage 1 Line (Alpha 2 Pickup leg)
                drawPath(
                    path = stage1Path,
                    color = if (!isMedicinePickedUp) Color(0xFFFDE68A) else Color(0xFFE2E8F0),
                    style = Stroke(width = 12f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                drawPath(
                    path = stage1Path,
                    color = if (!isMedicinePickedUp) Color(0xFFF59E0B) else Color(0xFF94A3B8),
                    style = Stroke(
                        width = 6f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                        pathEffect = if (!isMedicinePickedUp) PathEffect.dashPathEffect(floatArrayOf(16f, 10f)) else null
                    )
                )

                // Draw Stage 2 Line (Delivery leg to Alpha 1)
                drawPath(
                    path = stage2Path,
                    color = Color(0xFFBFDBFE),
                    style = Stroke(width = 14f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                drawPath(
                    path = stage2Path,
                    color = PrimaryBlue,
                    style = Stroke(
                        width = 7f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                        pathEffect = if (isMedicinePickedUp) null else PathEffect.dashPathEffect(floatArrayOf(16f, 10f))
                    )
                )

                // 4. Draw Point A: Rider Start in Alpha 2 (28.4830, 77.5190)
                val riderStartX = toScreenX(77.5190)
                val riderStartY = toScreenY(28.4830)
                drawCircle(color = Color(0xFF0284C7).copy(alpha = 0.2f), radius = 18f, center = Offset(riderStartX, riderStartY))
                drawCircle(color = Color(0xFF0284C7), radius = 11f, center = Offset(riderStartX, riderStartY))
                drawCircle(color = Color.White, radius = 5f, center = Offset(riderStartX, riderStartY))

                // 5. Draw Point B: Pharmacy in Alpha 2 (Wellness Forever)
                drawCircle(color = AccentGreen.copy(alpha = 0.25f), radius = 22f, center = Offset(storeX, storeY))
                drawCircle(color = AccentGreen, radius = 14f, center = Offset(storeX, storeY))
                drawCircle(color = Color.White, radius = 6f, center = Offset(storeX, storeY))

                // 6. Draw Point C: Customer Address in Alpha 1 (28.4735, 77.5098)
                val destX = toScreenX(77.5098)
                val destY = toScreenY(28.4735)
                drawCircle(color = AccentRed.copy(alpha = 0.25f), radius = 22f, center = Offset(destX, destY))
                drawCircle(color = AccentRed, radius = 14f, center = Offset(destX, destY))
                drawCircle(color = Color.White, radius = 6f, center = Offset(destX, destY))

                // 7. Draw Moving Rider (Rohit) with Pulse wave
                val riderX = toScreenX(currentLocation.longitude)
                val riderY = toScreenY(currentLocation.latitude)

                // Animated radar pulse
                drawCircle(
                    color = PrimaryBlue.copy(alpha = pulseAlpha),
                    radius = pulseRadius,
                    center = Offset(riderX, riderY)
                )
                // Outer ring
                drawCircle(
                    color = Color.White,
                    radius = 16f,
                    center = Offset(riderX, riderY)
                )
                // Core scooter dot
                drawCircle(
                    color = PrimaryBlue,
                    radius = 13f,
                    center = Offset(riderX, riderY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 4f,
                    center = Offset(riderX, riderY)
                )
            }
        }

        // Top Status Header: Live Cadence + Route Stage Indicator
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(Color.White.copy(alpha = 0.94f), RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(if (isContinuous) AccentGreen else AccentAmber)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isContinuous) "LIVE GPS • Continuous" else "Bg: ${frequencySec}s",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE0F2FE), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "$speedKmh km/h",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = PrimaryBlue
                        )
                    }
                }
                Text(
                    text = if (isMedicinePickedUp) "Alpha 2 ➔ Alpha 1 (Delivering)" else "Alpha 2 ➔ Store (Pickup)",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isMedicinePickedUp) PrimaryBlue else Color(0xFFD97706)
                )
            }
        }

        // Top-Right GPS Refresh Action
        IconButton(
            onClick = onRefreshClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(36.dp)
                .background(Color.White.copy(alpha = 0.94f), CircleShape)
                .border(1.dp, Color(0xFFE2E8F0), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Center GPS",
                tint = PrimaryBlue,
                modifier = Modifier.size(18.dp)
            )
        }

        // Bottom Map Route Legend Banner
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(10.dp)
                .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (isMedicinePickedUp) AccentGreen else Color(0xFFF59E0B))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isMedicinePickedUp) "Rx Picked Up at Alpha 2" else "En route to Alpha 2 Store",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate800
                )
            }

            Text(
                text = "➔ $destinationArea (By ${com.quickmeds24.app.utils.TimeUtils.formatIstDeliveryTime(7)})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = PrimaryBlue
            )
        }
    }
}
