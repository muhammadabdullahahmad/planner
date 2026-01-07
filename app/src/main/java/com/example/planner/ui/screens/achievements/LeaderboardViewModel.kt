package com.example.planner.ui.screens.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.preferences.UserPreferences
import com.example.planner.data.repository.UserRepository
import com.example.planner.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeaderboardUiState(
    val leaderboard: List<User> = emptyList(),
    val currentUserId: Long? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
            userPreferences.loggedInUserId.collect { userId ->
                _uiState.update { it.copy(currentUserId = userId) }
            }
        }

        viewModelScope.launch {
            userRepository.getLeaderboard().collect { users ->
                _uiState.update { it.copy(leaderboard = users, isLoading = false) }
            }
        }
    }
}
