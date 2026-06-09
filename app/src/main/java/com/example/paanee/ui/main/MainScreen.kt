package com.example.paanee.ui.main

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import com.example.paanee.R
import com.example.paanee.theme.*
import com.example.paanee.ui.profile.ProfileScreen
import com.example.paanee.ui.reminders.RemindersScreen
import com.example.paanee.ui.stats.StatsScreen
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showLogSheet by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (val state = uiState) {
            is MainScreenUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is MainScreenUiState.Success -> {
                MainShell(
                    state = state,
                    selectedTab = selectedTab,
                    onTabSelect = { selectedTab = it },
                    onAddWater = viewModel::addWater,
                    onUpdateProfile = viewModel::updateProfile,
                    onUpdateDailyGoal = viewModel::updateDailyGoal,
                    showLogSheet = showLogSheet,
                    onShowLogSheetChange = { showLogSheet = it }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainShell(
    state: MainScreenUiState.Success,
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
    onAddWater: (Int, String) -> Unit,
    onUpdateProfile: (String, Int, String, String) -> Unit,
    onUpdateDailyGoal: (Int) -> Unit,
    showLogSheet: Boolean,
    onShowLogSheetChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BrandBackgroundStart, BrandBackgroundEnd)
                )
            )
            .drawBehind {
                // Draw dot grid pattern
                val dotRadius = 1.dp.toPx()
                val dotSpacing = 24.dp.toPx()
                val dotColor = Color(0xFF005BC0).copy(alpha = 0.03f)
                
                var x = 0f
                while (x < size.width) {
                    var y = 0f
                    while (y < size.height) {
                        drawCircle(
                            color = dotColor,
                            radius = dotRadius,
                            center = Offset(x, y)
                        )
                        y += dotSpacing
                    }
                    x += dotSpacing
                }
                
                // Draw top-left radial blob
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BrandPrimaryStart.copy(alpha = 0.18f),
                            Color.Transparent
                        ),
                        center = Offset(0f, 0f),
                        radius = 450.dp.toPx()
                    ),
                    radius = 450.dp.toPx(),
                    center = Offset(0f, 0f)
                )
                
                // Draw bottom-right radial blob
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            SecondaryCyanBg.copy(alpha = 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(size.width, size.height),
                        radius = 400.dp.toPx()
                    ),
                    radius = 400.dp.toPx(),
                    center = Offset(size.width, size.height)
                )

                // Draw mid-right radial blob
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFD6E3FF).copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        center = Offset(size.width, size.height * 0.45f),
                        radius = 300.dp.toPx()
                    ),
                    radius = 300.dp.toPx(),
                    center = Offset(size.width, size.height * 0.45f)
                )
            }
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                MainHeader(userName = state.userName)
            },
            bottomBar = {
                MainBottomNavigation(
                    selectedTab = selectedTab,
                    onTabSelect = onTabSelect
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedTab) {
                    0 -> DashboardTab(
                        state = state,
                        onAddWater = { amount -> onAddWater(amount, "Water") },
                        onCustomClick = { onShowLogSheetChange(true) }
                    )
                    1 -> StatsScreen(
                        modifier = Modifier.fillMaxSize()
                    )
                    2 -> RemindersScreen(
                        city = state.city,
                        modifier = Modifier.fillMaxSize()
                    )
                    3 -> ProfileScreen(
                        userName = state.userName,
                        weightKg = state.weightKg,
                        activityLevel = state.activityLevel,
                        dailyGoalMl = state.dailyGoalMl,
                        gender = state.gender,
                        onUpdateProfile = onUpdateProfile,
                        onUpdateDailyGoal = onUpdateDailyGoal,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Custom Log Intake Bottom Sheet
        if (showLogSheet) {
            ModalBottomSheet(
                onDismissRequest = { onShowLogSheetChange(false) },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                containerColor = Color.White,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .width(48.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(OutlineVariantColor.copy(alpha = 0.5f))
                    )
                }
            ) {
                LogIntakeSheetContent(
                    onLog = { amount, type ->
                        onAddWater(amount, type)
                        onShowLogSheetChange(false)
                    },
                    onDismiss = { onShowLogSheetChange(false) }
                )
            }
        }
    }
}

