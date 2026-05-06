package com.issa.revtech.ui.auth

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.issa.revtech.navigation.ROUT_REGISTER

@Composable
fun LoginScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
        TextButton(onClick = {
            navController.navigate(ROUT_REGISTER)
        }) {
            Text("Don't have an account? Register")
        }


        Button(
            onClick = {


                Log.e("LOGIN_FLOW", "BUTTON CLICKED")

                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Email or password cannot be empty"
                    return@Button
                }

                loading = true

                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email.trim(), password.trim())
                    .addOnCompleteListener { task ->

                        loading = false

                        if (task.isSuccessful) {
                            Log.e("LOGIN_FLOW", "LOGIN SUCCESS")

                            navController.navigate("home")

                        } else {
                            val error = task.exception?.message
                            Log.e("LOGIN_FLOW", "LOGIN FAILED: $error")

                            errorMessage = error
                        }
                    }

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")

        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    LoginScreen(navController = rememberNavController())
}
