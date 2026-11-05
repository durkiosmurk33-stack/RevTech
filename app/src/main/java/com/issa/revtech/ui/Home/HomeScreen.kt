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
import com.issa.revtech.navigation.ROUT_PROFILE

private val RacingOrange = Color(0xFFFF6D00)
private val RacingBackground = Color(0xFF101114)
private val RacingCard = Color(0xFF1B1D22)
private val RacingMuted = Color(0xFF9EA3AD)
private val RacingWhite = Color(0xFFF5F5F5)
private val RacingGreen = Color(0xFF39C875)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = RacingBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "REVTECH",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = RacingWhite,
                            letterSpacing = 2.sp
                        )
                        Text(
                            "RIDER DASHBOARD",
                            fontSize = 10.sp,
                            color = RacingMuted,
                            letterSpacing = 1.5.sp
                        )
                    }
                },
                navigationIcon = {
                    Icon(
                        Icons.Default.TwoWheeler,
                        contentDescription = "Motorcycle",
                        tint = RacingOrange,
                        modifier = Modifier.padding(start = 16.dp, end = 10.dp)
                    )
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.NotificationsNone,
                            contentDescription = "Notifications",
                            tint = RacingWhite
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(RacingOrange)
                            .clickable {
                                navController.navigate(ROUT_PROFILE)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Open profile",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RacingBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            BikeStatusCard()

            SectionHeading("RIDE OVERVIEW", "YOUR MACHINE")

            FuelProgressCard(progress = 0.65f)

            ServiceCountdownCard(kmLeft = 320)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatBox("128 km", "Distance", Modifier.weight(1f))
                StatBox("5.2 L", "Fuel Used", Modifier.weight(1f))
                StatBox("KES 1,150", "Cost", Modifier.weight(1f))
            }

            SectionHeading("QUICK ACCESS", "YOUR SHORTCUTS")

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionTile(
                    title = "Music",
                    subtitle = "Coming soon",
                    icon = Icons.Default.MusicNote,
                    modifier = Modifier.weight(1f),
                    onClick = {}
                )
                ActionTile(
                    title = "Routes",
                    subtitle = "Coming soon",
                    icon = Icons.Default.Map,
                    modifier = Modifier.weight(1f),
                    onClick = {}
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionTile(
                    title = "Map View",
                    subtitle = "Don't get lost",
                    icon = Icons.Default.Map,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(ROUT_MAINTENANCE) }
                )
                ActionTile(
                    title = "Ride History",
                    subtitle = "Past records",
                    icon = Icons.Default.History,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(ROUT_HISTORY) }
                )
            }

            SectionHeading("BIKE STATUS", "STAY ROAD-READY")

            AlertCard(
                title = "Chain Check",
                message = "Needs lubrication soon",
                icon = Icons.Default.Warning
            )

            OutlinedButton(
                onClick = { navController.navigate(ROUT_FUEL) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = RacingOrange
                )
            ) {
                Icon(Icons.Default.LibraryMusic, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("MUSIC")
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SectionHeading(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            subtitle,
            fontSize = 10.sp,
            color = RacingOrange,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
        Text(
            title,
            fontSize = 16.sp,
            color = RacingWhite,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun BikeStatusCard() {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = RacingCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "YOUR RIDE",
                        fontSize = 10.sp,
                        color = RacingOrange,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        "Arya",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = RacingWhite
                    )
                    Text(
                        "Jincheng 550cc",
                        fontSize = 12.sp,
                        color = RacingMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF302116)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.TwoWheeler,
                        contentDescription = "Arya motorcycle",
                        tint = RacingOrange,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            Spacer(Modifier.height(18.dp))
            HorizontalDivider(color = Color(0xFF34363D))
            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("LAST SERVICE", fontSize = 9.sp, color = RacingMuted)
                    Text(
                        "12 Aug 2025",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RacingWhite
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("RECORDED DISTANCE", fontSize = 9.sp, color = RacingMuted)
                    Text(
                        "128 km",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RacingWhite
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF173A2A))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        "ACTIVE",
                        color = RacingGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FuelProgressCard(progress: Float) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = RacingCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocalGasStation,
                        contentDescription = null,
                        tint = RacingOrange
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        " Minimum Fuel Overview",
                        color = RacingWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    "${(progress * 100).toInt()}%",
                    color = RacingOrange,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
                color = RacingOrange,
                trackColor = Color(0xFF36383E)
            )

            Spacer(Modifier.height(8.dp))
            Text(
                "Estimated fuel remaining",
                fontSize = 11.sp,
                color = RacingMuted
            )
        }
    }
}

@Composable
fun ServiceCountdownCard(kmLeft: Int) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF302116),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF4A2A12)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Build,
                    contentDescription = null,
                    tint = RacingOrange
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "UPDATED COMING FEATURE",
                    fontSize = 10.sp,
                    color = RacingOrange,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "$kmLeft km until next service",
                    color = RacingWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = RacingOrange
            )
        }
    }
}

@Composable
fun StatBox(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = RacingCard
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = RacingWhite
            )
            Spacer(Modifier.height(5.dp))
            Text(
                label,
                fontSize = 10.sp,
                color = RacingMuted
            )
        }
    }
}

@Composable
private fun ActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(116.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = RacingCard
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(Color(0xFF302116)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = RacingOrange
                )
            }

            Column {
                Text(
                    title,
                    fontSize = 13.sp,
                    color = RacingWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    subtitle,
                    fontSize = 10.sp,
                    color = RacingMuted
                )
            }
        }
    }
}

@Composable
fun AlertCard(title: String, message: String, icon: ImageVector) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = RacingCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = RacingOrange)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = RacingWhite
                )
                Spacer(Modifier.height(3.dp))
                Text(message, fontSize = 11.sp, color = RacingMuted)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF101114)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}