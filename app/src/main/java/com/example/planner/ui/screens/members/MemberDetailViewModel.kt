package com.example.planner.ui.screens.members

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.repository.TaskRepository
import com.example.planner.data.repository.UserRepository
import com.example.planner.domain.model.Task
import com.example.planner.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MemberDetailUiState(
    val member: User? = null,
    val assignedTasks: List<Task> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class MemberDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemberDetailUiState())
    val uiState: StateFlow<MemberDetailUiState> = _uiState.asStateFlow()

    fun loadMember(memberId: Long) {
        viewModelScope.launch {
            userRepository.getUserByIdFlow(memberId).collect { user ->
                _uiState.update { it.copy(member = user, isLoading = false) }
            }
        }

        viewModelScope.launch {
            taskRepository.getActiveTasksForUser(memberId).collect { tasks ->
                _uiState.update { it.copy(assignedTasks = tasks) }
            }
        }
    }
}
