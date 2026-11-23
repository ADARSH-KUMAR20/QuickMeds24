package com.quickmeds24.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmeds24.app.R
import com.quickmeds24.app.theme.*

/**
 * 4-lobed cloud cross / clover emblem matching the QwikMeds brand asset.
 * Draws the vibrant blue clover cloud with a centered rounded white cross (+).
 */
@Composable
fun QwikMedsLogoEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f
            val lobeRadius = w * 0.23f
            val offsetDist = w * 0.20f

            // 4 Circular Lobes in BrandBlue
            drawCircle(
                color = BrandBlue,
                radius = lobeRadius,
                center = Offset(cx, cy - offsetDist) // Top
            )
            drawCircle(
                color = BrandBlue,
                radius = lobeRadius,
                center = Offset(cx, cy + offsetDist) // Bottom
            )
            drawCircle(
                color = BrandBlue,
                radius = lobeRadius,
                center = Offset(cx - offsetDist, cy) // Left
            )
            drawCircle(
                color = BrandBlue,
                radius = lobeRadius,
                center = Offset(cx + offsetDist, cy) // Right
            )

            // Center connecting body
            drawCircle(
                color = BrandBlue,
                radius = w * 0.24f,
                center = Offset(cx, cy)
            )

            // White Medical Cross (+)
            val crossThickness = w * 0.135f
            val crossLength = w * 0.44f
            val crossCornerRadius = CornerRadius(crossThickness / 2f, crossThickness / 2f)

            // Horizontal cross bar
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(cx - crossLength / 2f, cy - crossThickness / 2f),
                size = Size(crossLength, crossThickness),
                cornerRadius = crossCornerRadius
            )

            // Vertical cross bar
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(cx - crossThickness / 2f, cy - crossLength / 2f),
                size = Size(crossThickness, crossLength),
                cornerRadius = crossCornerRadius
            )
        }
    }
}

/**
 * Brand Header showing the 4-lobed clover emblem + "QwikMeds" typography.
 */
@Composable
fun QwikMedsHeader(
    modifier: Modifier = Modifier,
    emblemSize: Dp = 32.dp,
    textSize: TextUnit = 22.sp,
    textColor: Color = BrandNavy
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        QwikMedsLogoEmblem(size = emblemSize)
        Spacer(modifier = Modifier.width(9.dp))
        Text(
            text = "QwikMeds",
            color = textColor,
            fontSize = textSize,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.4).sp
        )
    }
}

/**
 * 3D-styled Capsule Pill matching Image 2 & Splash foreground.
 */
@Composable
fun QwikMedsPillCapsule(
    modifier: Modifier = Modifier,
    width: Dp = 68.dp,
    height: Dp = 26.dp,
    angle: Float = -25f
) {
    Box(
        modifier = modifier
            .rotate(angle)
            .shadow(6.dp, CircleShape, spotColor = BrandBlue.copy(alpha = 0.35f))
            .width(width)
            .height(height)
            .clip(CircleShape)
            .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Blue half
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF2E86FF), BrandBlue, Color(0xFF004ECC))
                        )
                    )
            )
            // Light Blue / White half
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White, Color(0xFFE8F1FD), Color(0xFFC7DEFE))
                        )
                    )
            )
        }
    }
}

/**
 * 3D Pharmacy Scene Illustration from Splash Screen:
 * - Background city skyline
 * - Pharmacy storefront building with blue & white striped awning
 * - 3D Blue medical tote shopping bag with handles and white cross
 * - Medicine pill bottle with blue ribbed cap and cross label
 * - Capsule pill lying in front
 * - Soft layered wavy ground
 */
