package com.issa.revtech.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.issa.revtech.navigation.ROUT_ONBOARDING2

private val ChuckyRed = Color(0xFFFF3030)
private val ChuckyBlack = Color(0xFF101012)
private val ChuckyCard = Color(0xFF1C1C21)
private val ChuckyMuted = Color(0xFFAAAAAF)

@Composable
fun OnBoardingScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ChuckyBlack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Doll's // NAV",
                color = ChuckyRed,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "CHOOSE YOUR ROUTE.\nWANNA PLAY.",
                color = Color.White,
                fontSize = 34.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Know your route. Own every mile.",
                color = ChuckyMuted,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Navigation-inspired feature card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = ChuckyCard,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(22.dp)
            ) {
                Text(
                    text = "⛽",
                    fontSize = 42.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Know your next fuel station",
                    color = Color.White,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Monitor every fuel station and distance. " +
                            "Keep every ride under control.",
                    color = ChuckyMuted,
                    fontSize = 15.sp,
                    lineHeight = 23.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Red route-style accent
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .background(
                                ChuckyRed,
                                RoundedCornerShape(50)
                            )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .width(8.dp)
                            .height(8.dp)
                            .background(
                                ChuckyRed,
                                RoundedCornerShape(50)
                            )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "RIDE YOUR OWN WAY",
                        color = ChuckyRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    navController.navigate(ROUT_ONBOARDING2)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChuckyRed,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "LET'S RIDE  →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "REVTECH  •  BUILT FOR THE ROAD",
                modifier = Modifier.fillMaxWidth(),
                color = Color.Gray,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnBoardingScreenPreview() {
    OnBoardingScreen(rememberNavController())
}