package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.localization.LocaleHelper
import com.example.ui.theme.*
import com.example.ui.vpn.DashboardScreen
import com.example.ui.vpn.OptimizeScreen
import com.example.ui.vpn.ServersScreen
import com.example.ui.vpn.SubscriptionsScreen
import com.example.viewmodel.VpnViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: VpnViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScreenWrapper(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainScreenWrapper(viewModel: VpnViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

    val settings by viewModel.appSettings.collectAsState()
    val lang = settings.language

    // Listen to ViewModel system event alerts and show as user-friendly Toasts
    LaunchedEffect(key1 = true) {
        viewModel.uiEvents.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = CosmicCard,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Dashboard Tab
                val isDashboard = currentRoute == "dashboard"
                NavigationBarItem(
                    selected = isDashboard,
                    onClick = {
                        navController.navigate("dashboard") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (isDashboard) Icons.Default.NetworkCheck else Icons.Outlined.NetworkCheck,
                            contentDescription = "Dashboard"
                        )
                    },
                    label = {
                        Text(
                            text = LocaleHelper.getString("tab_dashboard", lang),
                            fontSize = 11.sp,
                            fontWeight = if (isDashboard) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonBlue,
                        unselectedIconColor = TextSecondary,
                        selectedTextColor = NeonBlue,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = CosmicCardElevated
                    )
                )

                // Servers Tab
                val isServers = currentRoute == "servers"
                NavigationBarItem(
                    selected = isServers,
                    onClick = {
                        navController.navigate("servers") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (isServers) Icons.Default.Dns else Icons.Outlined.Dns,
                            contentDescription = "Servers"
                        )
                    },
                    label = {
                        Text(
                            text = LocaleHelper.getString("tab_servers", lang),
                            fontSize = 11.sp,
                            fontWeight = if (isServers) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonBlue,
                        unselectedIconColor = TextSecondary,
                        selectedTextColor = NeonBlue,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = CosmicCardElevated
                    )
                )

                // Subscriptions Tab
                val isSubs = currentRoute == "subscriptions"
                NavigationBarItem(
                    selected = isSubs,
                    onClick = {
                        navController.navigate("subscriptions") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (isSubs) Icons.Default.CloudQueue else Icons.Outlined.CloudQueue,
                            contentDescription = "Subscriptions"
                        )
                    },
                    label = {
                        Text(
                            text = LocaleHelper.getString("tab_subs", lang),
                            fontSize = 11.sp,
                            fontWeight = if (isSubs) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonBlue,
                        unselectedIconColor = TextSecondary,
                        selectedTextColor = NeonBlue,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = CosmicCardElevated
                    )
                )

                // Optimize/Gaming Tab
                val isOptimize = currentRoute == "optimize"
                NavigationBarItem(
                    selected = isOptimize,
                    onClick = {
                        navController.navigate("optimize") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (isOptimize) Icons.Default.Tune else Icons.Outlined.Tune,
                            contentDescription = "Optimize"
                        )
                    },
                    label = {
                        Text(
                            text = LocaleHelper.getString("tab_optimize", lang),
                            fontSize = 11.sp,
                            fontWeight = if (isOptimize) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonBlue,
                        unselectedIconColor = TextSecondary,
                        selectedTextColor = NeonBlue,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = CosmicCardElevated
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CosmicBg)
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "dashboard"
            ) {
                composable("dashboard") {
                    DashboardScreen(
                        viewModel = viewModel,
                        settings = settings,
                        onNavigateToServers = {
                            navController.navigate("servers") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                composable("servers") {
                    ServersScreen(
                        viewModel = viewModel,
                        settings = settings
                    )
                }
                composable("subscriptions") {
                    SubscriptionsScreen(
                        viewModel = viewModel,
                        settings = settings
                    )
                }
                composable("optimize") {
                    OptimizeScreen(
                        viewModel = viewModel,
                        settings = settings
                    )
                }
            }
        }
    }
}
