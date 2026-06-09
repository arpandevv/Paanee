package com.example.paanee.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.paanee.theme.*
import java.util.Calendar

@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Calculate dynamic stats
    val avgIntakeL = if (uiState.weeklyData.isNotEmpty()) {
        uiState.weeklyData.average() / 1000f
    } else {
        2.2f
    }
    
    val bestDayL = if (uiState.weeklyData.isNotEmpty()) {
        (uiState.weeklyData.maxOrNull() ?: 0) / 1000f
    } else {
        3.1f
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header (Mobile Title)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Activity",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weekly Intake Chart Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = AmbientShadowColor,
                        spotColor = AmbientShadowColor
                    )
                    .background(color = Color.White.copy(alpha = 0.7f), shape = RoundedCornerShape(24.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Weekly Intake",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextOnSurface
                        )
                        Text(
                            text = String.format("%.1fL / Day Avg", avgIntakeL),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = SecondaryCyanText
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Custom Bar Chart
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val weekdays = listOf("M", "T", "W", "T", "F", "S", "S")
                        
                        // We align weeklyData to match weekdays
                        // Default list if weeklyData is incomplete
                        val dataPoints = if (uiState.weeklyData.size == 7) {
                            uiState.weeklyData
                        } else {
                            listOf(1000, 2000, 1625, 2250, 1250, 250, 0)
                        }

                        val maxVal = maxOf(dataPoints.maxOrNull() ?: 0, uiState.dailyGoal, 1000)

                        dataPoints.forEachIndexed { index, value ->
                            val heightFraction = (value.toFloat() / maxVal.toFloat()).coerceIn(0.05f, 1.0f)
                            val isGoalMet = value >= uiState.dailyGoal
                            
                            // Check if this matches "Today" in the weekday sequence (for demo, assume it's index 3 or 4)
                            val isToday = index == 3 // Thursday/Today highlight in mockup

                            Column(
                                modifier = Modifier
                                    .weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                // Rounded Bar
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .fillMaxHeight(heightFraction)
                                        .then(
                                            if (isToday) {
                                                Modifier.shadow(
                                                    elevation = 4.dp,
                                                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                                                    ambientColor = AmbientShadowColor,
                                                    spotColor = AmbientShadowColor
                                                )
                                            } else {
                                                Modifier
                                            }
                                        )
                                        .background(
                                            color = if (isToday) {
                                                BrandPrimaryEnd
                                            } else if (isGoalMet) {
                                                BrandPrimaryStart.copy(alpha = 0.5f)
                                            } else if (value > 0) {
                                                BrandPrimaryStart.copy(alpha = 0.2f)
                                            } else {
                                                OutlineVariantColor.copy(alpha = 0.3f)
                                            },
                                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                        )
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Weekday label
                                Text(
                                    text = weekdays[index],
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    ),
                                    color = if (isToday) TextOnSurface else TextOnSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bento Grid Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Streak Card
                BentoCard(
                    icon = "🔥",
                    iconBgColor = Color(0xFFFFEBEF),
                    title = "Current Streak",
                    value = "${uiState.streak}",
                    unit = "Days",
                    modifier = Modifier.weight(1f)
                )
                // Best Day Card
                BentoCard(
                    icon = "🏆",
                    iconBgColor = Color(0xFFFFF7E6),
                    title = "Best Day",
                    value = String.format("%.1f", bestDayL),
                    unit = "L",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Avg Intake Card (spans full width)
            BentoCard(
                icon = "💧",
                iconBgColor = SecondaryCyanBg.copy(alpha = 0.4f),
                title = "Avg Intake",
                value = String.format("%.1f", avgIntakeL),
                unit = "L / day",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Insight Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = BrandPrimaryStart.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = BrandPrimaryStart.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "💡",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Column {
                        Text(
                            text = "Insight",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextOnSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "You drink less on Mondays. Try keeping a water bottle at your desk to start the week hydrated.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextOnSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun BentoCard(
    icon: String,
    iconBgColor: Color,
    title: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = AmbientShadowColor,
                spotColor = AmbientShadowColor
            )
            .background(color = Color.White.copy(alpha = 0.7f), shape = RoundedCornerShape(24.dp))
            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color = iconBgColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextOnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = BrandPrimaryEnd
                )
                if (unit.isNotBlank()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextOnSurfaceVariant,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}
