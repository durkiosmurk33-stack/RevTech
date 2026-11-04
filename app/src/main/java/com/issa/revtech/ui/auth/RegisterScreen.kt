package com.issa.revtech.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.issa.revtech.navigation.ROUT_LOGIN
import com.issa.revtech.navigation.ROUT_REGISTER

private val ChuckyRed = Color(0xFFFF3030)
private val ChuckyBlack = Color(0xFF101012)
private val ChuckyCard = Color(0xFF1C1C21)
private val ChuckyMuted = Color(0xFFAAAAAF)

@Composable
fun RegisterScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = ChuckyRed,
        unfocusedBorderColor = Color.Gray,
        focusedLabelColor = ChuckyRed,
        unfocusedLabelColor = ChuckyMuted,
        cursorColor = ChuckyRed
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

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "JOIN THE\nRIDE.",
            color = Color.White,
            fontSize = 36.sp,
            lineHeight = 42.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Create your rider account to get started.",
            color = ChuckyMuted,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(26.dp))

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
                text = "CREATE ACCOUNT",
                color = ChuckyRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    errorMessage = ""
                },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = ""
                },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = ""
                },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = fieldColors,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = ""
                },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = fieldColors,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = errorMessage,
                    color = ChuckyRed,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    errorMessage = ""

                    when {
                        name.isBlank() ||
                                email.isBlank() ||
                                password.isBlank() ||
                                confirmPassword.isBlank() -> {
                            errorMessage = "Please fill in all fields."
                        }

                        password != confirmPassword -> {
                            errorMessage = "Passwords do not match."
                        }

                        password.length < 6 -> {
                            errorMessage =
                                "Password must be at least 6 characters."
                        }

                        else -> {
                            isLoading = true

                            auth.createUserWithEmailAndPassword(
                                email.trim(),
                                password
                            ).addOnCompleteListener { task ->
                                isLoading = false

                                if (task.isSuccessful) {
                                    navController.navigate(ROUT_LOGIN) {
                                        popUpTo(ROUT_REGISTER) {
                                            inclusive = true
                                        }
                                    }
                                } else {
                                    val error =
                                        task.exception?.message.orEmpty()

                                    errorMessage = when {
                                        error.contains("already", true) ->
                                            "Account already exists."

                                        error.contains("formatted", true) ||
                                                error.contains("email", true) ->
                                            "Please check your email address."

                                        error.contains("password", true) ->
                                            "Please check your password."

                                        error.contains("network", true) ->
                                            "Check your internet connection."

                                        else ->
                                            "Registration failed. Please try again."
                                    }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChuckyRed,
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "CREATE ACCOUNT  →",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    navController.navigate(ROUT_LOGIN)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Already a rider? Log in",
                    color = ChuckyMuted,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "REVTECH  •  RIDE YOUR OWN WAY",
            modifier = Modifier.fillMaxWidth(),
            color = Color.Gray,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    RegisterScreen(rememberNavController())
}