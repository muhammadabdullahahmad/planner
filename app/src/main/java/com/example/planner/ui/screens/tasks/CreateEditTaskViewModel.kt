package com.example.planner.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.preferences.UserPreferences
import com.example.planner.data.repository.CategoryRepository
import com.example.planner.data.repository.TaskRepository
import com.example.planner.data.repository.UserRepository
import com.example.planner.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateEditTaskUiState(
    val title: String = "",
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val categoryId: Long? = null,
    val dueDate: Long? = null,
    val dueTime: String? = null,
    val isRecurring: Boolean = false,
    val recurrenceType: RecurrenceType? = null,
    val assignToAll: Boolean = false,
    val selectedAssignees: Set<Long> = emptySet(),
    val categories: List<Category> = emptyList(),
    val members: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CreateEditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateEditTaskUiState())
    val uiState: StateFlow<CreateEditTaskUiState> = _uiState.asStateFlow()

    private var editingTaskId: Long? = null
    private var currentUserId: Long? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }

        viewModelScope.launch {
            userRepository.getAllUsers().collect { users ->
                _uiState.update { it.copy(members = users) }
            }
        }

        viewModelScope.launch {
            userPreferences.loggedInUserId.collect { userId ->
                currentUserId = userId
            }
        }
    }

    fun loadTask(taskId: Long) {
        editingTaskId = taskId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val task = taskRepository.getTaskById(taskId)
            if (task != null) {
                _uiState.update {
                    it.copy(
                        title = task.title,
                        description = task.description ?: "",
                        priority = task.priority,
                        categoryId = task.categoryId,
                        dueDate = task.dueDate,
                        dueTime = task.dueTime,
                        isRecurring = task.isRecurring,
                        recurrenceType = task.recurrenceType,
                        assignToAll = task.assignedToAll,
                        isLoading = false
                    )
                }

                // Load assignees
                taskRepository.getAssigneesForTask(taskId).collect { assignees ->
                    _uiState.update {
                        it.copy(selectedAssignees = assignees.map { a -> a.userId }.toSet())
                    }
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Task not found") }
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title, error = null) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updatePriority(priority: TaskPriority) {
        _uiState.update { it.copy(priority = priority) }
    }

    fun updateCategory(categoryId: Long) {
        _uiState.update { it.copy(categoryId = categoryId) }
    }

    fun updateDueDate(dueDate: Long?) {
        _uiState.update { it.copy(dueDate = dueDate) }
    }

    fun updateRecurring(isRecurring: Boolean) {
        _uiState.update {
            it.copy(
                isRecurring = isRecurring,
                recurrenceType = if (isRecurring) RecurrenceType.WEEKLY else null
            )
        }
    }

    fun updateRecurrenceType(type: RecurrenceType) {
        _uiState.update { it.copy(recurrenceType = type) }
    }

    fun toggleAssignToAll() {
        _uiState.update { it.copy(assignToAll = !it.assignToAll) }
    }

    fun toggleAssignee(userId: Long) {
        _uiState.update { state ->
            val newAssignees = if (userId in state.selectedAssignees) {
                state.selectedAssignees - userId
            } else {
                state.selectedAssignees + userId
            }
            state.copy(selectedAssignees = newAssignees)
        }
    }

    fun saveTask() {
        val state = _uiState.value

        if (state.title.isBlank()) {
            _uiState.update { it.copy(error = "Please enter a title") }
            return
        }

        if (!state.assignToAll && state.selectedAssignees.isEmpty()) {
            _uiState.update { it.copy(error = "Please select at least one assignee") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            try {
                val points = when (state.priority) {
                    TaskPriority.HIGH -> 20
                    TaskPriority.MEDIUM -> 10
                    TaskPriority.LOW -> 5
                }

                val task = Task(
                    id = editingTaskId ?: 0,
                    title = state.title,
                    description = state.description.ifBlank { null },
                    createdByUserId = currentUserId,
                    categoryId = state.categoryId,
                    priority = state.priority,
                    status = TaskStatus.PENDING,
                    dueDate = state.dueDate,
                    dueTime = state.dueTime,
                    isRecurring = state.isRecurring,
                    recurrenceType = state.recurrenceType,
                    pointsValue = points,
                    assignedToAll = state.assignToAll
                )

                val assigneeIds = if (state.assignToAll) {
                    state.members.map { it.id }
                } else {
                    state.selectedAssignees.toList()
                }

                if (editingTaskId != null) {
                    taskRepository.updateTask(task, assigneeIds)
                } else {
                    taskRepository.createTask(task, assigneeIds)
                }

                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = "Failed to save task: ${e.message}"
                    )
                }
            }
        }
    }
}
