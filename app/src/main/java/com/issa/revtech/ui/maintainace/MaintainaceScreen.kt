package com.issa.revtech.ui.maintainance

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color as AndroidColor
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

// -----------------------------------------------------
// CHUCKY // NAV THEME
// -----------------------------------------------------

private val ChuckyRed = Color(0xFFFF3030)
private val ChuckyBlack = Color(0xFF101012)
private val ChuckyCard = Color(0xFF1C1C21)
private val ChuckyMuted = Color(0xFFAAAAAF)

// -----------------------------------------------------
// DESTINATION DATA
// -----------------------------------------------------

data class DestinationResult(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

// -----------------------------------------------------
// ROUTE DATA
// -----------------------------------------------------

data class RouteOption(
    val coordinates: List<GeoPoint>,
    val distanceKm: Double,
    val durationMinutes: Double
)

// -----------------------------------------------------
// OPENSTREETMAP DESTINATION SEARCH
// -----------------------------------------------------

suspend fun searchDestinations(
    query: String
): List<DestinationResult> = withContext(Dispatchers.IO) {

    val encodedQuery = URLEncoder.encode(query, "UTF-8")

    val requestUrl = URL(
        "https://nominatim.openstreetmap.org/search" +
                "?format=jsonv2&limit=5&q=$encodedQuery"
    )

    val connection =
        requestUrl.openConnection() as HttpURLConnection

    try {
        connection.requestMethod = "GET"

        connection.setRequestProperty(
            "User-Agent",
            "RevTech-CHUCKY-NAV/1.0"
        )

        connection.connectTimeout = 10000
        connection.readTimeout = 10000

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            throw IOException("Search service unavailable")
        }

        val response = connection.inputStream
            .bufferedReader()
            .use { it.readText() }

        val jsonArray = JSONArray(response)

        buildList {
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)

                add(
                    DestinationResult(
                        name = item.getString("display_name"),
                        latitude = item.getDouble("lat"),
                        longitude = item.getDouble("lon")
                    )
                )
            }
        }
    } finally {
        connection.disconnect()
    }
}

// -----------------------------------------------------
// OSRM ROUTE CALCULATION
// -----------------------------------------------------

suspend fun calculateRoutes(
    origin: GeoPoint,
    destination: GeoPoint
): List<RouteOption> = withContext(Dispatchers.IO) {

    val start = "${origin.longitude},${origin.latitude}"
    val end = "${destination.longitude},${destination.latitude}"

    val requestUrl = URL(
        "https://router.project-osrm.org/route/v1/driving/" +
                "$start;$end" +
                "?alternatives=true&overview=full&geometries=geojson"
    )

    val connection =
        requestUrl.openConnection() as HttpURLConnection

    try {
        connection.requestMethod = "GET"

        connection.setRequestProperty(
            "User-Agent",
            "RevTech-CHUCKY-NAV/1.0"
        )

        connection.connectTimeout = 15000
        connection.readTimeout = 20000

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            throw IOException("Routing service unavailable")
        }

        val response = connection.inputStream
            .bufferedReader()
            .use { it.readText() }

        val json = JSONObject(response)

        if (json.optString("code") != "Ok") {
            throw IOException("No route found")
        }

        val routes = json.getJSONArray("routes")

        buildList {
            for (i in 0 until routes.length()) {

                val route = routes.getJSONObject(i)

                val distanceKm =
                    route.getDouble("distance") / 1000.0

                val durationMinutes =
                    route.getDouble("duration") / 60.0

                val coordinateArray = route
                    .getJSONObject("geometry")
                    .getJSONArray("coordinates")

                val points = mutableListOf<GeoPoint>()

                for (j in 0 until coordinateArray.length()) {
                    val coordinate =
                        coordinateArray.getJSONArray(j)

                    points.add(
                        GeoPoint(
                            coordinate.getDouble(1),
                            coordinate.getDouble(0)
                        )
                    )
                }

                add(
                    RouteOption(
                        coordinates = points,
                        distanceKm = distanceKm,
                        durationMinutes = durationMinutes
                    )
                )
            }
        }
    } finally {
        connection.disconnect()
    }
}

// -----------------------------------------------------
// MAIN SCREEN
// -----------------------------------------------------

