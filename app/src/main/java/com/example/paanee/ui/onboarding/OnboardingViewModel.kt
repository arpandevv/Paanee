package com.example.paanee.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paanee.data.prefs.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateWeight(weight: String) {
        _uiState.value = _uiState.value.copy(weight = weight)
    }

    fun updateActivityLevel(level: ActivityLevel) {
        _uiState.value = _uiState.value.copy(activityLevel = level)
    }
    
    fun updateGender(gender: Gender) {
        _uiState.value = _uiState.value.copy(gender = gender)
    }

    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            val weightKg = state.weight.toIntOrNull() ?: 70
            
            // Basic hydration calculation: 
            // 35ml per kg of body weight
            // + 500ml if Active, + 250ml if Moderate
            var goalMl = weightKg * 35
            when (state.activityLevel) {
                ActivityLevel.ACTIVE -> goalMl += 500
                ActivityLevel.MODERATE -> goalMl += 250
                ActivityLevel.SEDENTARY -> {}
            }
            
            // For males, add slightly more (general guideline)
            if (state.gender == Gender.MALE) {
                goalMl += 300
            }

            // Save to preferences
            preferencesRepository.updateProfile(state.name, weightKg, state.activityLevel.name, state.gender.name)
            preferencesRepository.updateDailyGoal(goalMl)
            preferencesRepository.updateOnboardingCompleted(true)
            
            onComplete()
        }
    }
}

data class OnboardingUiState(
    val name: String = "",
    val weight: String = "",
    val activityLevel: ActivityLevel = ActivityLevel.SEDENTARY,
    val gender: Gender = Gender.PREFER_NOT_TO_SAY
)

enum class ActivityLevel {
    SEDENTARY, MODERATE, ACTIVE
}

enum class Gender {
    MALE, FEMALE, PREFER_NOT_TO_SAY
}
