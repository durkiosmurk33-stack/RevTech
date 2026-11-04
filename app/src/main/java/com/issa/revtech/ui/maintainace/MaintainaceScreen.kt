package com.issa.revtech.ui.maintainance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.clickable
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

private val ChuckyRed = Color(0xFFFF3030)
private val ChuckyBlack = Color(0xFF101012)
private val ChuckyCard = Color(0xFF1C1C21)
private val ChuckyMuted = Color(0xFFAAAAAF)

@Composable
fun MaintenanceScreen(navController: NavController) {

    val context = LocalContext.current

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var gpsActive by remember {
        mutableStateOf(false)
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            hasLocationPermission =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            if (!hasLocationPermission) {
                gpsActive = false
            }
        }

    var selectedDirective by remember {
        mutableStateOf("NONE")
    }

    val mapView = remember {
        Configuration.getInstance().userAgentValue = context.packageName

        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            setBuiltInZoomControls(false)

            controller.setZoom(13.0)
            controller.setCenter(
                GeoPoint(-1.286389, 36.817223)
            )
        }
    }
    val locationOverlay = remember(mapView) {
        MyLocationNewOverlay(
            GpsMyLocationProvider(context),
            mapView
        )
    }
    DisposableEffect(mapView, hasLocationPermission) {

        if (hasLocationPermission) {

            mapView.overlays.add(locationOverlay)

            locationOverlay.enableMyLocation()

            locationOverlay.runOnFirstFix {
                mapView.post {
                    locationOverlay.myLocation?.let { location ->
                        mapView.controller.animateTo(location)
                        gpsActive = true
                    }
                }
            }
        }

        onDispose {
            locationOverlay.disableMyLocation()
            mapView.overlays.remove(locationOverlay)
            gpsActive = false
        }
    }

    DisposableEffect(mapView) {
        mapView.onResume()

        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChuckyBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp)
            .padding(top = 8.dp, bottom = 8.dp)
    ) {

        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    ChuckyCard,
                    RoundedCornerShape(14.dp)
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(
                    "CHUCKY // NAV",
                    color = ChuckyRed,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "WHERE TO, DOLL?",
                    color = ChuckyMuted,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )
            }

            Text(
                text = when {
                    gpsActive -> "GPS ACTIVE"
                    hasLocationPermission -> "LOCATING..."
                    else -> "GPS OFF"
                },
                color = if (gpsActive) Color(0xFF39C875) else ChuckyRed,
                fontSize = 11.sp,
                modifier = Modifier
                    .background(
                        ChuckyBlack,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        if (!hasLocationPermission) {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )

        }

        Spacer(modifier = Modifier.height(12.dp))

        // MAP FRAME
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(
                    width = 1.dp,
                    color = ChuckyRed.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(18.dp)
                )
        ) {

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { mapView }
            )
            Button(
                onClick = {
                    locationOverlay.myLocation?.let { location ->
                        mapView.controller.animateTo(location)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChuckyBlack,
                    contentColor = ChuckyRed
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
            ) {
                Text("⌖ RECENTER")
            }

            Text(
                "OPENSTREETMAP • NAIROBI",
                color = Color.White,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
                    .background(
                        ChuckyBlack.copy(alpha = 0.9f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(
                        horizontal = 12.dp,
                        vertical = 9.dp
                    )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // DIRECTIVE PANEL
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    ChuckyCard,
                    RoundedCornerShape(14.dp)
                )
                .padding(vertical = 10.dp)
        ) {

            Text(
                "QUICK DIRECTIVES",
                color = ChuckyMuted,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(
                    start = 16.dp,
                    bottom = 6.dp
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                DirectiveButton(
                    title = "FUEL",
                    selected = selectedDirective == "FUEL",
                    onClick = { selectedDirective = "FUEL" }
                ) {
                    Icon(
                        Icons.Default.LocalGasStation,
                        contentDescription = "Fuel"
                    )
                }

                DirectiveButton(
                    title = "REPAIR",
                    selected = selectedDirective == "REPAIR",
                    onClick = { selectedDirective = "REPAIR" }
                ) {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = "Repair"
                    )
                }

                DirectiveButton(
                    title = "PARK",
                    selected = selectedDirective == "PARK",
                    onClick = { selectedDirective = "PARK" }
                ) {
                    Icon(
                        Icons.Default.LocalParking,
                        contentDescription = "Parking"
                    )
                }

                DirectiveButton(
                    title = "REST",
                    selected = selectedDirective == "REST",
                    onClick = { selectedDirective = "REST" }
                ) {
                    Icon(
                        Icons.Default.LocalCafe,
                        contentDescription = "Rest"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // BOTTOM STATUS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    ChuckyCard,
                    RoundedCornerShape(14.dp)
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(
                    if (selectedDirective == "NONE")
                        "MAP READY"
                    else
                        "$selectedDirective SELECTED",
                    color = ChuckyRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    if (selectedDirective == "NONE")
                        "GPS navigation coming next"
                    else
                        "Location filtering coming next",
                    color = ChuckyMuted,
                    fontSize = 10.sp
                )
            }

            Text(
                "CHUCKY",
                color = ChuckyMuted,
                fontSize = 12.sp,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun DirectiveButton(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    val iconColor = if (selected) ChuckyRed else ChuckyMuted

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {

        IconButton(onClick = onClick) {
            CompositionLocalProvider(
                LocalContentColor provides iconColor
            ) {
                icon()
            }
        }

        Text(
            title,
            color = if (selected) ChuckyRed else ChuckyMuted,
            fontSize = 10.sp,
            fontWeight = if (selected)
                FontWeight.Bold
            else
                FontWeight.Normal
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MaintenanceScreenPreview() {
    MaintenanceScreen(
        navController = rememberNavController()
    )
}