@Composable
fun MaintenanceScreen(navController: NavController) {

    val context = LocalContext.current
    val searchScope = rememberCoroutineScope()

    // -------------------------------------------------
    // GPS PERMISSIONS
    // -------------------------------------------------

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
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
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (!hasLocationPermission) {
                gpsActive = false
            }
        }

    // -------------------------------------------------
    // SEARCH STATES
    // -------------------------------------------------

    var destinationQuery by remember {
        mutableStateOf("")
    }

    var searchResults by remember {
        mutableStateOf<List<DestinationResult>>(emptyList())
    }

    var isSearching by remember {
        mutableStateOf(false)
    }

    var searchError by remember {
        mutableStateOf<String?>(null)
    }

    var searchJob by remember {
        mutableStateOf<Job?>(null)
    }

    // -------------------------------------------------
    // ROUTE STATES
    // -------------------------------------------------

    var routeOptions by remember {
        mutableStateOf<List<RouteOption>>(emptyList())
    }

    var routeOverlays by remember {
        mutableStateOf<List<Polyline>>(emptyList())
    }

    var selectedRouteIndex by remember {
        mutableIntStateOf(0)
    }

    var isCalculatingRoute by remember {
        mutableStateOf(false)
    }

    var routeError by remember {
        mutableStateOf<String?>(null)
    }

    var routeJob by remember {
        mutableStateOf<Job?>(null)
    }

    // -------------------------------------------------
    // DIRECTIVE STATE
    // -------------------------------------------------

    var selectedDirective by remember {
        mutableStateOf("NONE")
    }

    // -------------------------------------------------
    // MAP INITIALIZATION
    // -------------------------------------------------

    val mapView = remember {
        Configuration.getInstance().userAgentValue =
            context.packageName

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

    // -------------------------------------------------
    // GPS OVERLAY
    // -------------------------------------------------

    val locationOverlay = remember(mapView) {
        MyLocationNewOverlay(
            GpsMyLocationProvider(context),
            mapView
        )
    }

    var destinationMarker by remember {
        mutableStateOf<Marker?>(null)
    }

    // -------------------------------------------------
    // CLEAR ROUTES
    // -------------------------------------------------

    fun clearRoutes() {

        routeJob?.cancel()
        routeJob = null

        routeOverlays.forEach { overlay ->
            mapView.overlays.remove(overlay)
        }

        routeOverlays = emptyList()
        routeOptions = emptyList()
        selectedRouteIndex = 0

        routeError = null
        isCalculatingRoute = false

        mapView.invalidate()
    }

    // -------------------------------------------------
    // CLEAR DESTINATION
    // -------------------------------------------------

    fun clearDestination() {

        destinationMarker?.let { marker ->
            mapView.overlays.remove(marker)
        }

        destinationMarker = null

        clearRoutes()

        mapView.invalidate()
    }

    // -------------------------------------------------
    // SEARCH ACTION
    // -------------------------------------------------

    fun performSearch() {

        val query = destinationQuery.trim()

        if (query.isBlank()) return

        searchJob?.cancel()

        searchJob = searchScope.launch {

            isSearching = true
            searchError = null
            searchResults = emptyList()

            try {

                val results = searchDestinations(query)

                currentCoroutineContext().ensureActive()

                if (destinationQuery.trim() == query) {

                    searchResults = results

                    if (results.isEmpty()) {
                        searchError = "No destinations found"
                    }
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                if (destinationQuery.trim() == query) {
                    searchError =
                        "Search failed. Check your internet connection."
                }

            } finally {

                if (destinationQuery.trim() == query) {
                    isSearching = false
                }
            }
        }
    }

    // -------------------------------------------------
    // SELECT DESTINATION
    // BOTH SEARCH AND LONG PRESS USE THIS FUNCTION
    // -------------------------------------------------

    fun selectDestination(result: DestinationResult) {

        val destinationPoint = GeoPoint(
            result.latitude,
            result.longitude
        )

        // Remove any previous destination and route.
        clearDestination()

        val marker = Marker(mapView).apply {

            position = destinationPoint
            title = result.name

            setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            )
        }

        destinationMarker = marker

        mapView.overlays.add(marker)

        mapView.controller.setZoom(15.0)
        mapView.controller.animateTo(destinationPoint)

        mapView.invalidate()

        destinationQuery = result.name
        searchResults = emptyList()
        searchError = null

        // Calculate route from current GPS position.
        routeJob = searchScope.launch {

            isCalculatingRoute = true
            routeError = null

            try {

                val origin = locationOverlay.myLocation
                    ?: throw IllegalStateException(
                        "Current GPS location unavailable"
                    )

                val calculatedRoutes = calculateRoutes(
                    origin,
                    destinationPoint
                ).sortedBy { it.durationMinutes }

                currentCoroutineContext().ensureActive()

                if (calculatedRoutes.isEmpty()) {
                    throw IOException("No route found")
                }

                routeOptions = calculatedRoutes
                selectedRouteIndex = 0

                val newOverlays =
                    calculatedRoutes.mapIndexed { index, route ->

                        Polyline(mapView).apply {

                            setPoints(route.coordinates)

                            outlinePaint.color =
                                if (index == 0) {
                                    AndroidColor.parseColor("#FFFF3030")
                                } else {
                                    AndroidColor.parseColor("#FF777777")
                                }

                            outlinePaint.strokeWidth =
                                if (index == 0) 12f else 7f
                        }
                    }

                routeOverlays = newOverlays

                newOverlays.forEach { overlay ->
                    mapView.overlays.add(overlay)
                }

                // Keep the destination marker above route lines.
                mapView.overlays.remove(marker)
                mapView.overlays.add(marker)

                mapView.invalidate()

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                routeError =
                    e.message ?: "Unable to calculate route"

            } finally {

                isCalculatingRoute = false
            }
        }
    }

    // -------------------------------------------------
    // LONG-PRESS MAP HANDLER
    // -------------------------------------------------

    val currentLongPressHandler by rememberUpdatedState<(GeoPoint) -> Unit> {

            point ->

        selectDestination(
            DestinationResult(
                name = "Dropped pin",
                latitude = point.latitude,
                longitude = point.longitude
            )
        )
    }

    val mapEventsOverlay = remember(mapView) {

        MapEventsOverlay(
            object : MapEventsReceiver {

                override fun singleTapConfirmedHelper(
                    point: GeoPoint
                ): Boolean {
                    return false
                }

                override fun longPressHelper(
                    point: GeoPoint
                ): Boolean {

                    currentLongPressHandler(point)

                    return true
                }
            }
        )
    }

    // -------------------------------------------------
    // GPS LIFECYCLE
    // -------------------------------------------------

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

    // -------------------------------------------------
    // MAP EVENTS LIFECYCLE
    // -------------------------------------------------

    DisposableEffect(mapView, mapEventsOverlay) {

        mapView.overlays.add(mapEventsOverlay)

        onDispose {
            mapView.overlays.remove(mapEventsOverlay)
        }
    }

    // -------------------------------------------------
    // MAP LIFECYCLE
    // -------------------------------------------------

    DisposableEffect(mapView) {

        mapView.onResume()

        onDispose {

            searchJob?.cancel()
            routeJob?.cancel()

            mapView.onPause()
            mapView.onDetach()
        }
    }

    // -------------------------------------------------
    // SCREEN LAYOUT
    // -------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChuckyBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp)
            .padding(top = 8.dp, bottom = 8.dp)
    ) {

        // ---------------------------------------------
        // HEADER
        // ---------------------------------------------

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

                color = if (gpsActive) {
                    Color(0xFF39C875)
                } else {
                    ChuckyRed
                },

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
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---------------------------------------------
        // DESTINATION SEARCH
        // ---------------------------------------------

        OutlinedTextField(
            value = destinationQuery,

            onValueChange = { newValue ->

                destinationQuery = newValue

                searchJob?.cancel()
                searchJob = null

                isSearching = false

                clearDestination()

                searchResults = emptyList()
                searchError = null
            },

            modifier = Modifier.fillMaxWidth(),

            singleLine = true,

            enabled = !isCalculatingRoute,

            placeholder = {
                Text(
                    "Where are we going, Doll?",
                    color = ChuckyMuted,
                    fontSize = 13.sp
                )
            },

            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search destination",
                    tint = ChuckyRed
                )
            },

            trailingIcon = {

                if (destinationQuery.isNotEmpty()) {

                    IconButton(
                        onClick = {

                            searchJob?.cancel()
                            searchJob = null

                            destinationQuery = ""

                            isSearching = false
                            searchResults = emptyList()
                            searchError = null

                            clearDestination()
                        }
                    ) {

                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = ChuckyMuted
                        )
                    }
                }
            },

            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),

            keyboardActions = KeyboardActions(
                onSearch = {
                    performSearch()
                }
            ),

            shape = RoundedCornerShape(14.dp),

            colors = OutlinedTextFieldDefaults.colors(

                focusedBorderColor = ChuckyRed,

                unfocusedBorderColor =
                    ChuckyMuted.copy(alpha = 0.4f),

                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,

                cursorColor = ChuckyRed,

                focusedContainerColor = ChuckyCard,
                unfocusedContainerColor = ChuckyCard
            )
        )

        // ---------------------------------------------
        // SEARCH RESULTS
        // ---------------------------------------------

        if (
            isSearching ||
            searchResults.isNotEmpty() ||
            searchError != null
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .background(
                        ChuckyCard,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(10.dp)
            ) {

                when {

                    isSearching -> {

                        Text(
                            "SEARCHING DESTINATIONS...",
                            color = ChuckyRed,
                            fontSize = 11.sp
                        )
                    }

                    searchError != null -> {

                        Text(
                            searchError ?: "",
                            color = ChuckyMuted,
                            fontSize = 12.sp
                        )
                    }

                    else -> {

                        Text(
                            "SEARCH RESULTS",
                            color = ChuckyRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Column(
                            modifier = Modifier
                                .heightIn(max = 160.dp)
                                .verticalScroll(
                                    rememberScrollState()
                                )
                        ) {

                            searchResults.forEach { result ->

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {

                                            selectDestination(result)
                                        }
                                        .padding(vertical = 10.dp),

                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        tint = ChuckyRed
                                    )

                                    Spacer(
                                        modifier = Modifier.width(10.dp)
                                    )

                                    Text(
                                        result.name,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        maxLines = 2
                                    )
                                }

                                HorizontalDivider(
                                    color = ChuckyMuted.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---------------------------------------------
        // MAP FRAME
        // ---------------------------------------------

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

                factory = {
                    mapView
                },

                update = { map ->

                    // Keep the long-press listener on top
                    // of the map's other overlays.
                    map.overlays.remove(mapEventsOverlay)
                    map.overlays.add(mapEventsOverlay)
                }
            )

            // RECENTER BUTTON

            Button(
                onClick = {

                    locationOverlay.myLocation?.let { location ->

                        mapView.controller.animateTo(location)

                    } ?: run {

                        if (!hasLocationPermission) {

                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
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

            // MAP LABEL

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

            // LONG-PRESS HINT

            Text(
                "LONG-PRESS MAP TO DROP PIN",

                color = ChuckyMuted,

                fontSize = 9.sp,

                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(10.dp)
                    .background(
                        ChuckyBlack.copy(alpha = 0.85f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(
                        horizontal = 10.dp,
                        vertical = 7.dp
                    )
            )
        }

        // ---------------------------------------------
        // ROUTE INFORMATION PANEL
        // ---------------------------------------------

        if (
            isCalculatingRoute ||
            routeOptions.isNotEmpty() ||
            routeError != null
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .background(
                        ChuckyCard,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {

                when {

                    isCalculatingRoute -> {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = ChuckyRed,
                                strokeWidth = 2.dp
                            )

                            Spacer(
                                modifier = Modifier.width(10.dp)
                            )

                            Text(
                                "CALCULATING ROUTE...",
                                color = ChuckyRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    routeError != null -> {

                        Text(
                            routeError ?: "Route unavailable",
                            color = ChuckyRed,
                            fontSize = 11.sp
                        )
                    }

                    routeOptions.isNotEmpty() -> {

                        val selectedRoute =
                            routeOptions.getOrNull(selectedRouteIndex)

                        if (selectedRoute != null) {

                            Text(
                                "ROUTE OPTIONS",
                                color = ChuckyRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(6.dp)
                            )

                            Text(
                                String.format(
                                    "%.1f km  •  %.0f min",
                                    selectedRoute.distanceKm,
                                    selectedRoute.durationMinutes
                                ),

                                color = Color.White,

                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                if (selectedRouteIndex == 0) {
                                    "FASTEST AVAILABLE ROUTE"
                                } else {
                                    "ALTERNATIVE ROUTE SELECTED"
                                },

                                color = ChuckyMuted,
                                fontSize = 9.sp
                            )

                            Spacer(
                                modifier = Modifier.height(6.dp)
                            )

                            routeOptions.forEachIndexed { index, route ->

                                TextButton(
                                    onClick = {

                                        selectedRouteIndex = index

                                        routeOverlays.forEachIndexed {
                                                overlayIndex,
                                                overlay ->

                                            overlay.outlinePaint.color =
                                                if (overlayIndex == index) {
                                                    AndroidColor.parseColor(
                                                        "#FFFF3030"
                                                    )
                                                } else {
                                                    AndroidColor.parseColor(
                                                        "#FF777777"
                                                    )
                                                }

                                            overlay.outlinePaint.strokeWidth =
                                                if (overlayIndex == index) {
                                                    12f
                                                } else {
                                                    7f
                                                }
                                        }

                                        mapView.invalidate()
                                    },

                                    modifier = Modifier.fillMaxWidth()
                                ) {

                                    Text(
                                        text = String.format(
                                            "%s  •  %.1f km  •  %.0f min",

                                            if (index == 0) {
                                                "FASTEST"
                                            } else {
                                                "ALTERNATIVE $index"
                                            },

                                            route.distanceKm,
                                            route.durationMinutes
                                        ),

                                        color = if (
                                            selectedRouteIndex == index
                                        ) {
                                            ChuckyRed
                                        } else {
                                            ChuckyMuted
                                        },

                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ---------------------------------------------
        // QUICK DIRECTIVES
        // ---------------------------------------------

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

                horizontalArrangement =
                    Arrangement.SpaceEvenly
            ) {

                DirectiveButton(
                    title = "FUEL",
                    selected = selectedDirective == "FUEL",
                    onClick = {
                        selectedDirective = "FUEL"
                    }
                ) {

                    Icon(
                        Icons.Default.LocalGasStation,
                        contentDescription = "Fuel"
                    )
                }

                DirectiveButton(
                    title = "REPAIR",
                    selected = selectedDirective == "REPAIR",
                    onClick = {
                        selectedDirective = "REPAIR"
                    }
                ) {

                    Icon(
                        Icons.Default.Build,
                        contentDescription = "Repair"
                    )
                }

                DirectiveButton(
                    title = "PARK",
                    selected = selectedDirective == "PARK",
                    onClick = {
                        selectedDirective = "PARK"
                    }
                ) {

                    Icon(
                        Icons.Default.LocalParking,
                        contentDescription = "Parking"
                    )
                }

                DirectiveButton(
                    title = "REST",
                    selected = selectedDirective == "REST",
                    onClick = {
                        selectedDirective = "REST"
                    }
                ) {

                    Icon(
                        Icons.Default.LocalCafe,
                        contentDescription = "Rest"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ---------------------------------------------
        // BOTTOM STATUS
        // ---------------------------------------------

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    ChuckyCard,
                    RoundedCornerShape(14.dp)
                )
                .padding(14.dp),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column {

                Text(
                    if (selectedDirective == "NONE") {

                        if (destinationMarker == null) {
                            "MAP READY"
                        } else {
                            "DESTINATION SELECTED"
                        }

                    } else {
                        "$selectedDirective SELECTED"
                    },

                    color = ChuckyRed,

                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    when {

                        routeError != null ->
                            "Route unavailable"

                        isCalculatingRoute ->
                            "Finding your route..."

                        routeOptions.isNotEmpty() ->
                            "Route calculation complete"

                        else ->
                            "GPS navigation ready"
                    },

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

// -----------------------------------------------------
// DIRECTIVE BUTTON
// -----------------------------------------------------

@Composable
private fun DirectiveButton(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {

    val iconColor =
        if (selected) ChuckyRed else ChuckyMuted

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,

        modifier = Modifier.width(72.dp)
    ) {

        IconButton(
            onClick = onClick
        ) {

            CompositionLocalProvider(
                LocalContentColor provides iconColor
            ) {
                icon()
            }
        }

        Text(
            title,

            color = if (selected) {
                ChuckyRed
            } else {
                ChuckyMuted
            },

            fontSize = 10.sp,

            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            }
        )
    }
}

// -----------------------------------------------------
// PREVIEW
// -----------------------------------------------------

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun MaintenanceScreenPreview() {

    MaintenanceScreen(
        navController = rememberNavController()
    )
}