package com.example.planner.ui.screens.members

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.repository.UserRepository
import com.example.planner.domain.model.User
import com.example.planner.domain.model.UserRole
import com.example.planner.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddMemberUiState(
    val name: String = "",
    val pin: String = "",
    val confirmPin: String = "",
    val colorHex: String = Constants.MEMBER_COLORS[1],
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddMemberViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddMemberUiState())
    val uiState: StateFlow<AddMemberUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name, error = null) }
    }

    fun updatePin(pin: String) {
        if (pin.length <= 6) {
            _uiState.update { it.copy(pin = pin, error = null) }
        }
    }

    fun updateConfirmPin(pin: String) {
        if (pin.length <= 6) {
            _uiState.update { it.copy(confirmPin = pin, error = null) }
        }
    }

    fun updateColor(colorHex: String) {
        _uiState.update { it.copy(colorHex = colorHex) }
    }

    fun saveMember() {
        val state = _uiState.value

        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "Please enter a name") }
            return
        }

        if (state.pin.length < 4) {
            _uiState.update { it.copy(error = "PIN must be at least 4 digits") }
            return
        }

        if (state.pin != state.confirmPin) {
            _uiState.update { it.copy(error = "PINs don't match") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            try {
                val user = User(
                    name = state.name,
                    role = UserRole.MEMBER,
                    colorHex = state.colorHex
                )

                userRepository.createUser(user, state.pin)
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, error = "Failed to add member: ${e.message}")
                }
            }
        }
    }
}
