package com.example.paanee.ui.reminders

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paanee.theme.*

data class ReminderItem(
    val id: Int,
    val time: String,
    val label: String,
    val isEnabled: Boolean,
    val isSmartExtra: Boolean = false
)

@Composable
fun RemindersScreen(
    modifier: Modifier = Modifier,
    city: String = ""
) {
    val context = LocalContext.current
    var remindersList by remember {
        mutableStateOf(
            listOf(
                ReminderItem(1, "09:00 AM", "Morning Refresh", true),
                ReminderItem(2, "11:30 AM", "Mid-day Hydration", true),
                ReminderItem(3, "02:00 PM", "Peak Heat Extra", true, isSmartExtra = true),
                ReminderItem(4, "04:30 PM", "Afternoon Sip", false),
                ReminderItem(5, "06:00 PM", "Evening Recovery Extra", true, isSmartExtra = true)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Reminders",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Smart Weather Insight
                item {
                    val weatherCity = if (city.isNotBlank()) city else "your city"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = SecondaryCyanBg.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = SecondaryCyanBg,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                Toast.makeText(context, "Weather reminders optimized!", Toast.LENGTH_SHORT).show()
                            }
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "☀️",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Column {
                                Text(
                                    text = "Smart Weather Boost",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = TextOnSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "It's 38°C in $weatherCity today. We've added 2 extra reminders to help you stay hydrated.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextOnSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Reminders list header
                item {
                    Text(
                        text = "Schedule",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextOnSurface,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                // Individual reminders
                items(remindersList) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onToggle = { isChecked ->
                            remindersList = remindersList.map {
                                if (it.id == reminder.id) it.copy(isEnabled = isChecked) else it
                            }
                        }
                    )
                }

                // Bottom spacer to avoid overlap with FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                Toast.makeText(context, "Custom reminder dialog is coming soon!", Toast.LENGTH_SHORT).show()
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 20.dp)
                .size(56.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    ambientColor = AmbientShadowColor,
                    spotColor = AmbientShadowColor
                )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Reminder",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun ReminderCard(
    reminder: ReminderItem,
    onToggle: (Boolean) -> Unit
) {
    val isSelected = reminder.isEnabled
    val cardBackground = if (reminder.isSmartExtra && isSelected) {
        BrandPrimaryStart.copy(alpha = 0.05f)
    } else {
        Color.White.copy(alpha = 0.7f)
    }
    
    val cardBorderColor = if (reminder.isSmartExtra && isSelected) {
        BrandPrimaryStart.copy(alpha = 0.2f)
    } else {
        Color.White.copy(alpha = 0.5f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = AmbientShadowColor,
                spotColor = AmbientShadowColor
            )
            .background(
                color = cardBackground,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = cardBorderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Icon (water drop or streak fire)
                val iconColor = if (isSelected) {
                    if (reminder.isSmartExtra) Color(0xFFFF9800) else BrandPrimaryEnd
                } else {
                    OutlineColor.copy(alpha = 0.6f)
                }
                
                val iconBg = if (isSelected) {
                    if (reminder.isSmartExtra) Color(0xFFFF9800).copy(alpha = 0.1f) else BrandPrimaryStart.copy(alpha = 0.1f)
                } else {
                    SecondaryCyanBg.copy(alpha = 0.2f)
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color = iconBg, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (reminder.isSmartExtra) "🔥" else "💧",
                        fontSize = 20.sp
                    )
                }

                // Label and Time
                Column {
                    Text(
                        text = reminder.time,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isSelected) TextOnSurface else TextOnSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = reminder.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextOnSurfaceVariant
                    )
                }
            }

            // Switch (Toggle)
            Switch(
                checked = reminder.isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = BrandPrimaryEnd,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = SecondaryCyanBg
                )
            )
        }
    }
}
