package com.example.planner.util

object Constants {
    // Member colors for calendar color-coding
    val MEMBER_COLORS = listOf(
        "#4CAF50", // Green
        "#2196F3", // Blue
        "#FF9800", // Orange
        "#E91E63", // Pink
        "#9C27B0", // Purple
        "#00BCD4", // Cyan
        "#FF5722", // Deep Orange
        "#795548"  // Brown
    )

    // Points multipliers
    const val HIGH_PRIORITY_POINTS = 20
    const val MEDIUM_PRIORITY_POINTS = 10
    const val LOW_PRIORITY_POINTS = 5

    // Streak requirements
    const val CONSISTENT_STREAK = 3
    const val WEEK_WARRIOR_STREAK = 7
    const val UNSTOPPABLE_STREAK = 30

    // Special achievement requirements
    const val EARLY_BIRD_HOUR = 8
    const val NIGHT_OWL_HOUR = 22
    const val SUPER_HELPER_TASKS = 5
}
