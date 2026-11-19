
package com.example.sos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onLogout: () -> Unit
) {

    // Logout confirmation dialog state
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },

        bottomBar = {
            // Logout button pinned to the bottom
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = Color(0xFFF8FAFC),
                shadowElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            showLogoutDialog = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFEE2E2)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Logout",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444),
                            fontSize = 15.sp
                        )
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // User Header Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToSettings)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Harman",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF0F172A)
                            )

                            Text(
                                text = "harman@gmail.com",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8)
                    )
                }
            }

            // Menu Items List
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    ProfileMenuItem(
                        icon = Icons.Default.VerifiedUser,
                        iconBgColor = Color(0xFFDCFCE7),
                        iconTint = Color(0xFF16A34A),
                        title = "My Safety Status",
                        subtitle = "View your safety insights"
                    ) {}

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    ProfileMenuItem(
                        icon = Icons.Default.Place,
                        iconBgColor = Color(0xFFDBEAFE),
                        iconTint = Color(0xFF2563EB),
                        title = "My Saved Places",
                        subtitle = "11 places"
                    ) {}

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    ProfileMenuItem(
                        icon = Icons.Default.Map,
                        iconBgColor = Color(0xFFFEF3C7),
                        iconTint = Color(0xFFD97706),
                        title = "My Routes",
                        subtitle = "5 recent routes"
                    ) {}

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    ProfileMenuItem(
                        icon = Icons.Default.History,
                        iconBgColor = Color(0xFFF3E8FF),
                        iconTint = Color(0xFF9333EA),
                        title = "Scan History",
                        subtitle = "24 scans",
                        onClick = onNavigateToHistory
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    ProfileMenuItem(
                        icon = Icons.Default.People,
                        iconBgColor = Color(0xFFFFEDD5),
                        iconTint = Color(0xFFEA580C),
                        title = "Emergency Contacts",
                        subtitle = "3 contacts"
                    ) {}

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    ProfileMenuItem(
                        icon = Icons.Default.Settings,
                        iconBgColor = Color(0xFFE2E8F0),
                        iconTint = Color(0xFF475569),
                        title = "Settings",
                        subtitle = "App preferences",
                        onClick = onNavigateToSettings
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    ProfileMenuItem(
                        icon = Icons.Default.Help,
                        iconBgColor = Color(0xFFFCE7F3),
                        iconTint = Color(0xFFDB2777),
                        title = "Help & Support",
                        subtitle = "FAQs and contact"
                    ) {}
                }
            }

            // Bottom spacing
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {

        AlertDialog(
            onDismissRequest = {
                showLogoutDialog = false
            },

            title = {
                Text(
                    text = "Confirm Logout",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF0F172A)
                )
            },

            text = {
                Text(
                    text = "Are you sure you want to logout?",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text(
                        text = "Yes",
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold
                    )
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                    }
                ) {
                    Text(
                        text = "No",
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.Bold
                    )
                }
            },

            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}


// --------------------------------------------------
// PROFILE MENU ITEM
// --------------------------------------------------

@Composable
fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Colorful Icon Box
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0F172A)
                )

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(20.dp)
        )
    }
}


// --------------------------------------------------
// SETTINGS SCREEN
// --------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {

    var locationSharing by remember { mutableStateOf(true) }
    var notifications by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                ),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    SettingToggleItem(
                        icon = Icons.Default.ShareLocation,
                        title = "Location Sharing",
                        subtitle = "Used for safer routes and images",
                        checked = locationSharing,
                        onCheckedChange = {
                            locationSharing = it
                        }
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    SettingToggleItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        subtitle = "Alerts, safety updates and GeoAI",
                        checked = notifications,
                        onCheckedChange = {
                            notifications = it
                        }
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    SettingToggleItem(
                        icon = Icons.Default.DarkMode,
                        title = "Dark Mode",
                        subtitle = "Switch to dark theme",
                        checked = darkMode,
                        onCheckedChange = {
                            darkMode = it
                        }
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    SettingMenuItem(
                        icon = Icons.Default.Language,
                        title = "Language",
                        value = "English"
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    SettingMenuItem(
                        icon = Icons.Default.Map,
                        title = "Map View",
                        value = "Default"
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    SettingMenuItem(
                        icon = Icons.Default.Lock,
                        title = "Privacy & Security",
                        value = ""
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    SettingMenuItem(
                        icon = Icons.Default.Help,
                        title = "Help & Support",
                        value = ""
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    SettingMenuItem(
                        icon = Icons.Default.Info,
                        title = "About Vardan",
                        value = ""
                    )
                }
            }
        }
    }
}


// --------------------------------------------------
// SETTINGS TOGGLE ITEM
// --------------------------------------------------

@Composable
fun SettingToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {

            Icon(
                icon,
                contentDescription = null,
                tint = Color(0xFF334155),
                modifier = Modifier.size(22.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0F172A)
                )

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}


// --------------------------------------------------
// SETTINGS MENU ITEM
// --------------------------------------------------

@Composable
fun SettingMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                icon,
                contentDescription = null,
                tint = Color(0xFF334155),
                modifier = Modifier.size(22.dp)
            )

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF0F172A)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (value.isNotEmpty()) {
                Text(
                    text = value,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
