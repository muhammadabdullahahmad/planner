package com.example.planner.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.repository.EventRepository
import com.example.planner.domain.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventDetailUiState(
    val event: Event? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventDetailUiState())
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()

    private var eventId: Long = 0

    fun loadEvent(eventId: Long) {
        this.eventId = eventId
        viewModelScope.launch {
            eventRepository.getEventByIdFlow(eventId).collect { event ->
                _uiState.update { it.copy(event = event, isLoading = false) }
            }
        }
    }

    fun deleteEvent() {
        viewModelScope.launch {
            eventRepository.deleteEvent(eventId)
        }
    }
}
