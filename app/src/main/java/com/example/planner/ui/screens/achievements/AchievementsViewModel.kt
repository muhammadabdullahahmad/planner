package com.example.planner.ui.screens.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.preferences.UserPreferences
import com.example.planner.data.repository.AchievementRepository
import com.example.planner.domain.model.Achievement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AchievementsUiState(
    val allAchievements: List<Achievement> = emptyList(),
    val earnedAchievements: List<Achievement> = emptyList(),
    val lockedAchievements: List<Achievement> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val achievementRepository: AchievementRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    init {
        loadAchievements()
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            userPreferences.loggedInUserId.collect { userId ->
                if (userId != null) {
                    // Load earned achievements
                    achievementRepository.getEarnedAchievementsForUser(userId).collect { earned ->
                        _uiState.update {
                            it.copy(
                                earnedAchievements = earned,
                                allAchievements = earned + it.lockedAchievements,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            userPreferences.loggedInUserId.collect { userId ->
                if (userId != null) {
                    achievementRepository.getUnearnedAchievementsForUser(userId).collect { locked ->
                        _uiState.update {
                            it.copy(
                                lockedAchievements = locked,
                                allAchievements = it.earnedAchievements + locked
                            )
                        }
                    }
                }
            }
        }
    }
}
