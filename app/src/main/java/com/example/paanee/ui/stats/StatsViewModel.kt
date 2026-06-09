package com.example.paanee.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paanee.data.local.HydrationDao
import com.example.paanee.data.prefs.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val hydrationDao: HydrationDao,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            hydrationDao.getAllLogs().collect {
                loadStats()
            }
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            val prefs = preferencesRepository.userPreferencesFlow.first()
            
            val calendar = Calendar.getInstance()
            val weeklyData = mutableListOf<Int>()
            
            // Loop past 7 days
            for (i in 6 downTo 0) {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, -i)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                val start = cal.timeInMillis
                
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                val end = cal.timeInMillis

                val intakeForDay = hydrationDao.getTotalIntakeForDay(start, end).first() ?: 0
                weeklyData.add(intakeForDay)
            }

            _uiState.value = StatsUiState(
                streak = prefs.streak,
                level = prefs.level,
                dailyGoal = prefs.dailyGoalMl,
                weeklyData = weeklyData
            )
        }
    }
}

data class StatsUiState(
    val streak: Int = 0,
    val level: Int = 1,
    val dailyGoal: Int = 2000,
    val weeklyData: List<Int> = emptyList()
)
