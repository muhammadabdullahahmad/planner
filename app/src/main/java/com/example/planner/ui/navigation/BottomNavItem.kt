package com.example.planner.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    object Dashboard : BottomNavItem(
        Screen.Dashboard.route,
        "Home",
        Icons.Outlined.Home,
        Icons.Filled.Home
    )

    object Tasks : BottomNavItem(
        Screen.Tasks.route,
        "Tasks",
        Icons.Outlined.CheckCircle,
        Icons.Filled.CheckCircle
    )

    object Calendar : BottomNavItem(
        Screen.Calendar.route,
        "Calendar",
        Icons.Outlined.CalendarMonth,
        Icons.Filled.CalendarMonth
    )

    object Members : BottomNavItem(
        Screen.Members.route,
        "Family",
        Icons.Outlined.People,
        Icons.Filled.People
    )

    object Profile : BottomNavItem(
        Screen.Profile.route,
        "Profile",
        Icons.Outlined.Person,
        Icons.Filled.Person
    )

    companion object {
        val items = listOf(Dashboard, Tasks, Calendar, Members, Profile)
    }
}
