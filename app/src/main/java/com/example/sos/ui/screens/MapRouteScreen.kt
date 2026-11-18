package com.example.sos.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

enum class RouteScreenState {
    MAP_OVERVIEW,      // Screen 02
    ROUTE_OPTIONS,     // Screen 03
    ACTIVE_NAVIGATION  // Screen 04
}

data class SuggestionLocation(
    val name: String,
    val address: String,
    val latLng: LatLng
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapRouteScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val defaultLocation = LatLng(20.5937, 78.9629) // Neutral default center
    var destinationLocation by remember { mutableStateOf(defaultLocation) }
    var destinationName by remember { mutableStateOf("Map Overview") }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 5f)
    }

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

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(hasLocationPermission) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        destinationLocation = currentLatLng
                        destinationName = "Current Location"
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f),
                                1000
                            )
                        }
                    }
                }
            } catch (e: SecurityException) {
                // ignore
            }
        }
    }

    var screenState by remember { mutableStateOf(RouteScreenState.MAP_OVERVIEW) }
    var selectedRouteOption by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }
    val mapTypes = remember {
        listOf(
            Triple(MapType.NORMAL, "Normal View", Color(0xFF0F172A)),
            Triple(MapType.SATELLITE, "Satellite View", Color(0xFF0F172A)),
            Triple(MapType.TERRAIN, "Terrain View", Color(0xFF0F172A)),
            Triple(MapType.HYBRID, "Hybrid View", Color(0xFF0F172A))
        )
    }
    var currentMapTypeIndex by remember { mutableIntStateOf(0) }
    var currentMapType by remember { mutableStateOf(MapType.NORMAL) }
    var nightViewText by remember { mutableStateOf("Night View") }



    val allSuggestions = remember {
        listOf(
            SuggestionLocation("Golden Temple", "Harmandir Sahib, Amritsar, Punjab", LatLng(31.6200, 74.8765)),
            SuggestionLocation("Jallianwala Bagh", "Golden Temple Rd, Amritsar, Punjab", LatLng(31.6190, 74.8790)),
            SuggestionLocation("Hall Bazaar", "Katra Ahluwalia, Amritsar, Punjab", LatLng(31.6280, 74.8720)),
            SuggestionLocation("Partition Museum", "Town Hall, Amritsar, Punjab", LatLng(31.6260, 74.8780)),
            SuggestionLocation("Powai Plaza", "JVLR Road, Central Ave, Hiranandani Gardens", LatLng(19.1190, 72.9050)),
            SuggestionLocation("Powai Lake", "Powai, Mumbai, Maharashtra, India", LatLng(19.1176, 72.9060)),
            SuggestionLocation("Powai Police Station", "4V9X+VQ4, DP Rd Number 9, Mumbai", LatLng(19.1150, 72.9100)),
            SuggestionLocation("New Delhi", "Capital of India", LatLng(28.6139, 77.2090)),
            SuggestionLocation("Mumbai", "Financial Capital, Maharashtra", LatLng(19.0760, 72.8777)),
            SuggestionLocation("Bangalore", "Karnataka, India", LatLng(12.9716, 77.5946)),
            SuggestionLocation("New York", "New York, USA", LatLng(40.7128, -74.0060)),
            SuggestionLocation("London", "United Kingdom", LatLng(51.5074, -0.1278)),
            SuggestionLocation("Tokyo", "Japan", LatLng(35.6762, 139.6503)),
            SuggestionLocation("Hospital", "Nearest Emergency Hospital", LatLng(defaultLocation.latitude + 0.003, defaultLocation.longitude + 0.003)),
            SuggestionLocation("Police Station", "Nearest Police Assistance", LatLng(defaultLocation.latitude - 0.003, defaultLocation.longitude - 0.003))
        )
    }

    fun performSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        val lower = trimmed.lowercase()

        val baseLatLng = cameraPositionState.position.target

        val match = allSuggestions.find { it.name.equals(trimmed, ignoreCase = true) || it.name.lowercase().contains(lower) || it.address.lowercase().contains(lower) }

        val targetLatLng = when {
            match != null -> {
                destinationName = match.name
                match.latLng
            }
            lower.contains("new york") || lower.contains("nyc") -> {
                destinationName = "New York, USA"
                LatLng(40.7128, -74.0060)
            }
            lower.contains("london") -> {
                destinationName = "London, UK"
                LatLng(51.5074, -0.1278)
            }
            lower.contains("paris") -> {
                destinationName = "Paris, France"
                LatLng(48.8566, 2.3522)
            }
            lower.contains("tokyo") -> {
                destinationName = "Tokyo, Japan"
                LatLng(35.6762, 139.6503)
            }
            lower.contains("sydney") -> {
                destinationName = "Sydney, Australia"
                LatLng(-33.8688, 151.2093)
            }
            lower.contains("dubai") -> {
                destinationName = "Dubai, UAE"
                LatLng(25.2048, 55.2708)
            }
            lower.contains("berlin") -> {
                destinationName = "Berlin, Germany"
                LatLng(52.5200, 13.4050)
            }
            lower.contains("rome") -> {
                destinationName = "Rome, Italy"
                LatLng(41.9028, 12.4964)
            }
            lower.contains("singapore") -> {
                destinationName = "Singapore"
                LatLng(1.3521, 103.8198)
            }
            lower.contains("toronto") -> {
                destinationName = "Toronto, Canada"
                LatLng(43.6532, -79.3832)
            }
            lower.contains("san francisco") -> {
                destinationName = "San Francisco, USA"
                LatLng(37.7749, -122.4194)
            }
            lower.contains("delhi") -> {
                destinationName = "New Delhi, India"
                LatLng(28.6139, 77.2090)
            }
            lower.contains("mumbai") -> {
                destinationName = "Mumbai, India"
                LatLng(19.0760, 72.8777)
            }
            lower.contains("hospital") -> {
                destinationName = trimmed.replaceFirstChar { it.uppercase() }
                LatLng(baseLatLng.latitude + 0.005, baseLatLng.longitude + 0.005)
            }
            lower.contains("police") || lower.contains("station") -> {
                destinationName = trimmed.replaceFirstChar { it.uppercase() }
                LatLng(baseLatLng.latitude - 0.005, baseLatLng.longitude - 0.005)
            }
            else -> {
                destinationName = trimmed.replaceFirstChar { it.uppercase() }
                val hash = trimmed.hashCode().toDouble()
                val latOffset = (hash % 50) / 100.0
                val lngOffset = ((hash / 50) % 50) / 100.0
                LatLng(baseLatLng.latitude + latOffset, baseLatLng.longitude + lngOffset)
            }
        }

        destinationLocation = targetLatLng
        searchQuery = destinationName
        isSearchFocused = false

        scope.launch {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(targetLatLng, 15f),
                1000
            )
        }
    }

    val filteredSuggestions = remember(searchQuery) {
        if (searchQuery.isBlank()) emptyList()
        else {
            val matching = allSuggestions.filter { 
                it.name.contains(searchQuery, ignoreCase = true) || it.address.contains(searchQuery, ignoreCase = true) 
            }
            val customOption = SuggestionLocation(searchQuery, "Search for \"$searchQuery\"", defaultLocation)
            listOf(customOption) + matching.filter { !it.name.equals(searchQuery, ignoreCase = true) }
        }
    }

    val routePoints = listOf(
        LatLng(destinationLocation.latitude - 0.005, destinationLocation.longitude - 0.005),
        LatLng(destinationLocation.latitude - 0.002, destinationLocation.longitude - 0.002),
        destinationLocation
    )

    when (screenState) {
        RouteScreenState.MAP_OVERVIEW -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Amritsar", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0F172A)) },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                            }
                        },
                        actions = {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Tune, contentDescription = "Options", tint = Color(0xFF0F172A))
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Official Google Map Composable with Current Location enabled
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(
                            isMyLocationEnabled = hasLocationPermission,
                            mapType = currentMapType
                        ),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            compassEnabled = true,
                            myLocationButtonEnabled = false,
                            scrollGesturesEnabled = true,
                            zoomGesturesEnabled = true
                        )
                    ) {
                        Marker(
                            state = MarkerState(position = destinationLocation),
                            title = destinationName,
                            snippet = "Safe Destination (Rating: 8.8/10)"
                        )
                        Polyline(
                            points = routePoints,
                            color = Color(0xFF10B981),
                            width = 12f
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top Search Bar & Auto-Suggestions Overlay
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                MapChip("Safe Route", Color(0xFF10B981), Color.White)
                                MapChip("City Safety", Color.White, Color(0xFF0F172A))
                                MapChip(nightViewText, Color.White, Color(0xFF0F172A)) {
                                    currentMapTypeIndex = (currentMapTypeIndex + 1) % mapTypes.size
                                    val current = mapTypes[currentMapTypeIndex]
                                    currentMapType = current.first
                                    nightViewText = current.second
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Search Field
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { 
                                    searchQuery = it
                                    isSearchFocused = true
                                },
                                placeholder = { Text("Search places or destinations...", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF64748B))
                                        }
                                    }
                                },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        if (searchQuery.isNotBlank()) {
                                            performSearch(searchQuery)
                                        }
                                    }
                                ),
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF2563EB),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(4.dp, RoundedCornerShape(24.dp))
                            )

                            // Auto-Suggestions Dropdown Overlay
                            if (isSearchFocused && filteredSuggestions.isNotEmpty()) {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 220.dp)
                                    ) {
                                        items(filteredSuggestions) { suggestion ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        performSearch(suggestion.name)
                                                    }
                                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                                                Column {
                                                    Text(text = suggestion.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                                                    Text(text = suggestion.address, fontSize = 12.sp, color = Color(0xFF64748B), maxLines = 1)
                                                }
                                            }
                                            HorizontalDivider(color = Color(0xFFF1F5F9))
                                        }
                                    }
                                }
                            }
                        }

                        // Bottom Card & GPS Current Location Button
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            // Current Location FAB Button
                            FloatingActionButton(
                                onClick = {
                                    if (hasLocationPermission) {
                                        try {
                                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                                if (location != null) {
                                                    scope.launch {
                                                        cameraPositionState.animate(
                                                            CameraUpdateFactory.newLatLngZoom(
                                                                LatLng(location.latitude, location.longitude),
                                                                16f
                                                            ),
                                                            1000
                                                        )
                                                    }
                                                } else {
                                                    scope.launch {
                                                        cameraPositionState.animate(
                                                            CameraUpdateFactory.newLatLngZoom(destinationLocation, 15f),
                                                            1000
                                                        )
                                                    }
                                                }
                                            }.addOnFailureListener {
                                                scope.launch {
                                                    cameraPositionState.animate(
                                                        CameraUpdateFactory.newLatLngZoom(destinationLocation, 15f),
                                                        1000
                                                    )
                                                }
                                            }
                                        } catch (e: SecurityException) {
                                            permissionLauncher.launch(
                                                arrayOf(
                                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                                )
                                            )
                                        }
                                    } else {
                                        permissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    }
                                },
                                containerColor = Color.White,
                                contentColor = Color(0xFF2563EB),
                                shape = CircleShape,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(Icons.Default.MyLocation, contentDescription = "My Location")
                            }

                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { screenState = RouteScreenState.ROUTE_OPTIONS }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(text = "Route to $destinationName", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                                            Text(text = "3 safer routes found", fontSize = 12.sp, color = Color(0xFF64748B))
                                        }
                                        IconButton(onClick = onBack, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                                        }
                                    }

                                    HorizontalDivider(color = Color(0xFFF1F5F9))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(text = "Safest Route", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF16A34A))
                                            Text(text = "86 / 100", fontSize = 13.sp, color = Color(0xFF64748B))
                                        }
                                        Button(
                                            onClick = { screenState = RouteScreenState.ROUTE_OPTIONS },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                            modifier = Modifier.height(44.dp)
                                        ) {
                                            Text(text = "Start", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        RouteIndicator(Icons.Default.Check, "Better lighting", Color(0xFF16A34A))
                                        RouteIndicator(Icons.Default.Check, "More public places", Color(0xFF16A34A))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        RouteScreenState.ROUTE_OPTIONS -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Route Options", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0F172A)) },
                        navigationIcon = {
                            IconButton(onClick = { screenState = RouteScreenState.MAP_OVERVIEW }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                    )
                },
                bottomBar = {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding(),
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Box(modifier = Modifier.padding(16.dp)) {
                            Button(
                                onClick = { screenState = RouteScreenState.ACTIVE_NAVIGATION },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Text(text = "Start Navigation", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF8FAFC))
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RouteOptionCard(
                        title = "Safest Route",
                        score = "86 / 100",
                        scoreColor = Color(0xFF16A34A),
                        timeDist = "14 min • 6.2 km",
                        isRecommended = true,
                        isSelected = selectedRouteOption == 0,
                        indicators = listOf(
                            Pair(true, "Better lighting"),
                            Pair(true, "More public places"),
                            Pair(true, "Police stations nearby")
                        ),
                        onClick = { selectedRouteOption = 0 }
                    )

                    RouteOptionCard(
                        title = "Balanced Route",
                        score = "72 / 100",
                        scoreColor = Color(0xFF2563EB),
                        timeDist = "12 min • 5.9 km",
                        isRecommended = false,
                        isSelected = selectedRouteOption == 1,
                        indicators = listOf(
                            Pair(true, "Moderate safety"),
                            Pair(false, "Some low activity areas")
                        ),
                        onClick = { selectedRouteOption = 1 }
                    )

                    RouteOptionCard(
                        title = "Fastest Route",
                        score = "58 / 100",
                        scoreColor = Color(0xFFEF4444),
                        timeDist = "10 min • 5.5 km",
                        isRecommended = false,
                        isSelected = selectedRouteOption == 2,
                        indicators = listOf(
                            Pair(false, "Few low activity areas"),
                            Pair(false, "Less lighting at night")
                        ),
                        onClick = { selectedRouteOption = 2 }
                    )
                }
            }
        }

        RouteScreenState.ACTIVE_NAVIGATION -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = hasLocationPermission,
                        mapType = currentMapType
                    ),
                    uiSettings = MapUiSettings(zoomControlsEnabled = false, compassEnabled = false, myLocationButtonEnabled = false)
                ) {
                    Marker(
                        state = MarkerState(position = destinationLocation),
                        title = destinationName
                    )
                    Polyline(
                        points = routePoints,
                        color = Color(0xFF2563EB),
                        width = 14f
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF10B981),
                            shadowElevation = 8.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Turn Right",
                                        tint = Color.White,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(text = "200 m", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
                                        Text(text = "Turn right onto Hall Bazaar Road", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                                    }
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            shadowElevation = 4.dp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                                Text(text = "$destinationName • 1.2 km", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { screenState = RouteScreenState.MAP_OVERVIEW },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9))
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Exit Navigation", tint = Color(0xFF0F172A))
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "14 min • 6.2 km", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                                Text(text = "Arrive 10:05 AM", fontSize = 12.sp, color = Color(0xFF16A34A))
                            }

                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9))
                            ) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Volume", tint = Color(0xFF0F172A))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RouteOptionCard(
    title: String,
    score: String,
    scoreColor: Color,
    timeDist: String,
    isRecommended: Boolean,
    isSelected: Boolean,
    indicators: List<Pair<Boolean, String>>,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
        border = if (isSelected) BorderStroke(2.dp, Color(0xFF2563EB)) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = isSelected, onClick = onClick)
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                            if (isRecommended) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        text = "Recommended",
                                        color = Color(0xFF16A34A),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = score, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = scoreColor)
                            Text(text = "•", fontSize = 13.sp, color = Color(0xFF94A3B8))
                            Text(text = timeDist, fontSize = 13.sp, color = Color(0xFF64748B))
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                indicators.forEach { (isPositive, text) ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isPositive) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (isPositive) Color(0xFF16A34A) else Color(0xFFEF4444),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(text = text, fontSize = 12.sp, color = Color(0xFF334155))
                    }
                }
            }
        }
    }
}

@Composable
fun MapChip(text: String, bgColor: Color, fgColor: Color, onClick: () -> Unit = {}) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        shadowElevation = 2.dp,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        TestLabel(text, fgColor)
    }
}

@Composable
fun TestLabel(text: String, fgColor: Color) {
    Text(
        text = text,
        color = fgColor,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    )
}

@Composable
fun RouteIndicator(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Text(text = text, fontSize = 12.sp, color = Color(0xFF334155))
    }
}
