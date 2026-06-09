package com.example.paanee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.paanee.data.prefs.UserPreferencesRepository
import com.example.paanee.notifications.HydrationReminderWorker
import com.example.paanee.theme.PaaneeTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
  @Inject
  lateinit var preferencesRepository: UserPreferencesRepository

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Schedule Periodic Reminders (every 2 hours)
    val reminderWorkRequest = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
        2, TimeUnit.HOURS
    ).build()
    
    WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
        "hydration_reminder_work",
        ExistingPeriodicWorkPolicy.KEEP,
        reminderWorkRequest
    )

    enableEdgeToEdge()
    setContent {
      PaaneeTheme {
          val userPrefs by preferencesRepository.userPreferencesFlow.collectAsState(initial = null)
          
          Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { 
              userPrefs?.let { prefs ->
                  val startDestination = if (prefs.hasCompletedOnboarding) Main else Onboarding
                  MainNavigation(startDestination) 
              }
          } 
      }
    }
  }
}
