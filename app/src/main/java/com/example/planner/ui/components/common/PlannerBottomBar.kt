package com.example.planner.ui.components.common

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.planner.ui.navigation.BottomNavItem
import com.example.planner.ui.navigation.Screen

@Composable
fun PlannerBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Check if user is on child dashboard (to highlight Home correctly)
    val isOnChildDashboard = currentRoute == Screen.ChildDashboard.route

    NavigationBar(modifier = modifier) {
        BottomNavItem.items.forEach { item ->
            // For Dashboard item, also consider ChildDashboard as selected
            val selected = when {
                item == BottomNavItem.Dashboard && isOnChildDashboard -> true
                else -> currentRoute == item.route
            }

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = selected,
                onClick = {
                    // If on child dashboard and clicking Home, stay on child dashboard
                    val targetRoute = if (item == BottomNavItem.Dashboard && isOnChildDashboard) {
                        Screen.ChildDashboard.route
                    } else {
                        item.route
                    }

                    if (currentRoute != targetRoute) {
                        navController.navigate(targetRoute) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
