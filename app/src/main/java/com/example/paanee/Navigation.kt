package com.example.paanee

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.paanee.ui.main.MainScreen
import com.example.paanee.ui.onboarding.OnboardingScreen

@Composable
fun MainNavigation(startDestination: NavKey) {
  val backStack = rememberNavBackStack(startDestination)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Main> {
          MainScreen(onItemClick = { navKey -> backStack.add(navKey) }, modifier = Modifier.safeDrawingPadding())
        }
        entry<Stats> {
          com.example.paanee.ui.stats.StatsScreen()
        }
        entry<Onboarding> {
          OnboardingScreen(
            onOnboardingComplete = { 
              backStack.clear()
              backStack.add(Main) 
            }
          )
        }
      },
  )
}
