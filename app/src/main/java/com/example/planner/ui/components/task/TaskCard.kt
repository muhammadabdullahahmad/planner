package com.example.planner.ui.components.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.planner.domain.model.Task
import com.example.planner.domain.model.TaskPriority
import com.example.planner.domain.model.TaskStatus
import com.example.planner.ui.theme.*
import com.example.planner.util.DateTimeUtils

@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    onStatusChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = task.status == TaskStatus.COMPLETED

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Completion checkbox
            IconButton(
                onClick = onStatusChange,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (isCompleted) {
                        Icons.Filled.CheckCircle
                    } else {
                        Icons.Outlined.CheckCircleOutline
                    },
                    contentDescription = if (isCompleted) "Mark incomplete" else "Mark complete",
                    tint = if (isCompleted) StatusCompleted else MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Task content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Priority indicator
                    PriorityBadge(priority = task.priority)

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (task.description != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (task.dueDate != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = DateTimeUtils.getRelativeDateString(task.dueDate),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (task.isOverdue) {
                            PriorityHigh
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            // Points badge
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "+${task.pointsValue}",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun PriorityBadge(
    priority: TaskPriority,
    modifier: Modifier = Modifier
) {
    val color = when (priority) {
        TaskPriority.HIGH -> PriorityHigh
        TaskPriority.MEDIUM -> PriorityMedium
        TaskPriority.LOW -> PriorityLow
    }

    Box(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun StatusIndicator(
    status: TaskStatus,
    modifier: Modifier = Modifier
) {
    val (color, containerColor) = when (status) {
        TaskStatus.PENDING -> StatusPending to StatusPendingContainer
        TaskStatus.IN_PROGRESS -> StatusInProgress to StatusInProgressContainer
        TaskStatus.COMPLETED -> StatusCompleted to StatusCompletedContainer
    }

    Surface(
        modifier = modifier,
        color = containerColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
