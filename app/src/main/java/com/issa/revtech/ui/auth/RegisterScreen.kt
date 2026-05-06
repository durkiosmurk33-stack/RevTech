package com.issa.revtech.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.issa.revtech.navigation.ROUT_LOGIN
import com.issa.revtech.navigation.ROUT_REGISTER

@Composable
fun RegisterScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }  // ✅ FIX 1: Was missing — caused "Unresolved reference: errorMessage"
    var isLoading by remember { mutableStateOf(false) }  // ✅ FIX 2: Added loading state for UX

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()  // ✅ FIX 3: Fields now fill width properly
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),  // ✅ FIX 4: Password was visible as plain text
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),  // ✅ FIX 4: Same fix for confirm password
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ✅ FIX 5: Error message was never displayed in the UI
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                errorMessage = ""

                if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    errorMessage = "Please fill all fields"
                    return@Button
                }

                if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                    return@Button
                }

                if (password.length < 6) {
                    errorMessage = "Password must be at least 6 characters"
                    return@Button
                }

                isLoading = true

                auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                    .addOnCompleteListener { task ->
                        isLoading = false

                        if (task.isSuccessful) {
                            navController.navigate(ROUT_LOGIN) {
                                popUpTo(ROUT_REGISTER) { inclusive = true }
                            }
                        } else {
                            val error = task.exception?.message ?: ""
                            errorMessage = when {
                                error.contains("already", true) -> "Account already exists"
                                error.contains("formatted", true) -> "Invalid email format"
                                error.contains("password", true) -> "Password must be at least 6 characters"
                                error.contains("network", true) -> "Check your internet connection"
                                else -> error
                            }
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Register")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = { navController.navigate(ROUT_LOGIN) }) {
            Text("Already have an account? Log in")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    RegisterScreen(rememberNavController())
}
