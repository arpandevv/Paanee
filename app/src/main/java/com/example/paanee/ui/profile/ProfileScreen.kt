package com.example.paanee.ui.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paanee.R
import com.example.paanee.theme.*

@Composable
fun ProfileScreen(
    userName: String,
    weightKg: Int,
    activityLevel: String,
    dailyGoalMl: Int,
    gender: String,
    onUpdateProfile: (String, Int, String, String) -> Unit,
    onUpdateDailyGoal: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showEditWeightDialog by remember { mutableStateOf(false) }
    var showEditGoalDialog by remember { mutableStateOf(false) }
    var showEditActivityDialog by remember { mutableStateOf(false) }

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
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Info Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = CircleShape,
                                ambientColor = AmbientShadowColor,
                                spotColor = AmbientShadowColor
                            )
                            .clip(CircleShape)
                            .background(SecondaryCyanBg)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_paanee_logo),
                            contentDescription = "User profile logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                    }
                    IconButton(
                        onClick = { showEditNameDialog = true },
                        modifier = Modifier
                            .size(32.dp)
                            .shadow(4.dp, CircleShape)
                            .background(BrandPrimaryEnd, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Name",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = userName.ifBlank { "Arpan Singh" },
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextOnSurface
                )
                Text(
                    text = "${userName.lowercase().replace(" ", "")}@example.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextOnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bento Grid of editable profile stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Weight Card
                ProfileStatsCard(
                    title = "Weight",
                    value = "$weightKg",
                    unit = "kg",
                    icon = "⚖️",
                    onClick = { showEditWeightDialog = true },
                    modifier = Modifier.weight(1f)
                )
                // Activity Card
                ProfileStatsCard(
                    title = "Activity",
                    value = activityLevel.lowercase().replaceFirstChar { it.uppercase() },
                    unit = "",
                    icon = "🏃",
                    onClick = { showEditActivityDialog = true },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Daily Goal Card (spans full width)
            ProfileStatsCard(
                title = "Daily Goal",
                value = String.format("%.1f", dailyGoalMl.toFloat() / 1000f),
                unit = "L",
                icon = "💧",
                onClick = { showEditGoalDialog = true },
                modifier = Modifier.fillMaxWidth(),
                isPrimaryColor = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Go Premium Gold Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = AmbientShadowColor,
                        spotColor = AmbientShadowColor
                    )
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFE6F0FF),
                                Color(0xFFFFFBF0),
                                Color(0xFFF0F7FF)
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Unlock Paanee Pro",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = WaterBlueDark
                        )
                        Text(text = "👑", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Get personalized hydration plans, advanced analytics, and smart integrations.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextOnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            Toast.makeText(context, "Premium purchase is coming soon!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE5A93B) // Gold button
                        ),
                        shape = RoundedCornerShape(9999.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Go Premium",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Settings List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SettingItem(
                    icon = "🔔",
                    title = "Notifications",
                    onClick = { Toast.makeText(context, "Configure in Settings page!", Toast.LENGTH_SHORT).show() }
                )
                SettingItem(
                    icon = "🔗",
                    title = "Connected Apps",
                    onClick = { Toast.makeText(context, "Integrations coming soon!", Toast.LENGTH_SHORT).show() }
                )
                SettingItem(
                    icon = "❓",
                    title = "Help & Support",
                    onClick = { Toast.makeText(context, "Email support@example.com!", Toast.LENGTH_SHORT).show() }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // --- Dialogs ---

        // Edit Name Dialog
        if (showEditNameDialog) {
            var inputName by remember { mutableStateOf(userName) }
            AlertDialog(
                onDismissRequest = { showEditNameDialog = false },
                title = { Text("Edit Name") },
                text = {
                    OutlinedTextField(
                        value = inputName,
                        onValueChange = { inputName = it },
                        singleLine = true,
                        placeholder = { Text("Name") }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (inputName.isNotBlank()) {
                                onUpdateProfile(inputName, weightKg, activityLevel, gender)
                            }
                            showEditNameDialog = false
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditNameDialog = false }) { Text("Cancel") }
                }
            )
        }

        // Edit Weight Dialog
        if (showEditWeightDialog) {
            var inputWeight by remember { mutableStateOf(weightKg.toString()) }
            AlertDialog(
                onDismissRequest = { showEditWeightDialog = false },
                title = { Text("Edit Weight (kg)") },
                text = {
                    OutlinedTextField(
                        value = inputWeight,
                        onValueChange = { inputWeight = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("e.g. 75") }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val w = inputWeight.toIntOrNull()
                            if (w != null && w > 0) {
                                onUpdateProfile(userName, w, activityLevel, gender)
                            }
                            showEditWeightDialog = false
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditWeightDialog = false }) { Text("Cancel") }
                }
            )
        }

        // Edit Daily Goal Dialog
        if (showEditGoalDialog) {
            var inputGoal by remember { mutableStateOf(dailyGoalMl.toString()) }
            AlertDialog(
                onDismissRequest = { showEditGoalDialog = false },
                title = { Text("Edit Daily Goal (ml)") },
                text = {
                    OutlinedTextField(
                        value = inputGoal,
                        onValueChange = { inputGoal = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("e.g. 2500") }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val g = inputGoal.toIntOrNull()
                            if (g != null && g > 0) {
                                onUpdateDailyGoal(g)
                            }
                            showEditGoalDialog = false
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditGoalDialog = false }) { Text("Cancel") }
                }
            )
        }

        // Edit Activity Dialog
        if (showEditActivityDialog) {
            val activities = listOf("SEDENTARY", "MODERATE", "ACTIVE")
            var selectedActivity by remember { mutableStateOf(activityLevel) }
            AlertDialog(
                onDismissRequest = { showEditActivityDialog = false },
                title = { Text("Select Activity Level") },
                text = {
                    Column {
                        activities.forEach { act ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedActivity = act }
                                    .padding(vertical = 12.dp)
                            ) {
                                RadioButton(
                                    selected = selectedActivity == act,
                                    onClick = { selectedActivity = act }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = act.lowercase().replaceFirstChar { it.uppercase() })
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onUpdateProfile(userName, weightKg, selectedActivity, gender)
                            showEditActivityDialog = false
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditActivityDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun ProfileStatsCard(
    title: String,
    value: String,
    unit: String,
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimaryColor: Boolean = false
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
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = SecondaryCyanBg.copy(alpha = 0.4f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 18.sp)
                }
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Card",
                    tint = OutlineColor.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
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
                    color = if (isPrimaryColor) BrandPrimaryEnd else TextOnSurface
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

@Composable
fun SettingItem(
    icon: String,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = icon, fontSize = 20.sp)
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = TextOnSurface
            )
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = OutlineColor.copy(alpha = 0.6f)
        )
    }
}
