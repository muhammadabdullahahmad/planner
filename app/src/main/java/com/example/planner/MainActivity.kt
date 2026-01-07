package com.example.planner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.planner.ui.components.common.PlannerBottomBar
import com.example.planner.ui.navigation.BottomNavItem
import com.example.planner.ui.navigation.PlannerNavHost
import com.example.planner.ui.navigation.Screen
import com.example.planner.ui.theme.PlannerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlannerTheme {
                FamilyPlannerApp()
            }
        }
    }
}

@Composable
fun FamilyPlannerApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine if we should show bottom navigation
    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Tasks.route,
        Screen.Calendar.route,
        Screen.Members.route,
        Screen.Profile.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                PlannerBottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        PlannerNavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
