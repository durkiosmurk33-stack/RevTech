package com.issa.revtech.ui.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.issa.revtech.navigation.ROUT_FUEL
import com.issa.revtech.navigation.ROUT_MAINTENANCE
import com.issa.revtech.navigation.ROUT_HISTORY

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Revtech Dashboard", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Menu, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { navController.navigate("profile") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("A", fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.width(8.dp))
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            BikeStatusCard()

            Text("Live Stats", fontWeight = FontWeight.Medium, fontSize = 14.sp)

            FuelProgressCard(progress = 0.65f)

            ServiceCountdownCard(kmLeft = 320)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatBox("128 km", "Distance", Modifier.weight(1f))
                StatBox("5.2 L", "Fuel Used", Modifier.weight(1f))
                StatBox("KES 1,150", "Cost", Modifier.weight(1f))
            }

            Text("Quick Actions", fontWeight = FontWeight.Medium, fontSize = 14.sp)

            ActionItem(
                title = "Add Fuel",
                subtitle = "Log refuel",
                icon = Icons.Default.LocalGasStation,
                onClick = { navController.navigate(ROUT_FUEL) }
            )

            ActionItem(
                title = "Maintenance",
                subtitle = "Service record",
                icon = Icons.Default.Build,
                onClick = { navController.navigate(ROUT_MAINTENANCE) }
            )

            ActionItem(
                title = "Ride History",
                subtitle = "Trips log",
                icon = Icons.Default.DirectionsBike,
                onClick = { navController.navigate(ROUT_HISTORY) }
            )

            Text("Alerts", fontWeight = FontWeight.Medium, fontSize = 14.sp)

            AlertCard(
                title = "Chain Check",
                message = "Needs lubrication soon",
                icon = Icons.Default.Warning
            )
        }
    }
}

/* -------------------- BIKE STATUS -------------------- */

@Composable
fun BikeStatusCard() {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Arya", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Jincheng 550cc • Active",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatusChip("GOOD")
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Last Service", fontSize = 11.sp)
                    Text("12 Aug 2025", fontWeight = FontWeight.Medium)
                }

                Column {
                    Text("Mileage", fontSize = 11.sp)
                    Text("128 km", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

/* -------------------- FUEL PROGRESS -------------------- */

@Composable
fun FuelProgressCard(progress: Float) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {

            Text("Fuel Level", fontWeight = FontWeight.Medium)

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "${(progress * 100).toInt()}% remaining",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/* -------------------- SERVICE COUNTDOWN -------------------- */

@Composable
fun ServiceCountdownCard(kmLeft: Int) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {

            Text("Service Reminder", fontWeight = FontWeight.Medium)

            Spacer(Modifier.height(4.dp))

            Text(
                "$kmLeft km left until next service",
                fontSize = 13.sp
            )
        }
    }
}

/* -------------------- STATUS CHIP -------------------- */

@Composable
fun StatusChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF2E7D32))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, color = Color.White, fontSize = 11.sp)
    }
}

/* -------------------- STATS -------------------- */

@Composable
fun StatBox(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 11.sp)
        }
    }
}

/* -------------------- ACTION ITEM -------------------- */

@Composable
fun ActionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Medium)
                Text(subtitle, fontSize = 12.sp)
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

/* -------------------- ALERT -------------------- */

@Composable
fun AlertCard(title: String, message: String, icon: ImageVector) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)

            Spacer(Modifier.width(10.dp))

            Column {
                Text(title, fontWeight = FontWeight.Medium)
                Text(message, fontSize = 12.sp)
            }
        }
    }
}

/* -------------------- PREVIEW -------------------- */

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}