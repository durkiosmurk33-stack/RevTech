package com.issa.revtech.ui.screens.splash

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.issa.revtech.R
import com.issa.revtech.navigation.ROUT_ONBOARDING
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@Composable
fun SplashScreen(navController: NavController) {

    var glitchOn by remember { mutableStateOf(false) }

    val transition = rememberInfiniteTransition(label = "glitch")

    val flickerAlpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, delayMillis = 120),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flicker"
    )

    LaunchedEffect(Unit) {
        repeat(5) {
            glitchOn = true
            delay(70)
            glitchOn = false
            delay(180)
        }

        delay(1800)
        navController.navigate(ROUT_ONBOARDING) {
            popUpTo(0) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Tiffany fills the screen
        Image(
            painter = painterResource(id = R.drawable.tiffany_bg),
            contentDescription = "Tiffany background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark overlay keeps the title readable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.38f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Chucky in the center
            Image(
                painter = painterResource(id = R.drawable.chucky_icon),
                contentDescription = "Chucky",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.68f)
                    .height(260.dp)
                    .offset {
                        IntOffset(
                            x = if (glitchOn) 5 else 0,
                            y = 0
                        )
                    }
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Cyan shadow shifts during glitch
            Box(contentAlignment = Alignment.Center) {
                if (glitchOn) {
                    Text(
                        text = "TIFFANY MADE ME DO IT",
                        color = Color.Cyan,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.offset(x = 3.dp)
                    )
                }

                Text(
                    text = "TIFFANY MADE ME DO IT",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .alpha(flickerAlpha)
                        .offset(x = if (glitchOn) (-3).dp else 0.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "ARYA • SYSTEM INITIALIZING",
                color = Color.Red,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(navController = rememberNavController())
}