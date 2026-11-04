package com.issa.revtech.ui.auth

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.issa.revtech.navigation.ROUT_REGISTER

private val ChuckyRed = Color(0xFFFF3030)
private val ChuckyBlack = Color(0xFF101012)
private val ChuckyCard = Color(0xFF1C1C21)
private val ChuckyMuted = Color(0xFFAAAAAF)

@Composable
fun LoginScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = ChuckyCard,
        unfocusedContainerColor = ChuckyCard,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = ChuckyRed,
        unfocusedLabelColor = ChuckyMuted,
        cursorColor = ChuckyRed,
        focusedIndicatorColor = ChuckyRed,
        unfocusedIndicatorColor = Color.Transparent
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChuckyBlack)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "CHUCKY // NAV",
            color = ChuckyRed,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 3.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "WELCOME\nBACK.",
            color = Color.White,
            fontSize = 36.sp,
            lineHeight = 42.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Sign in and get back on the road.",
            color = ChuckyMuted,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Login form card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = ChuckyCard,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {

            Text(
                text = "RIDER ACCESS",
                color = ChuckyRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Email address",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your email") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Password",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your password") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = fieldColors,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = errorMessage!!,
                    color = ChuckyRed,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Enter your email and password."
                        return@Button
                    }

                    loading = true
                    errorMessage = null

                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(
                            email.trim(),
                            password
                        )
                        .addOnCompleteListener { task ->
                            loading = false

                            if (task.isSuccessful) {
                                Log.d("LOGIN_FLOW", "LOGIN SUCCESS")

                                navController.navigate("home")
                            } else {
                                Log.e(
                                    "LOGIN_FLOW",
                                    "LOGIN FAILED",
                                    task.exception
                                )

                                errorMessage =
                                    task.exception?.localizedMessage
                                        ?: "Login failed. Please try again."
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !loading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChuckyRed,
                    contentColor = Color.White
                )
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "LET'S RIDE  →",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    navController.navigate(ROUT_REGISTER)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "New rider? Create an account",
                    color = ChuckyMuted,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "REVTECH  •  RIDE YOUR OWN WAY",
            modifier = Modifier.fillMaxWidth(),
            color = Color.Gray,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    LoginScreen(navController = rememberNavController())
}