package com.example.planner.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class TaskCategory(
    val displayName: String,
    val icon: ImageVector,
    val colorHex: String
) {
    CHORES("Chores", Icons.Default.CleaningServices, "#4CAF50"),
    SCHOOL("School", Icons.Default.School, "#2196F3"),
    SHOPPING("Shopping", Icons.Default.ShoppingCart, "#FF9800"),
    FAMILY_EVENTS("Family Events", Icons.Default.Celebration, "#E91E63"),
    PERSONAL("Personal", Icons.Default.Person, "#9C27B0"),
    PROJECTS("Projects", Icons.Default.Folder, "#607D8B")
}
