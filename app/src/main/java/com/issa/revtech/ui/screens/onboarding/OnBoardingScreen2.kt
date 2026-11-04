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
import com.issa.revtech.navigation.ROUT_ONBOARDING3

private val ChuckyRed = Color(0xFFFF3030)
private val ChuckyBlack = Color(0xFF101012)
private val ChuckyCard = Color(0xFF202025)
private val ChuckyMuted = Color(0xFFB0B0B8)

@Composable
fun OnBoardingScreen2(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChuckyBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "🔪  ♫  🔪",
            fontSize = 34.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "CHUCKY'S",
            color = ChuckyRed,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 5.sp
        )

        Text(
            text = "Lucky Tracks",
            color = Color.White,
            fontSize = 34.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Your ride. Your rhythm. Your rules.",
            color = ChuckyMuted,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Music player preview card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = ChuckyCard,
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Album artwork placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(155.dp)
                        .background(
                            color = Color(0xFF351619),
                            shape = RoundedCornerShape(18.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💀  🎵  🔪",
                        fontSize = 42.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "THE KILLER PLAYLIST",
                    color = ChuckyRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ride Till The End",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Chucky's Mixtape",
                    color = ChuckyMuted,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Decorative progress bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .background(
                            Color.DarkGray,
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.42f)
                            .fillMaxHeight()
                            .background(
                                ChuckyRed,
                                RoundedCornerShape(8.dp)
                            )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⏮️", color = Color.White, fontSize = 26.sp)
                    Text("🔪", fontSize = 22.sp)

                    Surface(
                        color = ChuckyRed,
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = "▶️",
                            color = Color.White,
                            fontSize = 25.sp,
                            modifier = Modifier.padding(
                                horizontal = 22.dp,
                                vertical = 12.dp
                            )
                        )
                    }

                    Text("♫", color = Color.White, fontSize = 25.sp)
                    Text("⏭️", color = Color.White, fontSize = 26.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "YOUR SOUNDTRACK STARTS HERE",
                    color = ChuckyMuted,
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "LET THE BEAT FOLLOW YOUR RIDE.",
            color = ChuckyRed,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                navController.navigate(ROUT_ONBOARDING3)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ChuckyRed,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "NEXT  ➜",
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnBoardingScreen2Preview() {
    OnBoardingScreen2(rememberNavController())
}