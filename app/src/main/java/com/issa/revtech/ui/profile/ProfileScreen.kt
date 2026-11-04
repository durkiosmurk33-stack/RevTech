package com.issa.revtech.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val RacingOrange = Color(0xFFFF6D00)
private val RacingBackground = Color(0xFF101114)
private val RacingCard = Color(0xFF1B1D22)
private val RacingMuted = Color(0xFF9EA3AD)
private val RacingWhite = Color(0xFFF5F5F5)

@Composable
fun ProfileScreen(navController: NavController) {
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }
    val user = auth.currentUser

    var name by remember { mutableStateOf("") }
    var bike by remember { mutableStateOf("Arya") }
    var status by remember { mutableStateOf("Active Rider") }
    var message by remember { mutableStateOf("Loading rider profile...") }

    LaunchedEffect(user?.uid) {
        if (user == null) {
            message = "Sign in to view your rider profile."
            return@LaunchedEffect
        }

        name = user.displayName.orEmpty()
        message = "Loading rider profile..."

        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    name = document.getString("name")
                        ?: user.displayName.orEmpty()
                    bike = document.getString("bike") ?: "Arya"
                    status = document.getString("status")
                        ?: "Active Rider"
                    message = ""
                } else {
                    message = "Your profile hasn't been saved yet."
                }
            }
            .addOnFailureListener {
                message = "Profile couldn't load. Check your connection or permissions."
            }
    }

    ProfileContent(
        name = name,
        email = user?.email ?: "Not signed in",
        uid = user?.uid ?: "Unavailable",
        bike = bike,
        status = status,
        message = message
    )
}

@Composable
private fun ProfileContent(
    name: String,
    email: String,
    uid: String,
    bike: String,
    status: String,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RacingBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "REVTECH",
                style = MaterialTheme.typography.labelLarge,
                color = RacingOrange,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "RIDER PROFILE",
                style = MaterialTheme.typography.headlineMedium,
                color = RacingWhite,
                fontWeight = FontWeight.Black
            )

            Text(
                text = "YOUR RIDE. YOUR RULES.",
                style = MaterialTheme.typography.labelMedium,
                color = RacingMuted
            )
        }

        // Rider identity card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = RacingCard
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF29221E),
                                RacingCard,
                                RacingCard
                            )
                        )
                    )
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(RacingOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Rider avatar",
                            tint = Color.Black,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "REGISTERED RIDER",
                            style = MaterialTheme.typography.labelSmall,
                            color = RacingOrange,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = name.ifBlank { "Name not set" },
                            style = MaterialTheme.typography.titleLarge,
                            color = RacingWhite,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = RacingOrange,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = status,
                                style = MaterialTheme.typography.bodySmall,
                                color = RacingMuted
                            )
                        }
                    }
                }

                HorizontalDivider(
                    color = Color(0xFF373940)
                )

                ProfileDetail(
                    icon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            tint = RacingOrange
                        )
                    },
                    label = "EMAIL ADDRESS",
                    value = email
                )

                ProfileDetail(
                    icon = {
                        Icon(
                            Icons.Default.DirectionsBike,
                            contentDescription = null,
                            tint = RacingOrange
                        )
                    },
                    label = "CURRENT MOTORCYCLE",
                    value = bike
                )
            }
        }

        // Account information
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "ACCOUNT DETAILS",
                style = MaterialTheme.typography.titleMedium,
                color = RacingWhite,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = RacingCard
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "FIREBASE USER ID",
                        style = MaterialTheme.typography.labelSmall,
                        color = RacingOrange,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = uid,
                        style = MaterialTheme.typography.bodySmall,
                        color = RacingWhite
                    )
                }
            }
        }

        // Loading, missing profile, or permission status
        if (message.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF29221E)
                )
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    color = RacingOrange,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Text(
            text = "RIDE SMART. RIDE REVTECH.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = RacingMuted,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun ProfileDetail(
    icon: @Composable () -> Unit,
    label: String,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF30251F)),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = RacingMuted
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = RacingWhite,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF101114)
@Composable
private fun ProfileContentPreview() {
    MaterialTheme {
        ProfileContent(
            name = "Issa",
            email = "issa@example.com",
            uid = "sample-user-id",
            bike = "Arya",
            status = "Active Rider",
            message = ""
        )
    }
}