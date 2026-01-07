package com.example.planner.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.preferences.UserPreferences
import com.example.planner.data.repository.EventRepository
import com.example.planner.data.repository.TaskRepository
import com.example.planner.data.repository.UserRepository
import com.example.planner.domain.model.Event
import com.example.planner.domain.model.Task
import com.example.planner.domain.model.User
import com.example.planner.domain.model.UserRole
import com.example.planner.util.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class CalendarUiState(
    val currentUser: User? = null,
    val selectedMonth: Long = System.currentTimeMillis(),
    val selectedDate: Long = System.currentTimeMillis(),
    val eventsForSelectedDate: List<Event> = emptyList(),
    val tasksForSelectedDate: List<Task> = emptyList(),
    val eventsOnDates: Map<Int, List<String>> = emptyMap(), // day -> list of color hex
    val tasksOnDates: Map<Int, Int> = emptyMap(), // day -> count
    val isLoading: Boolean = true
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            userPreferences.loggedInUserId.collect { userId ->
                if (userId != null) {
                    val user = userRepository.getUserById(userId)
                    _uiState.update { it.copy(currentUser = user, isLoading = false) }
                    loadMonthData()
                    loadSelectedDateData()
                }
            }
        }
    }

    fun previousMonth() {
        val calendar = Calendar.getInstance().apply { timeInMillis = _uiState.value.selectedMonth }
        calendar.add(Calendar.MONTH, -1)
        _uiState.update { it.copy(selectedMonth = calendar.timeInMillis) }
        loadMonthData()
    }

    fun nextMonth() {
        val calendar = Calendar.getInstance().apply { timeInMillis = _uiState.value.selectedMonth }
        calendar.add(Calendar.MONTH, 1)
        _uiState.update { it.copy(selectedMonth = calendar.timeInMillis) }
        loadMonthData()
    }

    fun selectDate(date: Long) {
        _uiState.update { it.copy(selectedDate = date) }
        loadSelectedDateData()
    }

    private fun loadMonthData() {
        val startOfMonth = DateTimeUtils.getStartOfMonth(_uiState.value.selectedMonth)
        val endOfMonth = DateTimeUtils.getEndOfMonth(_uiState.value.selectedMonth)

        viewModelScope.launch {
            eventRepository.getEventsForMonth(startOfMonth, endOfMonth).collect { events ->
                val eventsByDay = events.groupBy { event ->
                    Calendar.getInstance().apply { timeInMillis = event.startDate }
                        .get(Calendar.DAY_OF_MONTH)
                }.mapValues { (_, events) -> events.map { it.colorHex } }
                _uiState.update { it.copy(eventsOnDates = eventsByDay) }
            }
        }

        viewModelScope.launch {
            val user = _uiState.value.currentUser ?: return@launch
            val tasksFlow = if (user.role == UserRole.ADMIN) {
                taskRepository.getTasksForDate(startOfMonth, endOfMonth)
            } else {
                taskRepository.getTasksForUserOnDate(user.id, startOfMonth, endOfMonth)
            }

            tasksFlow.collect { tasks ->
                val tasksByDay = tasks.groupBy { task ->
                    task.dueDate?.let { date ->
                        Calendar.getInstance().apply { timeInMillis = date }
                            .get(Calendar.DAY_OF_MONTH)
                    } ?: 0
                }.filterKeys { it > 0 }
                    .mapValues { (_, tasks) -> tasks.size }
                _uiState.update { it.copy(tasksOnDates = tasksByDay) }
            }
        }
    }

    private fun loadSelectedDateData() {
        val startOfDay = DateTimeUtils.getStartOfDay(_uiState.value.selectedDate)
        val endOfDay = DateTimeUtils.getEndOfDay(_uiState.value.selectedDate)

        viewModelScope.launch {
            eventRepository.getEventsForDate(startOfDay, endOfDay).collect { events ->
                _uiState.update { it.copy(eventsForSelectedDate = events) }
            }
        }

        viewModelScope.launch {
            val user = _uiState.value.currentUser ?: return@launch
            val tasksFlow = if (user.role == UserRole.ADMIN) {
                taskRepository.getTasksForDate(startOfDay, endOfDay)
            } else {
                taskRepository.getTasksForUserOnDate(user.id, startOfDay, endOfDay)
            }

            tasksFlow.collect { tasks ->
                _uiState.update { it.copy(tasksForSelectedDate = tasks) }
            }
        }
    }
}
