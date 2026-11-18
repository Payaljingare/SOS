package com.example.sos.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sos.R

// --- DATA MODELS ---

data class PoiItem(
    val name: String,
    val category: String,
    val distance: String,
    val score: String,
    val rating: String,
    val ratingColor: Color,
    val ratingBgColor: Color,
    val icon: ImageVector,
    val iconBgColor: Color,
    val iconTint: Color,
    val isTopPlace: Boolean = false
)

data class SafetyScoreDetail(
    val score: String,
    val label: String,
    val status: String,
    val statusColor: Color,
    val statusBg: Color,
    val icon: ImageVector,
    val iconTint: Color,
    val iconBg: Color
)

// --- MAIN ACTIVITY / NAV WRAPPER ---

@Composable
fun SafetyNavigationWrapper(
    onBack: () -> Unit = {}
) {
    var selectedPlace by remember { mutableStateOf<PoiItem?>(null) }

    if (selectedPlace != null) {
        PlaceSafetyDetailScreen(
            place = selectedPlace!!,
            onBack = { selectedPlace = null }
        )
    } else {
        CitySafetyScreen(
            onBack = onBack,
            onPlaceClick = { poi -> selectedPlace = poi }
        )
    }
}

// ---  CITY SAFETY ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySafetyScreen(
    onBack: () -> Unit,
    onPlaceClick: (PoiItem) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("All", "Top Places", "Attractions", "Eateries", "Shopping")

    val pois = listOf(
        PoiItem(
            "Golden Temple",
            "Religious Place",
            "1.2 km",
            "8.8",
            "High",
            Color(0xFF16A34A),
            Color(0xFFDCFCE7),
            Icons.Default.TempleBuddhist,
            Color(0xFFFEF3C7),
            Color(0xFFD97706),
            true
        ),
        PoiItem(
            "Jallianwala Bagh",
            "Historical Place",
            "1.9 km",
            "8.2",
            "Good",
            Color(0xFF16A34A),
            Color(0xFFDCFCE7),
            Icons.Default.History,
            Color(0xFFDCFCE7),
            Color(0xFF16A34A),
            true
        ),
        PoiItem(
            "Hall Bazaar",
            "Market",
            "2.1 km",
            "7.5",
            "Good",
            Color(0xFFD97706),
            Color(0xFFFEF3C7),
            Icons.Default.Storefront,
            Color(0xFFFFEDD5),
            Color(0xFFEA580C),
            true
        ),
        PoiItem(
            "Partition Museum",
            "Museum",
            "2.4 km",
            "7.8",
            "Good",
            Color(0xFF16A34A),
            Color(0xFFDCFCE7),
            Icons.Default.AccountBalance,
            Color(0xFFDBEAFE),
            Color(0xFF2563EB)
        ),
        PoiItem(
            "Ahuja Lassi",
            "Eatery",
            "2.6 km",
            "8.0",
            "Good",
            Color(0xFF16A34A),
            Color(0xFFDCFCE7),
            Icons.Default.Restaurant,
            Color(0xFFFCE7F3),
            Color(0xFFDB2777)
        ),
        PoiItem(
            "All India Pingalwara",
            "NGO / Social Service",
            "3.1 km",
            "8.6",
            "High",
            Color(0xFF16A34A),
            Color(0xFFDCFCE7),
            Icons.Default.VolunteerActivism,
            Color(0xFFF1F5F9),
            Color(0xFF64748B)
        ),
        PoiItem(
            "Amritsar Bus Stand",
            "Transport & Mobility",
            "3.5 km",
            "-",
            "Not assessed",
            Color(0xFF64748B),
            Color(0xFFF1F5F9),
            Icons.Default.DirectionsBus,
            Color(0xFFF1F5F9),
            Color(0xFF64748B)
        ),
        PoiItem(
            "Amritsar Hawai Adda",
            "Eatery",
            "4.0 km",
            "7.9",
            "Good",
            Color(0xFF16A34A),
            Color(0xFFDCFCE7),
            Icons.Default.Restaurant,
            Color(0xFFFEF3C7),
            Color(0xFFD97706)
        )
    )

    val filteredPois = remember(selectedCategory, searchQuery) {
        pois.filter { poi ->
            val matchesCategory = when (selectedCategory) {
                "All" -> true
                "Top Places" -> poi.isTopPlace
                "Attractions" -> poi.category.contains("Place", ignoreCase = true) || poi.category.contains("Historical", ignoreCase = true) || poi.category.contains("Museum", ignoreCase = true)
                "Eateries" -> poi.category.contains("Eatery", ignoreCase = true)
                "Shopping" -> poi.category.contains("Market", ignoreCase = true)
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() || poi.name.contains(searchQuery, ignoreCase = true) || poi.category.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            Column(Modifier.background(Color.White)) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "Safety in Amritsar",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                "Explore safety ratings for popular places across Amritsar.",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF0F172A)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = Color(0xFF0F172A)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
        ) {
            // Index Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF2FBF5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDCFCE7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Column {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        "8.2",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 32.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        " / 10",
                                        fontSize = 16.sp,
                                        color = Color(0xFF64748B),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                                Text(
                                    "City Safety Index",
                                    fontSize = 14.sp,
                                    color = Color(0xFF334155),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFDCFCE7)) {
                            Text(
                                "Good",
                                color = Color(0xFF16A34A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                    HorizontalDivider(
                        color = Color(0xFFE2E8F0),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SafetyStatItem("42", "Places indexed")
                        SafetyStatItem("37", "Assessed")
                        SafetyStatItem("5", "Not yet assessed")
                    }
                }
            }

            // Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = cat == selectedCategory
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Color(0xFF0F172A) else Color.White,
                        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.White else Color(0xFF334155),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Search
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search places in Amritsar...",
                            color = Color(0xFF94A3B8),
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF64748B)
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFFE2E8F0),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    singleLine = true
                )
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable {},
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Tune,
                        contentDescription = "Filter",
                        tint = Color(0xFF0F172A)
                    )
                }
            }

            // Table Header
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    "#",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(24.dp)
                )
                Text(
                    "Place",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "Category",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(72.dp)
                )
                Text(
                    "Safety Score",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(60.dp)
                )
                Text(
                    "Action",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(52.dp)
                )
            }
            HorizontalDivider(color = Color(0xFFE2E8F0))

            // List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(filteredPois) { index, poi ->
                    PoiRow(index + 1, poi) { onPlaceClick(poi) }
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                }
            }
        }
    }
}