@Composable
fun MainHeader(userName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Color.White.copy(alpha = 0.6f))
            .border(
                width = 1.dp,
                brush = SolidColor(Color.White.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(0.dp)
            )
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = "Paanee Icon",
                    tint = BrandPrimaryEnd,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "Paanee",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandPrimaryEnd,
                        fontSize = 24.sp
                    )
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SecondaryCyanBg)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_paanee_logo),
                    contentDescription = "User Profile Avatar",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                )
            }
        }
    }
}

@Composable
fun MainBottomNavigation(
    selectedTab: Int,
    onTabSelect: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                ambientColor = AmbientShadowColor,
                spotColor = AmbientShadowColor
            )
            .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                NavigationTabItem("Home", Icons.Default.Home),
                NavigationTabItem("Stats", Icons.Default.Assessment),
                NavigationTabItem("Reminders", Icons.Default.Notifications),
                NavigationTabItem("Profile", Icons.Default.Person)
            )

            tabs.forEachIndexed { index, tab ->
                val isActive = selectedTab == index
                if (isActive) {
                    Row(
                        modifier = Modifier
                            .height(44.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(9999.dp),
                                ambientColor = AmbientShadowColor,
                                spotColor = AmbientShadowColor
                            )
                            .background(BrandPrimaryEnd, RoundedCornerShape(9999.dp))
                            .clickable { onTabSelect(index) }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .size(60.dp)
                            .clickable { onTabSelect(index) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = TextOnSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextOnSurfaceVariant.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            }
        }
    }
}

data class NavigationTabItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun DashboardTab(
    state: MainScreenUiState.Success,
    onAddWater: (Int) -> Unit,
    onCustomClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Determine Greeting based on time of day
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..21 -> "Good Evening"
            else -> "Good Night"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hello Greeting Banner
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$greeting, ${state.userName} 👋",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = TextOnSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Let's reach your hydration goal today.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextOnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Circular Progress Gauge Composable
        WaterProgressCircleGauge(
            currentMl = state.currentIntakeMl,
            goalMl = state.dailyGoalMl
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Quick Log Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Quick Log",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = TextOnSurface,
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
            )
        }

        // Quick Log Bento Cards Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickLogBentoCard(
                amountMl = 250,
                iconText = "🥤",
                onClick = { onAddWater(250) },
                modifier = Modifier.weight(1f)
            )
            QuickLogBentoCard(
                amountMl = 500,
                iconText = "🍼",
                onClick = { onAddWater(500) },
                modifier = Modifier.weight(1f)
            )
            QuickLogCustomCard(
                onClick = onCustomClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Streak Card
        StreakBentoCard(streakDays = state.streak)

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun WaterProgressCircleGauge(
    currentMl: Int,
    goalMl: Int
) {
    val progress = (currentMl.toFloat() / goalMl.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "gauge_progress"
    )

    Box(
        modifier = Modifier
            .size(260.dp)
            .shadow(
                elevation = 6.dp,
                shape = CircleShape,
                ambientColor = AmbientShadowColor,
                spotColor = AmbientShadowColor
            )
            .background(Color.White.copy(alpha = 0.4f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Draw background track
            drawArc(
                color = SecondaryCyanBg.copy(alpha = 0.6f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw progress track
            drawArc(
                brush = Brush.linearGradient(
                    colors = listOf(BrandPrimaryStart, BrandPrimaryEnd)
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Inner Glassmorphic Circle
        Box(
            modifier = Modifier
                .size(170.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = CircleShape,
                    ambientColor = AmbientShadowColor.copy(alpha = 0.5f),
                    spotColor = AmbientShadowColor.copy(alpha = 0.5f)
                )
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.75f))
                .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Large amount in L (Liters)
                val currentL = currentMl / 1000f
                val goalL = goalMl / 1000f

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = String.format(java.util.Locale.US, "%.1f", currentL),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandPrimaryEnd,
                            letterSpacing = (-0.02).sp
                        )
                    )
                    Text(
                        text = "L",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = BrandPrimaryEnd
                        ),
                        modifier = Modifier.padding(bottom = 6.dp, start = 1.dp)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "of ${String.format(java.util.Locale.US, "%.1f", goalL)}L",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextOnSurfaceVariant.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.graphicsLayer { alpha = 0.8f }
                )
            }
        }
    }
}

@Composable
fun QuickLogBentoCard(
    amountMl: Int,
    iconText: String,
    onClick: () -> Unit,
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
            .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(24.dp))
            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(SecondaryCyanBg.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = iconText, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "$amountMl ml",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextOnSurface,
                    fontSize = 14.sp
                )
            )
        }
    }
}

