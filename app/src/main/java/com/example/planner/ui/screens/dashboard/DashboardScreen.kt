package com.example.planner.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.planner.domain.model.TaskStatus
import com.example.planner.domain.model.UserRole
import com.example.planner.ui.components.common.EmptyStateView
import com.example.planner.ui.components.common.LoadingIndicator
import com.example.planner.ui.components.task.TaskCard
import com.example.planner.ui.components.user.MemberCard
import com.example.planner.ui.components.user.UserAvatar
import com.example.planner.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onTaskClick: (Long) -> Unit,
    onCreateTask: () -> Unit,
    onViewAllTasks: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        LoadingIndicator()
        return
    }

    val currentUser = uiState.currentUser ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hello, ${currentUser.name}!",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = if (currentUser.role == UserRole.ADMIN) "Admin Dashboard" else "My Dashboard",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    UserAvatar(user = currentUser, size = 40.dp)
                    Spacer(modifier = Modifier.width(16.dp))
                }
            )
        },
        floatingActionButton = {
            if (currentUser.role == UserRole.ADMIN) {
                FloatingActionButton(onClick = onCreateTask) {
                    Icon(Icons.Default.Add, contentDescription = "Create Task")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Points",
                        value = "${currentUser.points}",
                        icon = Icons.Default.Star,
                        iconTint = BadgeGold,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Streak",
                        value = "${currentUser.currentStreak}",
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = StreakFire,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Tasks Done",
                        value = "${currentUser.tasksCompleted}",
                        icon = Icons.Default.CheckCircle,
                        iconTint = StatusCompleted,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Today's tasks section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Tasks",
                        style = MaterialTheme.typography.titleMedium
                    )
                    TextButton(onClick = onViewAllTasks) {
                        Text("View All")
                    }
                }
            }

            val pendingTasks = uiState.todayTasks.filter { it.status != TaskStatus.COMPLETED }
            if (pendingTasks.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Celebration,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "All done for today!",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Great job! Take a well-deserved break.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            } else {
                items(pendingTasks.take(5)) { task ->
                    TaskCard(
                        task = task,
                        onClick = { onTaskClick(task.id) },
                        onStatusChange = { viewModel.completeTask(task.id) }
                    )
                }
            }

            // Family members section (Admin only)
            if (currentUser.role == UserRole.ADMIN && uiState.allMembers.size > 1) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Family Members",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.allMembers.filter { it.id != currentUser.id }) { member ->
                            FamilyMemberChip(member = member)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FamilyMemberChip(
    member: com.example.planner.domain.model.User
) {
    Card(
        modifier = Modifier.width(120.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            com.example.planner.ui.components.user.UserAvatar(
                user = member,
                size = 48.dp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = member.name,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1
            )
            Text(
                text = "${member.points} pts",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
