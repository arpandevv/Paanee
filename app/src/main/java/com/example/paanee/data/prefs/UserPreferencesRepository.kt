package com.example.paanee.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "paanee_settings")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val USER_NAME = stringPreferencesKey("user_name")
        val DAILY_GOAL_ML = intPreferencesKey("daily_goal_ml")
        val WAKE_TIME = stringPreferencesKey("wake_time") // e.g. "07:00"
        val SLEEP_TIME = stringPreferencesKey("sleep_time") // e.g. "22:30"
        val CITY = stringPreferencesKey("city")
        val STREAK = intPreferencesKey("streak")
        val LEVEL = intPreferencesKey("level")
        val LAST_GOAL_MET_DATE = stringPreferencesKey("last_goal_met_date")
        val WEIGHT_KG = intPreferencesKey("weight_kg")
        val ACTIVITY_LEVEL = stringPreferencesKey("activity_level")
        val GENDER = stringPreferencesKey("gender")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data.map { preferences ->
        UserPreferences(
            hasCompletedOnboarding = preferences[HAS_COMPLETED_ONBOARDING] ?: false,
            userName = preferences[USER_NAME] ?: "",
            dailyGoalMl = preferences[DAILY_GOAL_ML] ?: 2000,
            wakeTime = preferences[WAKE_TIME] ?: "07:00",
            sleepTime = preferences[SLEEP_TIME] ?: "22:30",
            city = preferences[CITY] ?: "",
            streak = preferences[STREAK] ?: 0,
            level = preferences[LEVEL] ?: 1,
            lastGoalMetDate = preferences[LAST_GOAL_MET_DATE] ?: "",
            weightKg = preferences[WEIGHT_KG] ?: 70,
            activityLevel = preferences[ACTIVITY_LEVEL] ?: "SEDENTARY",
            gender = preferences[GENDER] ?: "PREFER_NOT_TO_SAY"
        )
    }

    suspend fun updateOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[HAS_COMPLETED_ONBOARDING] = completed
        }
    }

    suspend fun updateDailyGoal(goalMl: Int) {
        dataStore.edit { preferences ->
            preferences[DAILY_GOAL_ML] = goalMl
        }
    }
    
    suspend fun updateCity(city: String) {
        dataStore.edit { preferences ->
            preferences[CITY] = city
        }
    }

    suspend fun updateGamification(streak: Int, level: Int, date: String) {
        dataStore.edit { preferences ->
            preferences[STREAK] = streak
            preferences[LEVEL] = level
            preferences[LAST_GOAL_MET_DATE] = date
        }
    }

    suspend fun updateProfile(name: String, weightKg: Int, activityLevel: String, gender: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = name
            preferences[WEIGHT_KG] = weightKg
            preferences[ACTIVITY_LEVEL] = activityLevel
            preferences[GENDER] = gender
        }
    }
}

data class UserPreferences(
    val hasCompletedOnboarding: Boolean,
    val userName: String,
    val dailyGoalMl: Int,
    val wakeTime: String,
    val sleepTime: String,
    val city: String,
    val streak: Int = 0,
    val level: Int = 1,
    val lastGoalMetDate: String = "",
    val weightKg: Int = 70,
    val activityLevel: String = "SEDENTARY",
    val gender: String = "PREFER_NOT_TO_SAY"
)

