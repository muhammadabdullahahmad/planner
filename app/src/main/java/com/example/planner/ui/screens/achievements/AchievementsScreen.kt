package com.example.planner.ui.screens.achievements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.planner.ui.components.achievement.AchievementCard
import com.example.planner.ui.components.common.EmptyStateView
import com.example.planner.ui.components.common.LoadingIndicator
import com.example.planner.ui.components.common.PlannerTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    onBack: () -> Unit,
    viewModel: AchievementsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        LoadingIndicator()
        return
    }

    Scaffold(
        topBar = {
            PlannerTopBar(
                title = "Achievements",
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        if (uiState.allAchievements.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.EmojiEvents,
                title = "No achievements yet",
                message = "Complete tasks to earn achievements!",
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Earned achievements
                if (uiState.earnedAchievements.isNotEmpty()) {
                    item {
                        Text(
                            text = "Earned (${uiState.earnedAchievements.size})",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    items(uiState.earnedAchievements) { achievement ->
                        AchievementCard(achievement = achievement)
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                // Locked achievements
                if (uiState.lockedAchievements.isNotEmpty()) {
                    item {
                        Text(
                            text = "Locked (${uiState.lockedAchievements.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(uiState.lockedAchievements) { achievement ->
                        AchievementCard(achievement = achievement)
                    }
                }
            }
        }
    }
}
