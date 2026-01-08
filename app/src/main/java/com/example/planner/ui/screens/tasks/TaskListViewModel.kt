package com.example.planner.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.preferences.UserPreferences
import com.example.planner.data.repository.AchievementRepository
import com.example.planner.data.repository.CategoryRepository
import com.example.planner.data.repository.TaskRepository
import com.example.planner.data.repository.UserRepository
import com.example.planner.domain.model.*
import com.example.planner.util.DateTimeUtils
import com.example.planner.util.PasswordHasher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TaskFilter {
    ALL, TODAY, PENDING, COMPLETED
}

data class TaskListUiState(
    val currentUser: User? = null,
    val tasks: List<Task> = emptyList(),
    val categories: List<Category> = emptyList(),
    val filter: TaskFilter = TaskFilter.ALL,
    val selectedCategory: Category? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val showPasswordDialog: Boolean = false,
    val passwordError: Boolean = false,
    val hasAdminPassword: Boolean = false
)

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val achievementRepository: AchievementRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    init {
        loadData()
        checkAdminPassword()
    }

    private fun checkAdminPassword() {
        viewModelScope.launch {
            userPreferences.adminPasswordHash.collect { hash ->
                _uiState.update { it.copy(hasAdminPassword = hash != null) }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            userPreferences.loggedInUserId.collect { userId ->
                if (userId != null) {
                    val user = userRepository.getUserById(userId)
                    _uiState.update { it.copy(currentUser = user, isLoading = false) }
                    loadTasks(user)
                }
            }
        }

        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }

    private fun loadTasks(user: User?) {
        if (user == null) return

        viewModelScope.launch {
            val tasksFlow = if (user.role == UserRole.ADMIN) {
                taskRepository.getAllTasks()
            } else {
                taskRepository.getTasksForUser(user.id)
            }

            tasksFlow.collect { tasks ->
                _uiState.update { state ->
                    state.copy(tasks = filterTasks(tasks, state.filter, state.selectedCategory))
                }
            }
        }
    }

    fun setFilter(filter: TaskFilter) {
        _uiState.update { state ->
            val filteredTasks = when (filter) {
                TaskFilter.ALL -> state.tasks
                TaskFilter.TODAY -> {
                    val startOfDay = DateTimeUtils.getStartOfDay()
                    val endOfDay = DateTimeUtils.getEndOfDay()
                    state.tasks.filter { it.dueDate != null && it.dueDate in startOfDay..endOfDay }
                }
                TaskFilter.PENDING -> state.tasks.filter { it.status != TaskStatus.COMPLETED }
                TaskFilter.COMPLETED -> state.tasks.filter { it.status == TaskStatus.COMPLETED }
            }
            state.copy(filter = filter, tasks = filterTasks(filteredTasks, filter, state.selectedCategory))
        }
        loadTasks(_uiState.value.currentUser)
    }

    fun setCategory(category: Category?) {
        _uiState.update { state ->
            state.copy(selectedCategory = category)
        }
        loadTasks(_uiState.value.currentUser)
    }

    private fun filterTasks(tasks: List<Task>, filter: TaskFilter, category: Category?): List<Task> {
        var filtered = when (filter) {
            TaskFilter.ALL -> tasks
            TaskFilter.TODAY -> {
                val startOfDay = DateTimeUtils.getStartOfDay()
                val endOfDay = DateTimeUtils.getEndOfDay()
                tasks.filter { it.dueDate != null && it.dueDate in startOfDay..endOfDay }
            }
            TaskFilter.PENDING -> tasks.filter { it.status != TaskStatus.COMPLETED }
            TaskFilter.COMPLETED -> tasks.filter { it.status == TaskStatus.COMPLETED }
        }

        if (category != null) {
            filtered = filtered.filter { it.categoryId == category.id }
        }

        return filtered
    }

    fun completeTask(taskId: Long) {
        viewModelScope.launch {
            val userId = _uiState.value.currentUser?.id ?: return@launch
            val task = taskRepository.getTaskById(taskId) ?: return@launch

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

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
        }
    }

    fun onCreateTaskClicked() {
        if (_uiState.value.hasAdminPassword) {
            _uiState.update { it.copy(showPasswordDialog = true, passwordError = false) }
        }
    }

    fun dismissPasswordDialog() {
        _uiState.update { it.copy(showPasswordDialog = false, passwordError = false) }
    }

    fun verifyAdminPassword(password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            userPreferences.adminPasswordHash.first()?.let { storedHash ->
                if (PasswordHasher.verify(password, storedHash)) {
                    _uiState.update { it.copy(showPasswordDialog = false, passwordError = false) }
                    onSuccess()
                } else {
                    _uiState.update { it.copy(passwordError = true) }
                }
            } ?: run {
                _uiState.update { it.copy(showPasswordDialog = false) }
                onSuccess()
            }
        }
    }
}
