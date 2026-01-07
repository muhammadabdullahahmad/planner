package com.example.planner.ui.screens.members

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.preferences.UserPreferences
import com.example.planner.data.repository.UserRepository
import com.example.planner.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MembersUiState(
    val currentUser: User? = null,
    val members: List<User> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class MembersViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(MembersUiState())
    val uiState: StateFlow<MembersUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            userPreferences.loggedInUserId.collect { userId ->
                if (userId != null) {
                    val user = userRepository.getUserById(userId)
                    _uiState.update { it.copy(currentUser = user, isLoading = false) }
                }
            }
        }

        viewModelScope.launch {
            userRepository.getAllUsers().collect { users ->
                _uiState.update { it.copy(members = users) }
            }
        }
    }
}
