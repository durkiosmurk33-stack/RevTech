package com.issa.revtech.ui.screens.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text

import androidx.compose.ui.Alignment

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

import com.issa.revtech.navigation.ROUT_ONBOARDING3

@Composable
fun OnBoardingScreen2(navController: NavController){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🔧",
            fontSize = 70.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Manage Maintenance",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Never miss oil changes,\nservice dates or repairs",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                navController.navigate(ROUT_ONBOARDING3)
            }
        ) {
            Text("Next")
        }

    }

}

@Preview(showBackground = true)
@Composable
fun OnBoardingScreen2Preview() {

    OnBoardingScreen2(rememberNavController())

}

