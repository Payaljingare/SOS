package com.example.sos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HistoryItem(
    val title: String,
    val type: String,
    val risk: String,
    val score: String,
    val time: String,
    val dateCategory: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanHistoryScreen(
    onBack: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "High Risk", "Suspicious", "Safe")

    val historyItems = listOf(
        HistoryItem("https://xyz-offer-payment.com", "Link", "High Risk", "85", "10:34 AM", "Today"),
        HistoryItem("Payment QR Code", "QR", "Suspicious", "62", "09:15 AM", "Today"),
        HistoryItem("SBI Account Alert", "Email", "Safe", "18", "08:42 AM", "Today"),
        HistoryItem("Job Opportunity", "Email", "Suspicious", "48", "06:20 PM", "Yesterday"),
        HistoryItem("Credit Card Offer", "Link", "High Risk", "76", "11:12 AM", "Yesterday"),
        HistoryItem("Amazon Deal", "Link", "Safe", "12", "09:05 AM", "Yesterday")
    )

    val filteredItems = if (selectedFilter == "All") historyItems else historyItems.filter { it.risk.equals(selectedFilter, ignoreCase = true) }
    val todayItems = filteredItems.filter { it.dateCategory == "Today" }
    val yesterdayItems = filteredItems.filter { it.dateCategory == "Yesterday" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan History", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0F172A)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color(0xFF0F172A))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                filters.forEach { filter ->
                    val isSelected = filter == selectedFilter
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Color(0xFF0F172A) else Color.White,
                        modifier = Modifier.clickable { selectedFilter = filter }
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color.White else Color(0xFF64748B),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (todayItems.isNotEmpty()) {
                    item {
                        Text(text = "Today", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF64748B))
                    }
                    items(todayItems) { item -> HistoryCard(item) }
                }

                if (yesterdayItems.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Yesterday", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF64748B))
                    }
                    items(yesterdayItems) { item -> HistoryCard(item) }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(item: HistoryItem) {
    val riskColor = when (item.risk) {
        "High Risk" -> Color(0xFFEF4444)
        "Suspicious" -> Color(0xFFF59E0B)
        else -> Color(0xFF16A34A)
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(riskColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (item.type) {
                            "Link" -> Icons.Default.Link
                            "QR" -> Icons.Default.QrCode
                            else -> Icons.Default.Email
                        },
                        contentDescription = null,
                        tint = riskColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A), maxLines = 1)
                    Text(text = "${item.risk} • ${item.score}", fontSize = 12.sp, color = riskColor, fontWeight = FontWeight.Medium)
                }
            }

            Text(text = item.time, fontSize = 11.sp, color = Color(0xFF94A3B8))
        }
    }
}