package com.example.planner.ui.screens.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.planner.domain.model.User
import com.example.planner.ui.components.common.LoadingIndicator
import com.example.planner.ui.components.common.PlannerTopBar
import com.example.planner.ui.components.user.UserAvatar
import com.example.planner.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    onBack: () -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        LoadingIndicator()
        return
    }

    Scaffold(
        topBar = {
            PlannerTopBar(
                title = "Leaderboard",
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top 3 podium
            if (uiState.leaderboard.size >= 3) {
                item {
                    TopThreePodium(
                        first = uiState.leaderboard[0],
                        second = uiState.leaderboard[1],
                        third = uiState.leaderboard[2],
                        currentUserId = uiState.currentUserId
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Full list
            item {
                Text(
                    text = "All Members",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            itemsIndexed(uiState.leaderboard) { index, user ->
                LeaderboardItem(
                    rank = index + 1,
                    user = user,
                    isCurrentUser = user.id == uiState.currentUserId
                )
            }
        }
    }
}

@Composable
private fun TopThreePodium(
    first: User,
    second: User,
    third: User,
    currentUserId: Long?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // Second place
        PodiumItem(
            user = second,
            rank = 2,
            height = 100.dp,
            color = BadgeSilver,
            isCurrentUser = second.id == currentUserId
        )

        // First place
        PodiumItem(
            user = first,
            rank = 1,
            height = 130.dp,
            color = BadgeGold,
            isCurrentUser = first.id == currentUserId
        )

        // Third place
        PodiumItem(
            user = third,
            rank = 3,
            height = 80.dp,
            color = BadgeBronze,
            isCurrentUser = third.id == currentUserId
        )
    }
}

@Composable
private fun PodiumItem(
    user: User,
    rank: Int,
    height: androidx.compose.ui.unit.Dp,
    color: Color,
    isCurrentUser: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserAvatar(user = user, size = 48.dp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = user.name,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1
        )
        Text(
            text = "${user.points} pts",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            color = color,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .width(80.dp)
                .height(height)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        when (rank) {
                            1 -> Icons.Default.EmojiEvents
                            else -> Icons.Default.Star
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "#$rank",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardItem(
    rank: Int,
    user: User,
    isCurrentUser: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when (rank) {
                            1 -> BadgeGold
                            2 -> BadgeSilver
                            3 -> BadgeBronze
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (rank <= 3) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            UserAvatar(user = user, size = 40.dp)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleSmall
                    )
                    if (isCurrentUser) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(You)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = StreakFire
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${user.currentStreak}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${user.tasksCompleted} tasks",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Points
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${user.points}",
                    style = MaterialTheme.typography.titleMedium,
                    color = BadgeGold
                )
                Text(
                    text = "points",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
