package com.example.planner.domain.model

enum class TaskPriority(val displayName: String, val points: Int) {
    HIGH("High", 20),
    MEDIUM("Medium", 10),
    LOW("Low", 5)
}
