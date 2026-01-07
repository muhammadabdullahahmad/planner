package com.example.planner.ui.components.achievement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.planner.domain.model.Achievement

@Composable
fun BadgeDisplay(
    achievement: Achievement,
    modifier: Modifier = Modifier
) {
    val badgeColor = try {
        Color(android.graphics.Color.parseColor(achievement.badgeColorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Column(
        modifier = modifier
            .width(100.dp)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    if (achievement.isEarned) badgeColor
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (achievement.isEarned) {
                    getIconForAchievement(achievement.iconName)
                } else {
                    Icons.Outlined.Lock
                },
                contentDescription = achievement.name,
                modifier = Modifier.size(32.dp),
                tint = if (achievement.isEarned) Color.White else MaterialTheme.colorScheme.outline
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = achievement.name,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 2,
            color = if (achievement.isEarned) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
fun AchievementCard(
    achievement: Achievement,
    modifier: Modifier = Modifier
) {
    val badgeColor = try {
        Color(android.graphics.Color.parseColor(achievement.badgeColorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isEarned) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (achievement.isEarned) badgeColor
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (achievement.isEarned) {
                        getIconForAchievement(achievement.iconName)
                    } else {
                        Icons.Outlined.Lock
                    },
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (achievement.isEarned) Color.White else MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (achievement.isEarned) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Earned",
                    tint = badgeColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun getIconForAchievement(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "star" -> Icons.Filled.Star
        "stars" -> Icons.Filled.Stars
        "workspace_premium" -> Icons.Filled.WorkspacePremium
        "diamond" -> Icons.Filled.Diamond
        "check_circle" -> Icons.Filled.CheckCircle
        "military_tech" -> Icons.Filled.MilitaryTech
        "emoji_events" -> Icons.Filled.EmojiEvents
        "local_fire_department" -> Icons.Filled.LocalFireDepartment
        "whatshot" -> Icons.Filled.Whatshot
        "bolt" -> Icons.Filled.Bolt
        "wb_sunny" -> Icons.Filled.WbSunny
        "nights_stay" -> Icons.Filled.NightsStay
        else -> Icons.Filled.Star
    }
}
