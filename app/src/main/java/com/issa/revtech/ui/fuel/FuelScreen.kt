package com.issa.revtech.ui.fuel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.issa.data.FuelLog
import com.issa.data.FuelStore


import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FuelScreen(navController: NavController) {

    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    var litres by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("You must be logged in to log fuel.", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Add Fuel Log", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = litres,
            onValueChange = { litres = it },
            label = { Text("Litres") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price (KES)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                message = ""
                isError = false

                if (litres.isBlank() || price.isBlank()) {
                    message = "Please fill all fields"
                    isError = true
                    return@Button
                }

                val litresDouble = litres.toDoubleOrNull()
                val priceDouble = price.toDoubleOrNull()

                if (litresDouble == null || priceDouble == null) {
                    message = "Litres and price must be valid numbers"
                    isError = true
                    return@Button
                }

                isLoading = true

                val timestamp = System.currentTimeMillis()
                val dateFormatted = SimpleDateFormat(
                    "dd MMM yyyy, HH:mm",
                    Locale.getDefault()
                ).format(Date(timestamp))

                val fuelData = hashMapOf(
                    "litres" to litresDouble,
                    "price" to priceDouble,
                    "userId" to user.uid,
                    "timestamp" to timestamp
                )

                db.collection("fuel_logs")
                    .add(fuelData)
                    .addOnSuccessListener {
                        isLoading = false
                        isError = false
                        message = "Fuel saved successfully"

                        // Save to local FuelStore for HistoryScreen
                        FuelStore.add(
                            FuelLog(
                                liters = litresDouble,
                                cost = priceDouble,
                                note = "",
                                date = dateFormatted
                            )
                        )

                        litres = ""
                        price = ""
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                        isError = true
                        message = e.message ?: "Failed to save fuel"
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
                Text("Save")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (message.isNotEmpty()) {
            Text(
                text = message,
                color = if (isError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FuelScreenPreview() {
    FuelScreen(rememberNavController())
}