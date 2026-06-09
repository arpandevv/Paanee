package com.example.paanee.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paanee.data.local.HydrationDao
import com.example.paanee.data.local.HydrationLog
import com.example.paanee.data.prefs.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val hydrationDao: HydrationDao,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val todayStartAndEnd: Pair<Long, Long>
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val start = calendar.timeInMillis
            
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val end = calendar.timeInMillis
            return Pair(start, end)
        }

    val uiState: StateFlow<MainScreenUiState> = combine(
        hydrationDao.getTotalIntakeForDay(todayStartAndEnd.first, todayStartAndEnd.second),
        preferencesRepository.userPreferencesFlow
    ) { currentIntake, prefs ->
        MainScreenUiState.Success(
            currentIntakeMl = currentIntake ?: 0,
            dailyGoalMl = prefs.dailyGoalMl,
            userName = prefs.userName,
            streak = prefs.streak,
            level = prefs.level,
            weightKg = prefs.weightKg,
            activityLevel = prefs.activityLevel,
            gender = prefs.gender,
            city = prefs.city
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainScreenUiState.Loading
    )

    fun addWater(amountMl: Int, drinkType: String = "Water") {
        viewModelScope.launch {
            val log = HydrationLog(
                amountMl = amountMl,
                drinkType = drinkType,
                timestamp = System.currentTimeMillis()
            )
            hydrationDao.insertLog(log)
            checkAndApplyGamification()
        }
    }
    
    private suspend fun checkAndApplyGamification() {
        val prefs = preferencesRepository.userPreferencesFlow.first()
        val totalIntake = hydrationDao.getTotalIntakeForDay(todayStartAndEnd.first, todayStartAndEnd.second).first() ?: 0
        
        if (totalIntake >= prefs.dailyGoalMl) {
            val cal = Calendar.getInstance()
            val todayStr = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}-${cal.get(Calendar.DAY_OF_MONTH)}"
            
            if (prefs.lastGoalMetDate != todayStr) {
                // Goal met for the first time today!
                var newStreak = prefs.streak + 1
                
                // If they missed yesterday, reset streak
                cal.add(Calendar.DAY_OF_YEAR, -1)
                val yesterdayStr = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}-${cal.get(Calendar.DAY_OF_MONTH)}"
                
                if (prefs.lastGoalMetDate != "" && prefs.lastGoalMetDate != yesterdayStr) {
                    newStreak = 1
                }
                
                val newLevel = prefs.level + (newStreak / 5) // Level up every 5 days streak
                
                preferencesRepository.updateGamification(newStreak, newLevel, todayStr)
            }
        }
    }

    fun updateDailyGoal(goalMl: Int) {
        viewModelScope.launch {
            preferencesRepository.updateDailyGoal(goalMl)
        }
    }

    fun updateProfile(name: String, weightKg: Int, activityLevel: String, gender: String) {
        viewModelScope.launch {
            preferencesRepository.updateProfile(name, weightKg, activityLevel, gender)
        }
    }
}

sealed interface MainScreenUiState {
    object Loading : MainScreenUiState
    data class Success(
        val currentIntakeMl: Int,
        val dailyGoalMl: Int,
        val userName: String,
        val streak: Int,
        val level: Int,
        val weightKg: Int,
        val activityLevel: String,
        val gender: String,
        val city: String
    ) : MainScreenUiState
}
