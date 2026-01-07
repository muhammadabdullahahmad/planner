package com.example.planner.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.preferences.UserPreferences
import com.example.planner.data.repository.AchievementRepository
import com.example.planner.data.repository.UserRepository
import com.example.planner.domain.model.Achievement
import com.example.planner.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val earnedAchievements: List<Achievement> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val achievementRepository: AchievementRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            userPreferences.loggedInUserId.collect { userId ->
                if (userId != null) {
                    userRepository.getUserByIdFlow(userId).collect { user ->
                        _uiState.update { it.copy(user = user, isLoading = false) }
                    }
                }
            }
        }

        viewModelScope.launch {
            userPreferences.loggedInUserId.collect { userId ->
                if (userId != null) {
                    achievementRepository.getEarnedAchievementsForUser(userId).collect { achievements ->
                        _uiState.update { it.copy(earnedAchievements = achievements) }
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.logout()
        }
    }
}
