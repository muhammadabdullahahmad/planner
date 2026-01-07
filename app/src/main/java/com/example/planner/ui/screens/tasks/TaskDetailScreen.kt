package com.example.planner.ui.screens.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.planner.domain.model.TaskPriority
import com.example.planner.domain.model.TaskStatus
import com.example.planner.domain.model.UserRole
import com.example.planner.ui.components.common.LoadingIndicator
import com.example.planner.ui.components.common.PlannerTopBar
import com.example.planner.ui.components.task.PriorityBadge
import com.example.planner.ui.components.task.StatusIndicator
import com.example.planner.ui.theme.*
import com.example.planner.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Long,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    if (uiState.isLoading) {
        LoadingIndicator()
        return
    }

    val task = uiState.task
    if (task == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Task not found")
        }
        return
    }

    val isAdmin = uiState.currentUser?.role == UserRole.ADMIN

    Scaffold(
        topBar = {
            PlannerTopBar(
                title = "Task Details",
                showBackButton = true,
                onBackClick = onBack,
                actions = {
                    if (isAdmin) {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = {
                            viewModel.deleteTask()
                            onBack()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Title and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PriorityBadge(priority = task.priority)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
                StatusIndicator(status = task.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            if (task.description != null) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Details card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Due date
                    if (task.dueDate != null) {
                        DetailRow(
                            icon = Icons.Default.CalendarToday,
                            label = "Due Date",
                            value = DateTimeUtils.formatDate(task.dueDate) +
                                    (task.dueTime?.let { " at $it" } ?: "")
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Priority
                    DetailRow(
                        icon = Icons.Default.Flag,
                        label = "Priority",
                        value = task.priority.displayName,
                        valueColor = when (task.priority) {
                            TaskPriority.HIGH -> PriorityHigh
                            TaskPriority.MEDIUM -> PriorityMedium
                            TaskPriority.LOW -> PriorityLow
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Points
                    DetailRow(
                        icon = Icons.Default.Star,
                        label = "Points",
                        value = "+${task.pointsValue} points",
                        valueColor = BadgeGold
                    )

                    // Recurring
                    if (task.isRecurring && task.recurrenceType != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        DetailRow(
                            icon = Icons.Default.Repeat,
                            label = "Repeats",
                            value = task.recurrenceType.displayName
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Complete button
            if (task.status != TaskStatus.COMPLETED) {
                Button(
                    onClick = {
                        viewModel.completeTask()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mark as Complete")
                }
            } else {
                Surface(
                    color = StatusCompletedContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusCompleted
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Task Completed",
                            style = MaterialTheme.typography.titleMedium,
                            color = StatusCompleted
                        )
                    }
                }

                if (task.completedAt != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Completed on ${DateTimeUtils.formatDateTime(task.completedAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor
        )
    }
}
