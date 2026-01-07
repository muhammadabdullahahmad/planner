package com.example.planner.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.preferences.UserPreferences
import com.example.planner.data.repository.AchievementRepository
import com.example.planner.data.repository.TaskRepository
import com.example.planner.data.repository.UserRepository
import com.example.planner.domain.model.Task
import com.example.planner.domain.model.User
import com.example.planner.util.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskDetailUiState(
    val task: Task? = null,
    val currentUser: User? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val achievementRepository: AchievementRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    private var taskId: Long = 0

    init {
        viewModelScope.launch {
            userPreferences.loggedInUserId.collect { userId ->
                if (userId != null) {
                    val user = userRepository.getUserById(userId)
                    _uiState.update { it.copy(currentUser = user) }
                }
            }
        }
    }

    fun loadTask(taskId: Long) {
        this.taskId = taskId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            taskRepository.getTaskByIdFlow(taskId).collect { task ->
                _uiState.update { it.copy(task = task, isLoading = false) }
            }
        }
    }

    fun completeTask() {
        viewModelScope.launch {
            val userId = _uiState.value.currentUser?.id ?: return@launch
            val task = _uiState.value.task ?: return@launch

            taskRepository.markCompletedByUser(taskId, userId)
            userRepository.addPoints(userId, task.pointsValue)
            userRepository.incrementTasksCompleted(userId)

            val today = DateTimeUtils.getStartOfDay()
            val currentUser = userRepository.getUserById(userId)
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

            val updatedUser = userRepository.getUserById(userId)
            if (updatedUser != null) {
                achievementRepository.checkAndAwardAchievements(
                    userId = userId,
                    points = updatedUser.points,
                    tasksCompleted = updatedUser.tasksCompleted,
                    streak = updatedUser.currentStreak
                )
            }
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
        }
    }
}
