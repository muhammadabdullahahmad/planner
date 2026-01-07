package com.example.planner.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.preferences.UserPreferences
import com.example.planner.data.repository.UserRepository
import com.example.planner.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileUiState(
    val user: User? = null,
    val name: String = "",
    val status: String = "Active",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            userPreferences.loggedInUserId.collect { userId ->
                if (userId != null) {
                    val user = userRepository.getUserById(userId)
                    if (user != null) {
                        _uiState.update {
                            it.copy(
                                user = user,
                                name = user.name,
                                status = user.status,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name, error = null) }
    }

    fun updateStatus(status: String) {
        _uiState.update { it.copy(status = status) }
    }

    fun saveProfile() {
        val state = _uiState.value
        val user = state.user ?: return

        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "Please enter your name") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            try {
                val updatedUser = user.copy(
                    name = state.name,
                    status = state.status
                )
                userRepository.updateUser(updatedUser)
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, error = "Failed to save: ${e.message}")
                }
            }
        }
    }
}