@Composable
fun QuickLogCustomCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = AmbientShadowColor,
                spotColor = AmbientShadowColor
            )
            .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(24.dp))
            .border(
                BorderStroke(2.dp, Brush.sweepGradient(listOf(BrandPrimaryStart.copy(alpha = 0.4f), BrandPrimaryEnd.copy(alpha = 0.4f)))),
                RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(OutlineVariantColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Custom Log",
                    tint = TextOnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Custom",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextOnSurface,
                    fontSize = 14.sp
                )
            )
        }
    }
}

@Composable
fun StreakBentoCard(streakDays: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = AmbientShadowColor,
                spotColor = AmbientShadowColor
            )
            .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(24.dp))
            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFFFDAD6), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔥", fontSize = 24.sp)
                }

                Column {
                    Text(
                        text = "Today's Streak",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextOnSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Keep it up!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextOnSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "$streakDays",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandPrimaryEnd
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "days",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextOnSurfaceVariant
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
fun LogIntakeSheetContent(
    onLog: (Int, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDrinkType by remember { mutableStateOf("Water") }
    var currentVolume by remember { mutableIntStateOf(250) }

    val drinkTypes = listOf(
        DrinkTypeItem("Water", "💧"),
        DrinkTypeItem("Juice", "🍹"),
        DrinkTypeItem("Tea", "🍵"),
        DrinkTypeItem("Coffee", "☕")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Drag indicator padding is handled in Scaffold container, start with title
        Text(
            text = "Log Intake",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = TextOnSurface,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal chips row
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            drinkTypes.forEach { type ->
                val isSelected = selectedDrinkType == type.name
                val chipBgColor = if (isSelected) BrandPrimaryEnd else SecondaryCyanBg.copy(alpha = 0.3f)
                val chipTextColor = if (isSelected) Color.White else TextOnSurface

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(chipBgColor)
                        .clickable { selectedDrinkType = type.name }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = type.icon, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = type.name,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = chipTextColor,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Amount input area with plus/minus
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
        ) {
            // Minus Button
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(SecondaryCyanBg.copy(alpha = 0.6f))
                    .clickable {
                        if (currentVolume > 50) {
                            currentVolume -= 50
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease Volume",
                    tint = TextOnSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Central display number
            Box(
                modifier = Modifier.width(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$currentVolume",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandPrimaryEnd,
                            letterSpacing = (-0.02).sp
                        )
                    )
                    Text(
                        text = "ml",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = SecondaryCyanText,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }

            // Plus Button
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(BrandPrimaryEnd)
                    .clickable {
                        currentVolume += 50
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase Volume",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Quick add chips: +100, +250, +500
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(100, 250, 500).forEach { amt ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(SecondaryCyanBg.copy(alpha = 0.5f))
                        .clickable { currentVolume += amt }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "+$amt",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurfaceVariant
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Log It Action Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(9999.dp),
                    ambientColor = AmbientShadowColor,
                    spotColor = AmbientShadowColor
                )
                .clip(RoundedCornerShape(9999.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(BrandPrimaryStart, BrandPrimaryEnd)
                    )
                )
                .clickable { onLog(currentVolume, selectedDrinkType) },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Log It",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "💧",
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

data class DrinkTypeItem(
    val name: String,
    val icon: String
)