@Composable
fun SafetyStatItem(value: String, label: String) {
    Column {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
        Text(label, fontSize = 11.sp, color = Color(0xFF64748B))
    }
}

@Composable
fun PoiRow(index: Int, poi: PoiItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "$index",
            fontSize = 13.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.width(24.dp)
        )

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(poi.iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    poi.icon,
                    contentDescription = null,
                    tint = poi.iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    poi.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (poi.isTopPlace) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            " Top Place",
                            fontSize = 10.sp,
                            color = Color(0xFFD97706),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Text(
            poi.category,
            fontSize = 11.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.width(72.dp),
            maxLines = 2,
            lineHeight = 14.sp
        )

        Column(Modifier.width(60.dp), horizontalAlignment = Alignment.Start) {
            Text(
                poi.score,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF0F172A)
            )
            if (poi.score != "-") {
                Surface(shape = RoundedCornerShape(4.dp), color = poi.ratingBgColor) {
                    Text(
                        poi.rating,
                        fontSize = 9.sp,
                        color = poi.ratingColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            } else {
                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFF1F5F9)) {
                    Text(
                        "Not assessed",
                        fontSize = 8.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .width(52.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEFF6FF))
                .clickable { onClick() }
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "View",
                fontSize = 11.sp,
                color = Color(0xFF2563EB),
                fontWeight = FontWeight.Medium
            )
        }
    }
}


