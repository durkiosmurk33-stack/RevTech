package com.issa.revtech.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class HistoryItem(
    val title: String = "",
    val subtitle: String = "",
    val type: String = ""
)

@Composable
fun HistoryScreen(navController: NavController) {

    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    var historyList by remember { mutableStateOf(listOf<HistoryItem>()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {

        val tempList = mutableListOf<HistoryItem>()

        // Fuel Logs
        db.collection("fuel_logs")
            .whereEqualTo("userId", user?.uid)
            .get()
            .addOnSuccessListener { fuelResult ->

                for (document in fuelResult) {

                    val litres = document.get("litres").toString()
                    val price = document.get("price").toString()

                    tempList.add(
                        HistoryItem(
                            title = "Fuel Refill",
                            subtitle = "$litres Litres - KES $price",
                            type = "fuel"
                        )
                    )
                }

                // Maintenance Logs
                db.collection("maintenance_logs")
                    .whereEqualTo("userId", user?.uid)
                    .get()
                    .addOnSuccessListener { maintenanceResult ->

                        for (document in maintenanceResult) {

                            val service = document.get("service").toString()
                            val cost = document.get("cost").toString()

                            tempList.add(
                                HistoryItem(
                                    title = "Maintenance",
                                    subtitle = "$service - KES $cost",
                                    type = "maintenance"
                                )
                            )
                        }

                        historyList = tempList
                        loading = false
                    }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Ride History",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else if (historyList.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No history found")
            }

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(historyList) { item ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector =
                                    if (item.type == "fuel")
                                        Icons.Default.LocalGasStation
                                    else
                                        Icons.Default.Build,
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {

                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = item.subtitle,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HistoryScreen(navController = rememberNavController())
}