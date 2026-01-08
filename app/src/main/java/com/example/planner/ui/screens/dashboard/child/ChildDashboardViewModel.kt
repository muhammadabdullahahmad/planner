package com.example.planner.ui.screens.dashboard.child

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.preferences.UserPreferences
import com.example.planner.data.repository.AchievementRepository
import com.example.planner.data.repository.CategoryRepository
import com.example.planner.data.repository.TaskRepository
import com.example.planner.data.repository.UserRepository
import com.example.planner.domain.model.Category
import com.example.planner.domain.model.Task
import com.example.planner.domain.model.TaskStatus
import com.example.planner.domain.model.User
import com.example.planner.util.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChildTaskItem(
    val task: Task,
    val category: Category? = null,
    val isCompletedByUser: Boolean = false
)

data class ChildDashboardUiState(
    val currentUser: User? = null,
    val todayTasks: List<ChildTaskItem> = emptyList(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val isLoading: Boolean = true,
    val showCelebration: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ChildDashboardViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val achievementRepository: AchievementRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChildDashboardUiState())
    val uiState: StateFlow<ChildDashboardUiState> = _uiState.asStateFlow()

    private var categories: Map<Long, Category> = emptyMap()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Load categories first
            categoryRepository.getAllCategories().collect { categoryList ->
                categories = categoryList.associateBy { it.id }
            }
        }

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
                val startOfDay = DateTimeUtils.getStartOfDay()
                val endOfDay = DateTimeUtils.getEndOfDay()

                taskRepository.getTasksForUserOnDate(userId, startOfDay, endOfDay).collect { tasks ->
                    val taskItems = tasks.map { task ->
                        val isCompleted = taskRepository.isCompletedByUser(task.id, userId)
                        ChildTaskItem(
                            task = task,
                            category = task.categoryId?.let { categories[it] },
                            isCompletedByUser = isCompleted
                        )
                    }

                    val completed = taskItems.count { it.isCompletedByUser || it.task.status == TaskStatus.COMPLETED }
                    val total = taskItems.size

                    _uiState.update { state ->
                        state.copy(
                            todayTasks = taskItems,
                            completedCount = completed,
                            totalCount = total
                        )
                    }

                    // Check if all tasks just got completed
                    if (total > 0 && completed == total && !_uiState.value.showCelebration) {
                        _uiState.update { it.copy(showCelebration = true) }
                    }
                }
            }
        }
    }

    fun startTask(taskId: Long) {
        viewModelScope.launch {
            taskRepository.startTask(taskId)
            // Refresh tasks
            _uiState.value.currentUser?.id?.let { loadUserData(it) }
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

    fun dismissCelebration() {
        _uiState.update { it.copy(showCelebration = false) }
    }
}