// ---  PLACE DETAIL ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceSafetyDetailScreen(
    place: PoiItem,
    onBack: () -> Unit
) {
    val scores = listOf(
        SafetyScoreDetail(
            "8.8",
            "Overall Safety",
            "High",
            Color(0xFF16A34A),
            Color(0xFFDCFCE7),
            Icons.Default.VerifiedUser,
            Color(0xFF16A34A),
            Color(0xFFDCFCE7)
        ),
        SafetyScoreDetail(
            "8.4",
            "Women Safety",
            "Good",
            Color(0xFF16A34A),
            Color(0xFFDCFCE7),
            Icons.Default.Female,
            Color(0xFF16A34A),
            Color(0xFFDCFCE7)
        ),
        SafetyScoreDetail(
            "7.2",
            "Night Safety",
            "Moderate",
            Color(0xFFD97706),
            Color(0xFFFEF3C7),
            Icons.Default.DarkMode,
            Color(0xFFD97706),
            Color(0xFFFEF3C7)
        )
    )

    // Box layout completely replaces the Scaffold topBar approach to ensure perfect layering
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // 1. The Image Background (Anchored to the top)
        Image(
            painter = painterResource(id = R.drawable.golden_temple),
            contentDescription = "Golden Temple",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp) // Slightly taller to allow for overlap
        )

        // 2. The Top Action Buttons (Back, Save, Share) - floating over the image
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.background(Color.White.copy(alpha = 0.9f), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF0F172A)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = {},
                    modifier = Modifier.background(Color.White.copy(alpha = 0.9f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        contentDescription = "Save",
                        tint = Color(0xFF0F172A)
                    )
                }
                IconButton(
                    onClick = {},
                    modifier = Modifier.background(Color.White.copy(alpha = 0.9f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color(0xFF0F172A)
                    )
                }
            }
        }

        // 3. The Photo Count Pill (1/8) - placed at the bottom right of the image
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.Black.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 185.dp, end = 16.dp) // Positioned just above the white card
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
                Text(" 1/8", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }

        // 4. The White Content Card - starts 220.dp from the top, overlapping the image
        Surface(
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 220.dp) // Overlaps the 260.dp tall image by 40dp
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title & Status
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    place.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier
                                        .size(18.dp)
                                        .padding(start = 6.dp)
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 6.dp)
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    " Amritsar, Punjab",
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFDCFCE7)) {
                                Text(
                                    "Open Now",
                                    color = Color(0xFF16A34A),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                            Text(
                                "Closes at 10:00 PM",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B),
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }

                // Tags
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFF1F5F9)) {
                            Text(
                                place.category,
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                        if (place.isTopPlace) {
                            Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFFEF3C7)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        " Top Place",
                                        fontSize = 12.sp,
                                        color = Color(0xFFD97706),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // Safety Scores Header
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Safety Scores",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Info",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(start = 4.dp)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Data confidence: ", fontSize = 12.sp, color = Color(0xFF64748B))
                            Text(
                                "High",
                                fontSize = 12.sp,
                                color = Color(0xFF16A34A),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Scores Row
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        scores.forEach { score ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(score.iconBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                score.icon,
                                                contentDescription = null,
                                                tint = score.iconTint,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Text(
                                            " ${score.score} / 10",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                    }
                                    Text(
                                        score.label,
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B),
                                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = score.statusBg,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            score.status,
                                            color = score.statusColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Why this score
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFFDCFCE7)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    " Why this score?",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                            CheckListText("High crowd presence throughout the day")
                            CheckListText("Well-lit premises and surrounding areas")
                            CheckListText("Police presence and security management")
                            CheckListText("CCTV coverage")
                            CheckListText("Good public transport connectivity")
                        }
                    }
                }

                // Insights
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    " Insights & Recommendations",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                            Text(
                                "The Golden Temple is one of the most visited and well-maintained religious sites in India. It has continuous crowd presence, good security, and well-managed premises, making it generally safe for all visitors.",
                                fontSize = 13.sp,
                                color = Color(0xFF475569),
                                lineHeight = 18.sp
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFEF2F2),
                                border = BorderStroke(1.dp, Color(0xFFFEE2E2)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            " Things to keep in mind",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF0F172A),
                                            modifier = Modifier.padding(start = 6.dp)
                                        )
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    BulletText("Keep your belongings secure in crowded areas.")
                                    BulletText("Use designated entry and exit routes.")
                                    BulletText("Prefer official parking and transport options.")
                                    BulletText("Evening and night hours can be more crowded, so extra caution is advised.")
                                }
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

// --- HELPER COMPOSABLES ---

@Composable
fun CheckListText(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            Icons.Default.Check,
            contentDescription = null,
            tint = Color(0xFF16A34A),
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp, end = 6.dp)
        )
        Text(text, fontSize = 13.sp, color = Color(0xFF334155))
    }
}

@Composable
fun BulletText(text: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(bottom = 4.dp)) {
        Text("• ", fontSize = 13.sp, color = Color(0xFF64748B))
        Text(text, fontSize = 13.sp, color = Color(0xFF64748B), lineHeight = 18.sp)
    }
}