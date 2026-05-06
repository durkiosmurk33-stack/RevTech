package com.issa.revtech.ui.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.issa.revtech.navigation.ROUT_LOGIN

@Composable
fun OnBoardingScreen3(navController: NavController) {

    // Pulse animation for the emoji icon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F2027),
                        Color(0xFF203A43),
                        Color(0xFF2C5364)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Animated icon inside glowing circle
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF00C6FF).copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1A3A4A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📊",
                        fontSize = 52.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Step indicator dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (index == 2) 28.dp else 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (index == 2) Color(0xFF00C6FF)
                                else Color.White.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Title
            Text(
                text = "Smart Dashboard",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Subtitle
            Text(
                text = "Track fuel records, maintenance logs,\nand vehicle performance — all in one place.",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Feature chips row
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("⛽ Fuel", "🔧 Maintenance", "📈 Stats").forEach { label ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Get Started button
            Button(
                onClick = { navController.navigate(ROUT_LOGIN) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF00C6FF),
                                    Color(0xFF0072FF)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Get Started →",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Skip / already have account
            TextButton(onClick = { navController.navigate(ROUT_LOGIN) }) {
                Text(
                    text = "Already have an account? Log in",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnBoardingScreen3Preview() {
    OnBoardingScreen3(rememberNavController())
}