@Composable
fun QwikMedsHeroIllustration(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Soft Circular Ambient Glow Behind Illustration
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.Center)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFFE3F0FF), Color(0xFFF0F7FF), Color.Transparent)
                    )
                )
        )

        // Background City Skyline Silhouette
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.BottomCenter)
        ) {
            val w = size.width
            val h = size.height
            val skyColor = Color(0xFFDFEEFF)

            // Left building
            drawRoundRect(
                color = skyColor,
                topLeft = Offset(w * 0.12f, h * 0.25f),
                size = Size(w * 0.16f, h * 0.75f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            // Left small building
            drawRoundRect(
                color = Color(0xFFE8F3FF),
                topLeft = Offset(w * 0.05f, h * 0.45f),
                size = Size(w * 0.12f, h * 0.55f),
                cornerRadius = CornerRadius(6f, 6f)
            )

            // Right distant building
            drawRoundRect(
                color = skyColor.copy(alpha = 0.8f),
                topLeft = Offset(w * 0.78f, h * 0.32f),
                size = Size(w * 0.14f, h * 0.68f),
                cornerRadius = CornerRadius(8f, 8f)
            )
        }

        // Pharmacy Storefront Building (Center-Right)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 50.dp, bottom = 42.dp)
                .width(155.dp)
                .height(145.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color(0xFFF7FAFF))
                .border(1.dp, Color(0xFFD6E7FF), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Striped Awning (Blue and White stripes)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .shadow(4.dp, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                ) {
                    val stripes = listOf(
                        BrandBlue, Color.White, BrandBlue, Color.White, BrandBlue
                    )
                    stripes.forEachIndexed { idx, color ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(color)
                                .then(
                                    if (idx == 0) Modifier.clip(RoundedCornerShape(bottomStart = 8.dp))
                                    else if (idx == stripes.lastIndex) Modifier.clip(RoundedCornerShape(bottomEnd = 8.dp))
                                    else Modifier
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Shop Door and Window
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Window
                    Box(
                        modifier = Modifier
                            .size(46.dp, 40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFDCEBFF))
                            .border(1.5.dp, Color(0xFFB9D8FE), RoundedCornerShape(8.dp))
                    )
                    // Door
                    Box(
                        modifier = Modifier
                            .size(34.dp, 58.dp)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(Color(0xFFE3EFFF))
                            .border(1.5.dp, Color(0xFFB9D8FE), RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                    )
                }
            }
        }

        // 3D Medical Shopping Tote Bag (Foreground Left)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 48.dp, bottom = 26.dp)
                .size(130.dp, 138.dp)
        ) {
            // Tote Bag Handles
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .align(Alignment.TopCenter)
            ) {
                val handleColor = Color(0xFF0052CC)
                val strokeW = 10f
                val path = Path().apply {
                    moveTo(size.width * 0.32f, size.height)
                    cubicTo(
                        size.width * 0.32f, 0f,
                        size.width * 0.68f, 0f,
                        size.width * 0.68f, size.height
                    )
                }
                drawPath(path, handleColor, style = Stroke(width = strokeW, cap = StrokeCap.Round))
            }

            // Tote Bag Body
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .width(122.dp)
                    .height(106.dp)
                    .shadow(12.dp, RoundedCornerShape(22.dp), spotColor = BrandBlue.copy(alpha = 0.5f))
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF2D84FF), BrandBlue, Color(0xFF004ECC))
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                // White Medical Cross on Bag Front
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }

        // Medicine Pill Bottle (Right of Bag)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 120.dp, bottom = 28.dp)
                .width(48.dp)
                .height(68.dp)
                .shadow(8.dp, RoundedCornerShape(10.dp), spotColor = Color(0xFF002A7A).copy(alpha = 0.25f))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Bottle Cap (Ribbed Royal Blue)
                Box(
                    modifier = Modifier
                        .width(34.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(BrandBlue)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(5) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .fillMaxHeight()
                                    .background(Color(0xFF4594FF))
                            )
                        }
                    }
                }
                // Bottle Body
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.White, Color(0xFFF3F7FD))
                            )
                        )
                        .border(1.dp, Color(0xFFD7E5F8), RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Small cross on bottle label
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F2FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = BrandBlue,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }

        // Medicine Capsule Pill (Foreground Right)
        QwikMedsPillCapsule(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 42.dp, bottom = 22.dp),
            width = 46.dp,
            height = 18.dp,
            angle = -8f
        )

        // Soft Layered Ground Waves at Bottom
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .align(Alignment.BottomCenter)
        ) {
            val w = size.width
            val h = size.height

            val path = Path().apply {
                moveTo(0f, h * 0.4f)
                cubicTo(w * 0.35f, 0f, w * 0.65f, h * 0.7f, w, h * 0.2f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFEAF2FD).copy(alpha = 0.7f), Color(0xFFF8FAFD))
                )
            )
        }
    }
}

