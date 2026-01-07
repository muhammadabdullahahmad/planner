package com.example.planner.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.preferences.UserPreferences
import com.example.planner.data.repository.AchievementRepository
import com.example.planner.data.repository.TaskRepository
import com.example.planner.data.repository.UserRepository
import com.example.planner.domain.model.Task
import com.example.planner.domain.model.TaskStatus
import com.example.planner.domain.model.User
import com.example.planner.domain.model.UserRole
import com.example.planner.util.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val currentUser: User? = null,
    val todayTasks: List<Task> = emptyList(),
    val allMembers: List<User> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val achievementRepository: AchievementRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            userPreferences.loggedInUserId.collect { userId ->
                if (userId != null) {
                    loadUserData(userId)
                }
            }
        }
    }

    private fun loadUserData(userId: Long) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            _uiState.update { it.copy(currentUser = user, isLoading = false) }

            if (user != null) {
                // Load tasks for today
                val startOfDay = DateTimeUtils.getStartOfDay()
                val endOfDay = DateTimeUtils.getEndOfDay()

                if (user.role == UserRole.ADMIN) {
                    // Admin sees all tasks for today
                    taskRepository.getTasksForDate(startOfDay, endOfDay).collect { tasks ->
                        _uiState.update { it.copy(todayTasks = tasks) }
                    }
                } else {
                    // Member sees only their tasks
                    taskRepository.getTasksForUserOnDate(userId, startOfDay, endOfDay).collect { tasks ->
                        _uiState.update { it.copy(todayTasks = tasks) }
                    }
                }
            }
        }

        // Load all family members for admin
        viewModelScope.launch {
            userRepository.getAllUsers().collect { users ->
                _uiState.update { it.copy(allMembers = users) }
            }
        }
    }

    fun completeTask(taskId: Long) {
        viewModelScope.launch {
            val userId = _uiState.value.currentUser?.id ?: return@launch
            val task = taskRepository.getTaskById(taskId) ?: return@launch

            // Mark completed by user
            taskRepository.markCompletedByUser(taskId, userId)

            // Award points
            userRepository.addPoints(userId, task.pointsValue)
            userRepository.incrementTasksCompleted(userId)

            // Update streak
            val currentUser = userRepository.getUserById(userId)
            val today = DateTimeUtils.getStartOfDay()
            val lastCompletion = currentUser?.lastTaskCompletionDate ?: 0
            val yesterday = today - 24 * 60 * 60 * 1000

            val newStreak = if (lastCompletion in yesterday until today) {
                (currentUser?.currentStreak ?: 0) + 1
            } else if (lastCompletion >= today) {
                currentUser?.currentStreak ?: 1
            } else {
                1
            }

            userRepository.updateStreak(userId, newStreak, today)

            // Check achievements
            val updatedUser = userRepository.getUserById(userId)
            if (updatedUser != null) {
                achievementRepository.checkAndAwardAchievements(
                    userId = userId,
                    points = updatedUser.points,
                    tasksCompleted = updatedUser.tasksCompleted,
                    streak = updatedUser.currentStreak
                )
            }

            // Refresh user data
            loadUserData(userId)
        }
    }
}
