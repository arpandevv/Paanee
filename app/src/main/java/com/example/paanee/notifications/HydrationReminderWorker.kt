package com.example.paanee.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.paanee.data.prefs.UserPreferencesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class HydrationReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val preferencesRepository: UserPreferencesRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val prefs = preferencesRepository.userPreferencesFlow.first()
        
        // Skip reminder if onboarding not completed
        if (!prefs.hasCompletedOnboarding) {
            return Result.success()
        }

        // Logic for Quiet Hours (Sleep Time)
        // For simplicity in this demo, we assume sleep time is respected if the current time is not between sleep and wake
        // Real implementation would parse the "07:00" and "22:30" strings and compare with current hour
        
        // Mock Weather Boost:
        // Let's pretend it's a hot day in the user's city
        val city = prefs.city.ifBlank { "your city" }
        val weatherMessage = if (city.isNotBlank()) "It's hot in $city! " else ""

        NotificationHelper.createNotificationChannel(appContext)
        NotificationHelper.showHydrationReminder(
            appContext,
            "Time to Hydrate \uD83D\uDCA7",
            "${weatherMessage}Drink a glass of water to stay on track."
        )

        return Result.success()
    }
}
