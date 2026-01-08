package com.example.planner.ui.screens.tasks

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.planner.domain.model.UserRole
import com.example.planner.ui.components.common.AdminPasswordDialog
import com.example.planner.ui.components.common.EmptyStateView
import com.example.planner.ui.components.common.LoadingIndicator
import com.example.planner.ui.components.common.PlannerTopBar
import com.example.planner.ui.components.task.TaskCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onTaskClick: (Long) -> Unit,
    onCreateTask: () -> Unit,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        LoadingIndicator()
        return
    }

    val isAdmin = uiState.currentUser?.role == UserRole.ADMIN

    // Admin password dialog
    if (uiState.showPasswordDialog) {
        AdminPasswordDialog(
            onDismiss = { viewModel.dismissPasswordDialog() },
            onConfirm = { password ->
                viewModel.verifyAdminPassword(password) {
                    onCreateTask()
                }
            },
            isError = uiState.passwordError
        )
    }

    Scaffold(
        topBar = {
            PlannerTopBar(title = "Tasks")
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = {
                        if (uiState.hasAdminPassword) {
                            viewModel.onCreateTaskClicked()
                        } else {
                            onCreateTask()
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Task")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = uiState.filter == filter,
                        onClick = { viewModel.setFilter(filter) },
                        label = { Text(filter.name.replace("_", " ")) }
                    )
                }
            }

            // Category filter
            if (uiState.categories.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.selectedCategory == null,
                        onClick = { viewModel.setCategory(null) },
                        label = { Text("All Categories") }
                    )
                    uiState.categories.forEach { category ->
                        FilterChip(
                            selected = uiState.selectedCategory?.id == category.id,
                            onClick = { viewModel.setCategory(category) },
                            label = { Text(category.name) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.tasks.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.CheckCircle,
                    title = "No tasks found",
                    message = when (uiState.filter) {
                        TaskFilter.ALL -> "Create your first task to get started!"
                        TaskFilter.TODAY -> "No tasks scheduled for today."
                        TaskFilter.PENDING -> "All tasks are completed!"
                        TaskFilter.COMPLETED -> "No completed tasks yet."
                    },
                    actionLabel = if (isAdmin) "Create Task" else null,
                    onAction = if (isAdmin) onCreateTask else null
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.tasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onClick = { onTaskClick(task.id) },
                            onStatusChange = { viewModel.completeTask(task.id) }
                        )
                    }
                }
            }
        }
    }
}
