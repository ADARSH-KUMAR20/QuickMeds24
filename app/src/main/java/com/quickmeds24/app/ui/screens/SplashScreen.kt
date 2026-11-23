package com.quickmeds24.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmeds24.app.theme.*
import com.quickmeds24.app.ui.components.QwikMedsHeroIllustration
import com.quickmeds24.app.ui.components.QwikMedsLogoEmblem

@Composable
fun SplashScreen(
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFF6FAFF),
                        Color(0xFFEBF3FF)
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Branding matching user image media_1795448937105_652c6829.jpg
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 44.dp)
        ) {
            // 4-Lobed Cloud Cross / Clover Emblem in Vibrant Blue
            QwikMedsLogoEmblem(size = 76.dp)

            Spacer(modifier = Modifier.height(16.dp))

            // Brand Typography in Deep Royal Navy
            Text(
                text = "QwikMeds",
                color = BrandNavy,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.6).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = "Medicines from\nNearby Pharmacies",
                color = Slate700,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        // 3D Storefront, Tote Bag, Bottle & Capsule Illustration
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            QwikMedsHeroIllustration(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(270.dp)
            )
        }

        // Bottom CTA Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onGetStarted,
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Delivery within 15 minutes • 100% Genuine Medicines",
                color = Slate400,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
