package com.issa.revtech.ui.maintainance

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

@Composable
fun MaintenanceScreen(navController: NavController) {

    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    var serviceType by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }      // ✅ FIX 1: was imported from android.R.id instead of declared as state
    var isError by remember { mutableStateOf(false) }   // ✅ FIX 2: track error vs success
    var isLoading by remember { mutableStateOf(false) }

    // ✅ FIX 3: guard against unauthenticated access
    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("You must be logged in.", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Maintenance Tracker",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = serviceType,
            onValueChange = { serviceType = it },
            label = { Text("Service Type (e.g Oil change)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = cost,
            onValueChange = { cost = it },
            label = { Text("Cost (Ksh)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal) // ✅ FIX 4: numeric keyboard for cost
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                message = ""
                isError = false

                // ✅ FIX 5: was using undefined variable "service" — correct name is "serviceType"
                if (serviceType.isBlank() || cost.isBlank()) {
                    message = "Please fill all fields"
                    isError = true
                    return@Button
                }

                // ✅ FIX 6: validate cost is a real number before saving
                val costDouble = cost.toDoubleOrNull()
                if (costDouble == null) {
                    message = "Cost must be a valid number"
                    isError = true
                    return@Button
                }

                isLoading = true

                val maintenanceData = hashMapOf(
                    "serviceType" to serviceType,       // ✅ FIX 5: was "service" which doesn't exist
                    "cost" to costDouble,               // ✅ FIX 6: stored as Double not String
                    "notes" to notes,
                    "userId" to user.uid,               // ✅ FIX 7: safe — user is non-null here
                    "timestamp" to System.currentTimeMillis()
                )

                db.collection("maintenance_logs")
                    .add(maintenanceData)
                    .addOnSuccessListener {
                        isLoading = false
                        isError = false
                        message = "Maintenance saved successfully"
                        serviceType = ""                // ✅ FIX 5: was resetting "service" which doesn't exist
                        cost = ""
                        notes = ""
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                        isError = true
                        message = e.message ?: "Failed to save data"  // ✅ FIX 8: show real error
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
                Text("Save Maintenance")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ✅ FIX 9: message was never displayed
        if (message.isNotEmpty()) {
            Text(
                text = message,
                color = if (isError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Recent Maintenance",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "No records yet",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MaintenanceScreenPreview() {
    MaintenanceScreen(rememberNavController())
}