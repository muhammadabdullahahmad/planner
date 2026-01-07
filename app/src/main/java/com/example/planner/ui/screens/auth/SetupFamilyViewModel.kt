package com.example.planner.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.preferences.UserPreferences
import com.example.planner.data.repository.AchievementRepository
import com.example.planner.data.repository.CategoryRepository
import com.example.planner.data.repository.UserRepository
import com.example.planner.domain.model.User
import com.example.planner.domain.model.UserRole
import com.example.planner.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FamilyMember(
    val name: String = "",
    val role: UserRole = UserRole.MEMBER,
    val pin: String = "",
    val colorHex: String = Constants.MEMBER_COLORS.first()
)

data class SetupFamilyUiState(
    val adminName: String = "",
    val adminPin: String = "",
    val confirmPin: String = "",
    val members: List<FamilyMember> = emptyList(),
    val currentStep: Int = 0, // 0 = admin setup, 1 = add members, 2 = review
    val isLoading: Boolean = false,
    val error: String? = null,
    val setupComplete: Boolean = false
)

@HiltViewModel
class SetupFamilyViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val achievementRepository: AchievementRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupFamilyUiState())
    val uiState: StateFlow<SetupFamilyUiState> = _uiState.asStateFlow()

    private var usedColorIndex = 0

    fun updateAdminName(name: String) {
        _uiState.update { it.copy(adminName = name, error = null) }
    }

    fun updateAdminPin(pin: String) {
        if (pin.length <= 6) {
            _uiState.update { it.copy(adminPin = pin, error = null) }
        }
    }

    fun updateConfirmPin(pin: String) {
        if (pin.length <= 6) {
            _uiState.update { it.copy(confirmPin = pin, error = null) }
        }
    }

    fun nextStep() {
        val state = _uiState.value

        when (state.currentStep) {
            0 -> {
                // Validate admin info
                if (state.adminName.isBlank()) {
                    _uiState.update { it.copy(error = "Please enter your name") }
                    return
                }
                if (state.adminPin.length < 4) {
                    _uiState.update { it.copy(error = "PIN must be at least 4 digits") }
                    return
                }
                if (state.adminPin != state.confirmPin) {
                    _uiState.update { it.copy(error = "PINs don't match") }
                    return
                }
                usedColorIndex = 1 // Admin uses first color
                _uiState.update { it.copy(currentStep = 1, error = null) }
            }
            1 -> {
                // Validate all members have names and PINs
                val invalidMember = state.members.find {
                    it.name.isBlank() || it.pin.length < 4
                }
                if (invalidMember != null) {
                    _uiState.update {
                        it.copy(error = "All members need a name and PIN (4+ digits)")
                    }
                    return
                }
                _uiState.update { it.copy(currentStep = 2, error = null) }
            }
        }
    }

    fun previousStep() {
        if (_uiState.value.currentStep > 0) {
            _uiState.update { it.copy(currentStep = it.currentStep - 1, error = null) }
        }
    }

    fun addMember() {
        val newColorHex = Constants.MEMBER_COLORS.getOrElse(usedColorIndex) {
            Constants.MEMBER_COLORS.first()
        }
        usedColorIndex++

        _uiState.update { state ->
            state.copy(
                members = state.members + FamilyMember(colorHex = newColorHex)
            )
        }
    }

    fun updateMember(index: Int, member: FamilyMember) {
        _uiState.update { state ->
            state.copy(
                members = state.members.toMutableList().apply {
                    this[index] = member
                },
                error = null
            )
        }
    }

    fun removeMember(index: Int) {
        _uiState.update { state ->
            state.copy(
                members = state.members.toMutableList().apply {
                    removeAt(index)
                }
            )
        }
    }

    fun completeSetup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Initialize default categories
                categoryRepository.initializeDefaultCategories()

                // Initialize default achievements
                achievementRepository.initializeDefaultAchievements()

                // Create admin user
                val adminUser = User(
                    name = _uiState.value.adminName,
                    role = UserRole.ADMIN,
                    colorHex = Constants.MEMBER_COLORS.first()
                )
                val adminId = userRepository.createUser(adminUser, _uiState.value.adminPin)

                // Create family members
                _uiState.value.members.forEach { member ->
                    val user = User(
                        name = member.name,
                        role = UserRole.MEMBER,
                        colorHex = member.colorHex
                    )
                    userRepository.createUser(user, member.pin)
                }

                // Mark first launch complete and log in admin
                userPreferences.setFirstLaunchComplete()
                userPreferences.setLoggedInUser(adminId)

                _uiState.update { it.copy(isLoading = false, setupComplete = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to setup family: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
