package com.example.planner.domain.model

import com.example.planner.data.local.entity.AchievementEntity

data class Achievement(
    val id: Long = 0,
    val name: String,
    val description: String,
    val iconName: String,
    val badgeColorHex: String,
    val requiredPoints: Int? = null,
    val requiredTaskCount: Int? = null,
    val requiredStreak: Int? = null,
    val achievementType: AchievementType,
    val isEarned: Boolean = false,
    val earnedAt: Long? = null
) {
    companion object {
        fun fromEntity(entity: AchievementEntity): Achievement {
            return Achievement(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                iconName = entity.iconName,
                badgeColorHex = entity.badgeColorHex,
                requiredPoints = entity.requiredPoints,
                requiredTaskCount = entity.requiredTaskCount,
                requiredStreak = entity.requiredStreak,
                achievementType = AchievementType.valueOf(entity.achievementType)
            )
        }

        fun getDefaultAchievements(): List<Achievement> {
            return listOf(
                // Points-based
                Achievement(
                    name = "First Points",
                    description = "Earn your first points",
                    iconName = "star",
                    badgeColorHex = "#FFD700",
                    requiredPoints = 1,
                    achievementType = AchievementType.POINTS
                ),
                Achievement(
                    name = "Century",
                    description = "Earn 100 points",
                    iconName = "stars",
                    badgeColorHex = "#FFD700",
                    requiredPoints = 100,
                    achievementType = AchievementType.POINTS
                ),
                Achievement(
                    name = "Point Master",
                    description = "Earn 500 points",
                    iconName = "workspace_premium",
                    badgeColorHex = "#FFD700",
                    requiredPoints = 500,
                    achievementType = AchievementType.POINTS
                ),
                Achievement(
                    name = "Point Legend",
                    description = "Earn 1000 points",
                    iconName = "diamond",
                    badgeColorHex = "#FFD700",
                    requiredPoints = 1000,
                    achievementType = AchievementType.POINTS
                ),

                // Task-based
                Achievement(
                    name = "First Step",
                    description = "Complete your first task",
                    iconName = "check_circle",
                    badgeColorHex = "#4CAF50",
                    requiredTaskCount = 1,
                    achievementType = AchievementType.TASKS
                ),
                Achievement(
                    name = "Task Warrior",
                    description = "Complete 10 tasks",
                    iconName = "military_tech",
                    badgeColorHex = "#4CAF50",
                    requiredTaskCount = 10,
                    achievementType = AchievementType.TASKS
                ),
                Achievement(
                    name = "Task Champion",
                    description = "Complete 50 tasks",
                    iconName = "emoji_events",
                    badgeColorHex = "#4CAF50",
                    requiredTaskCount = 50,
                    achievementType = AchievementType.TASKS
                ),
                Achievement(
                    name = "Task Legend",
                    description = "Complete 100 tasks",
                    iconName = "local_fire_department",
                    badgeColorHex = "#4CAF50",
                    requiredTaskCount = 100,
                    achievementType = AchievementType.TASKS
                ),

                // Streak-based
                Achievement(
                    name = "Consistent",
                    description = "Maintain a 3-day streak",
                    iconName = "whatshot",
                    badgeColorHex = "#FF5722",
                    requiredStreak = 3,
                    achievementType = AchievementType.STREAK
                ),
                Achievement(
                    name = "Week Warrior",
                    description = "Maintain a 7-day streak",
                    iconName = "local_fire_department",
                    badgeColorHex = "#FF5722",
                    requiredStreak = 7,
                    achievementType = AchievementType.STREAK
                ),
                Achievement(
                    name = "Unstoppable",
                    description = "Maintain a 30-day streak",
                    iconName = "bolt",
                    badgeColorHex = "#FF5722",
                    requiredStreak = 30,
                    achievementType = AchievementType.STREAK
                ),

                // Special
                Achievement(
                    name = "Early Bird",
                    description = "Complete a task before 8 AM",
                    iconName = "wb_sunny",
                    badgeColorHex = "#FFC107",
                    achievementType = AchievementType.SPECIAL
                ),
                Achievement(
                    name = "Night Owl",
                    description = "Complete a task after 10 PM",
                    iconName = "nights_stay",
                    badgeColorHex = "#3F51B5",
                    achievementType = AchievementType.SPECIAL
                ),
                Achievement(
                    name = "Super Helper",
                    description = "Complete 5 tasks in one day",
                    iconName = "superhero",
                    badgeColorHex = "#E91E63",
                    achievementType = AchievementType.SPECIAL
                )
            )
        }
    }

    fun toEntity(): AchievementEntity {
        return AchievementEntity(
            id = id,
            name = name,
            description = description,
            iconName = iconName,
            badgeColorHex = badgeColorHex,
            requiredPoints = requiredPoints,
            requiredTaskCount = requiredTaskCount,
            requiredStreak = requiredStreak,
            achievementType = achievementType.name
        )
    }
}
