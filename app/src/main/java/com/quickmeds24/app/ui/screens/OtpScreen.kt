package com.quickmeds24.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmeds24.app.theme.*
import kotlinx.coroutines.delay

@Composable
fun OtpScreen(
    phoneNumber: String,
    onVerifySuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 6 OTP digits matching Image 3: [4] [2] [8] [6] [1] [0]
    var digits by remember { mutableStateOf(listOf("4", "2", "8", "6", "1", "0")) }
    var secondsRemaining by remember { mutableStateOf(28) }

    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            IconButton(onClick = onBack, modifier = Modifier.offset(x = (-8).dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Slate800)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Enter OTP",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Text(
                    text = "We've sent a 6-digit OTP to",
                    fontSize = 13.sp,
                    color = Slate500,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Text(
                    text = phoneNumber,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate800,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 6 OTP Boxes matching Image 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                digits.forEachIndexed { index, digit ->
                    Box(
                        modifier = Modifier
                            .size(48.dp, 56.dp)
                            .background(Slate50, RoundedCornerShape(14.dp))
                            .border(1.dp, PrimaryBlue, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = digit,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Resend OTP in 00:${if (secondsRemaining < 10) "0$secondsRemaining" else secondsRemaining}",
                fontSize = 13.sp,
                color = Slate500,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Button(
            onClick = onVerifySuccess,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 12.dp)
        ) {
            Text(
                text = "Verify & Continue",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

