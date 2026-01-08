package com.example.planner.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.preferences.UserPreferences
import com.example.planner.data.repository.UserRepository
import com.example.planner.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val users: List<User> = emptyList(),
    val selectedUser: User? = null,
    val pin: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val isFirstLaunch: Boolean = true,
    val loginSuccess: Boolean = false,
    val loggedInUserIsAdmin: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
        checkFirstLaunch()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            userRepository.getAllUsers().collect { users ->
                _uiState.update { it.copy(
                    users = users,
                    isLoading = false,
                    isFirstLaunch = users.isEmpty()
                )}
            }
        }
    }

    private fun checkFirstLaunch() {
        viewModelScope.launch {
            userPreferences.isFirstLaunch.collect { isFirst ->
                _uiState.update { it.copy(isFirstLaunch = isFirst || it.users.isEmpty()) }
            }
        }
    }

    fun selectUser(user: User) {
        _uiState.update { it.copy(selectedUser = user, pin = "", error = null) }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedUser = null, pin = "", error = null) }
    }

    fun updatePin(pin: String) {
        if (pin.length <= 6) {
            _uiState.update { it.copy(pin = pin, error = null) }
        }
    }

    fun login() {
        val selectedUser = _uiState.value.selectedUser ?: return
        val pin = _uiState.value.pin

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val isValid = userRepository.verifyPin(selectedUser.id, pin)
            if (isValid) {
                userPreferences.setLoggedInUser(selectedUser.id)
                userRepository.updateLastLogin(selectedUser.id, System.currentTimeMillis())
                _uiState.update { it.copy(
                    isLoading = false,
                    loginSuccess = true,
                    loggedInUserIsAdmin = selectedUser.isAdmin
                )}
            } else {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "Incorrect PIN. Please try again."
                )}
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
