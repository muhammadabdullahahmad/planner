package com.example.planner.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.preferences.UserPreferences
import com.example.planner.data.repository.EventRepository
import com.example.planner.data.repository.UserRepository
import com.example.planner.domain.model.Event
import com.example.planner.domain.model.EventType
import com.example.planner.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateEventUiState(
    val title: String = "",
    val description: String = "",
    val eventType: EventType = EventType.FAMILY_EVENT,
    val startDate: Long = System.currentTimeMillis(),
    val startTime: String? = null,
    val isAllDay: Boolean = true,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState: StateFlow<CreateEventUiState> = _uiState.asStateFlow()

    private var currentUserId: Long? = null
    private var userColorHex: String = Constants.MEMBER_COLORS.first()

    init {
        viewModelScope.launch {
            userPreferences.loggedInUserId.collect { userId ->
                currentUserId = userId
                if (userId != null) {
                    val user = userRepository.getUserById(userId)
                    userColorHex = user?.colorHex ?: Constants.MEMBER_COLORS.first()
                }
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title, error = null) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateEventType(type: EventType) {
        _uiState.update { it.copy(eventType = type) }
    }

    fun updateStartDate(date: Long) {
        _uiState.update { it.copy(startDate = date) }
    }

    fun updateIsAllDay(isAllDay: Boolean) {
        _uiState.update { it.copy(isAllDay = isAllDay) }
    }

    fun saveEvent() {
        val state = _uiState.value

        if (state.title.isBlank()) {
            _uiState.update { it.copy(error = "Please enter a title") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            try {
                val event = Event(
                    title = state.title,
                    description = state.description.ifBlank { null },
                    createdByUserId = currentUserId,
                    eventType = state.eventType,
                    startDate = state.startDate,
                    isAllDay = state.isAllDay,
                    startTime = state.startTime,
                    colorHex = userColorHex
                )

                eventRepository.createEvent(event)
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, error = "Failed to create event: ${e.message}")
                }
            }
        }
    }
}
