package com.example.sos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sos.ui.screens.*
import com.example.sos.ui.theme.SOSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SOSTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNavigateToCitySafety = { navController.navigate("city_safety") },
                onNavigateToScamShield = { navController.navigate("scam_shield") },
                onNavigateToSupport = { navController.navigate("support") },
                onNavigateToMap = { navController.navigate("map_route") },
                onNavigateToHistory = { navController.navigate("scan_history") },
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }
        composable("city_safety") {
            SafetyNavigationWrapper(
                onBack = { navController.popBackStack() }
            )
        }
        composable("scam_shield") {
            ScamShieldScreen(
                onBack = { navController.popBackStack() },
                onAnalyzeLink = { _ -> navController.navigate("scan_result/link") },
                onAnalyzeEmail = { _, _, _ -> navController.navigate("scan_result/email") },
                onScanQr = { navController.navigate("scan_result/qr") },
                onViewHistory = { navController.navigate("scan_history") }
            )
        }
        composable(
            route = "scan_result/{type}",
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "link"
            ScanResultScreen(
                resultType = type,
                onBack = { navController.popBackStack() }
            )
        }
        composable("scan_history") {
            ScanHistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("support") {
            SupportAssistantScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("profile") {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToHistory = { navController.navigate("scan_history") }
            )
        }
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("map_route") {
            MapRouteScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
