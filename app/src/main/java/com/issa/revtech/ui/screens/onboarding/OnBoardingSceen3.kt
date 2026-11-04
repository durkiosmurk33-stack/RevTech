package com.issa.revtech.ui.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.issa.revtech.R
import com.issa.revtech.navigation.ROUT_LOGIN

private val ChuckyRed = Color(0xFFFF3030)
private val ChuckyWhite = Color(0xFFF5F0EE)
private val ChuckyNote = Color(0xE51A1517)

@Composable
fun OnBoardingScreen3(navController: NavController) {

    val transition = rememberInfiniteTransition(label = "chuckyPulse")

    val pulse by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // Background artwork
        Image(
            painter = painterResource(
                id = R.drawable.chucky_background
            ),
            contentDescription = "Chucky-inspired dark background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark overlay keeps the coded text readable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.48f),
                            Color.Black.copy(alpha = 0.78f),
                            Color.Black.copy(alpha = 0.92f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "🔪  ♡  ♫",
                fontSize = 30.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "YOU GOT ME KID...",
                color = ChuckyWhite,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )

            Text(
                text = "AM FUCKING INFAMOUS!",
                color = ChuckyRed,
                fontSize = 31.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = 38.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "YOU + ME + ARYA ♡",
                color = ChuckyWhite,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Text(
                text = "Same chaos. Different day.",
                color = ChuckyRed,
                fontSize = 14.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Chucky quote notes
            ChuckyQuote(
                doodle = "✎  🩸",
                quote = "“There's worse things than death. Believe me.”",
                label = "— CHUCKY"
            )

            Spacer(modifier = Modifier.height(12.dp))

            ChuckyQuote(
                doodle = "☠️  ✕",
                quote = "“Fucking Andy!”",
                label = "— CHUCKY, LOSING HIS PATIENCE"
            )

            Spacer(modifier = Modifier.height(12.dp))

            ChuckyQuote(
                doodle = "♜  🔪",
                quote = "“Ade due damballa… give me the power!”",
                label = "— THE CLASSIC INVOCATION"
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "MORE RIDES. MORE MUSIC. MORE CHAOS.",
                color = ChuckyRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Whatever the road brings,\nwe keep moving our own way.",
                color = ChuckyWhite,
                fontSize = 15.sp,
                lineHeight = 23.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Animated red button
            Button(
                onClick = {
                    navController.navigate(ROUT_LOGIN)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .scale(pulse),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChuckyRed,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "LET'S RIDE  →",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    navController.navigate(ROUT_LOGIN)
                }
            ) {
                Text(
                    text = "Already have an account? Log in",
                    color = ChuckyWhite.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "CHUCKY APPROVES. ♡",
                color = ChuckyRed,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun ChuckyQuote(
    doodle: String,
    quote: String,
    label: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ChuckyNote,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = ChuckyRed.copy(alpha = 0.65f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = doodle,
                fontSize = 19.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = quote,
                color = ChuckyWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 23.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                color = ChuckyRed,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnBoardingScreen3Preview() {
    OnBoardingScreen3(rememberNavController())
}