package com.example.planner.ui.screens.members

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.planner.domain.model.UserRole
import com.example.planner.ui.components.common.EmptyStateView
import com.example.planner.ui.components.common.LoadingIndicator
import com.example.planner.ui.components.common.PlannerTopBar
import com.example.planner.ui.components.user.MemberCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(
    onMemberClick: (Long) -> Unit,
    onAddMember: () -> Unit,
    viewModel: MembersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        LoadingIndicator()
        return
    }

    val isAdmin = uiState.currentUser?.role == UserRole.ADMIN

    Scaffold(
        topBar = {
            PlannerTopBar(title = "Family Members")
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(onClick = onAddMember) {
                    Icon(Icons.Default.Add, contentDescription = "Add Member")
                }
            }
        }
    ) { paddingValues ->
        if (uiState.members.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.People,
                title = "No family members",
                message = "Add family members to start assigning tasks",
                actionLabel = if (isAdmin) "Add Member" else null,
                onAction = if (isAdmin) onAddMember else null,
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
                items(uiState.members, key = { it.id }) { member ->
                    MemberCard(
                        user = member,
                        onClick = { onMemberClick(member.id) }
                    )
                }
            }
        }
    }
}
