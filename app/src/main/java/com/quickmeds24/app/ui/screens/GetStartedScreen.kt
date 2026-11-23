package com.quickmeds24.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmeds24.app.theme.*
import com.quickmeds24.app.ui.components.QwikMedsHeader
import com.quickmeds24.app.ui.components.QwikMedsPillCapsule

@Composable
fun GetStartedScreen(
    onContinueWithPhone: () -> Unit,
    onContinueWithEmail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Logo & Title matching Image 2
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 28.dp)
        ) {
            // Header with 4-lobed clover logo + QwikMeds
            QwikMedsHeader(
                emblemSize = 34.dp,
                textSize = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Get Started",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Text(
                text = "Login or register to order medicines in minutes",
                fontSize = 13.sp,
                color = Slate500,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        // Phone & Email Button Pills matching Image 2
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Phone Pill
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate200, RoundedCornerShape(18.dp))
                    .clickable { onContinueWithPhone() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = BrandBlue)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "Continue with Phone",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate400)
                }
            }

            // Email Pill
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate200, RoundedCornerShape(18.dp))
                    .clickable { onContinueWithEmail() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = BrandBlue)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "Continue with Email",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate400)
                }
            }
        }

        // 3D Capsule Pill Graphic matching Image 2
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            QwikMedsPillCapsule(
                width = 80.dp,
                height = 32.dp,
                angle = -35f
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "By continuing, you agree to our Terms of Service & Privacy Policy",
                fontSize = 11.sp,
                color = Slate400
            )
        }
    }
}
