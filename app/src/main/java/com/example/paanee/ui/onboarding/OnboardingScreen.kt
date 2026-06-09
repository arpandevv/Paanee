package com.example.paanee.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.paanee.R
import com.example.paanee.theme.*


@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentStep by remember { mutableIntStateOf(0) }

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
                            BrandPrimaryStart.copy(alpha = 0.22f),
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
                            SecondaryCyanBg.copy(alpha = 0.5f),
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
                            Color(0xFFD6E3FF).copy(alpha = 0.35f),
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
        // Content Wrapper
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp)
        ) {
            when (currentStep) {
                0 -> WelcomeStep { currentStep++ }
                1 -> NameStep(
                    name = uiState.name,
                    onNameChange = viewModel::updateName,
                    onNext = { currentStep++ }
                )
                2 -> WeightGenderStep(
                    weight = uiState.weight,
                    onWeightChange = viewModel::updateWeight,
                    gender = uiState.gender,
                    onGenderChange = viewModel::updateGender,
                    onNext = { currentStep++ }
                )
                3 -> ActivityStep(
                    activityLevel = uiState.activityLevel,
                    onActivityChange = viewModel::updateActivityLevel,
                    onComplete = {
                        viewModel.completeOnboarding(onOnboardingComplete)
                    }
                )
            }
        }
    }
}

@Composable
fun StepIndicator(activeStep: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in 0..2) {
            val isActive = i == activeStep
            val isCompleted = i < activeStep
            val color = if (isActive || isCompleted) BrandPrimaryEnd else OutlineVariantColor.copy(alpha = 0.5f)
            val weight = if (isActive) 2f else 1f
            Box(
                modifier = Modifier
                    .weight(weight)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun GradientPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val alpha = if (enabled) 1f else 0.5f
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer { this.alpha = alpha }
            .then(
                if (enabled) {
                    Modifier.shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(9999.dp),
                        ambientColor = AmbientShadowColor,
                        spotColor = AmbientShadowColor
                    )
                } else {
                    Modifier
                }
            )
            .clip(RoundedCornerShape(9999.dp))
            .then(
                if (enabled) {
                    Modifier.background(
                        Brush.linearGradient(
                            colors = listOf(BrandPrimaryStart, BrandPrimaryEnd)
                        )
                    )
                } else {
                    Modifier.background(Color.Gray.copy(alpha = 0.4f))
                }
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
fun WelcomeStep(onNext: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val translateY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoFloat"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(220.dp),
                contentAlignment = Alignment.Center
            ) {
                // Soft radial glow behind the logo
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    BrandPrimaryStart.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_paanee_logo),
                    contentDescription = "Paanee Logo",
                    modifier = Modifier
                        .graphicsLayer { translationY = translateY.dp.toPx() }
                        .size(180.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Paanee 💧",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 40.sp),
                color = WaterBluePrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your smart hydration companion for a healthier, refreshed you.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextOnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GradientPillButton(
                text = "Get Started",
                onClick = onNext
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Already have an account? Log in",
                color = OutlineColor,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { /* Log in option */ }
            )
        }
    }
}

@Composable
fun NameStep(name: String, onNameChange: (String) -> Unit, onNext: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val glowModifier = if (isFocused) {
        Modifier.shadow(
            elevation = 6.dp,
            shape = RoundedCornerShape(16.dp),
            ambientColor = BrandPrimaryStart.copy(alpha = 0.35f),
            spotColor = BrandPrimaryStart.copy(alpha = 0.35f)
        )
    } else {
        Modifier
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            StepIndicator(activeStep = 0)
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "What should we call you?",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = TextOnSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enter your name to personalize your hydration journey.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextOnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                placeholder = { Text("Enter your name", style = MaterialTheme.typography.bodyMedium, color = TextOnSurfaceVariant.copy(alpha = 0.6f)) },
                singleLine = true,
                interactionSource = interactionSource,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextOnSurface),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPrimaryEnd,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = SecondaryCyanBg.copy(alpha = 0.2f),
                    unfocusedContainerColor = SecondaryCyanBg.copy(alpha = 0.2f),
                    focusedTextColor = TextOnSurface,
                    unfocusedTextColor = TextOnSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .then(glowModifier)
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp)
            )
        }

        GradientPillButton(
            text = "Next",
            onClick = onNext,
            enabled = name.isNotBlank()
        )
    }
}

@Composable
fun WeightGenderStep(
    weight: String,
    onWeightChange: (String) -> Unit,
    gender: Gender,
    onGenderChange: (Gender) -> Unit,
    onNext: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val glowModifier = if (isFocused) {
        Modifier.shadow(
            elevation = 6.dp,
            shape = RoundedCornerShape(16.dp),
            ambientColor = BrandPrimaryStart.copy(alpha = 0.35f),
            spotColor = BrandPrimaryStart.copy(alpha = 0.35f)
        )
    } else {
        Modifier
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            StepIndicator(activeStep = 1)
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "A bit about you",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = TextOnSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "These details will help us calculate your recommended daily water target.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextOnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Weight (kg)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = TextOnSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = weight,
                onValueChange = onWeightChange,
                placeholder = { Text("e.g. 70", style = MaterialTheme.typography.bodyMedium, color = TextOnSurfaceVariant.copy(alpha = 0.6f)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                interactionSource = interactionSource,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextOnSurface),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPrimaryEnd,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = SecondaryCyanBg.copy(alpha = 0.2f),
                    unfocusedContainerColor = SecondaryCyanBg.copy(alpha = 0.2f),
                    focusedTextColor = TextOnSurface,
                    unfocusedTextColor = TextOnSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .then(glowModifier)
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Gender",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = TextOnSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Stacked column of gender selections to ensure no squishing of "Prefer not to say"
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Gender.entries.forEach { g ->
                    val isSelected = gender == g
                    val displayName = g.name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .then(
                                if (isSelected) {
                                    Modifier.shadow(
                                        elevation = 6.dp,
                                        shape = RoundedCornerShape(9999.dp),
                                        ambientColor = AmbientShadowColor,
                                        spotColor = AmbientShadowColor
                                    )
                                } else {
                                    Modifier
                                }
                            )
                            .clip(RoundedCornerShape(9999.dp))
                            .background(
                                if (isSelected) {
                                    Brush.linearGradient(
                                        colors = listOf(BrandPrimaryStart, BrandPrimaryEnd)
                                    )
                                } else {
                                    SolidColor(SecondaryCyanBg.copy(alpha = 0.3f))
                                }
                            )
                            .clickable { onGenderChange(g) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayName,
                            color = if (isSelected) Color.White else TextOnSurface,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        GradientPillButton(
            text = "Next",
            onClick = onNext,
            enabled = weight.isNotBlank()
        )
    }
}

@Composable
fun ActivityStep(
    activityLevel: ActivityLevel,
    onActivityChange: (ActivityLevel) -> Unit,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            StepIndicator(activeStep = 2)
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "How active are you?",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = TextOnSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "This helps us calculate your water needs based on daily energy levels.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextOnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ActivityOption(
                    title = "Sedentary",
                    desc = "Little to no exercise",
                    selected = activityLevel == ActivityLevel.SEDENTARY,
                    onClick = { onActivityChange(ActivityLevel.SEDENTARY) }
                )
                ActivityOption(
                    title = "Moderate",
                    desc = "Light exercise 1-3 days a week",
                    selected = activityLevel == ActivityLevel.MODERATE,
                    onClick = { onActivityChange(ActivityLevel.MODERATE) }
                )
                ActivityOption(
                    title = "Active",
                    desc = "Hard exercise 3-5 days a week",
                    selected = activityLevel == ActivityLevel.ACTIVE,
                    onClick = { onActivityChange(ActivityLevel.ACTIVE) }
                )
            }
        }

        GradientPillButton(
            text = "Complete Setup",
            onClick = onComplete
        )
    }
}

@Composable
fun ActivityOption(title: String, desc: String, selected: Boolean, onClick: () -> Unit) {
    val containerColor = if (selected) SecondaryCyanBg.copy(alpha = 0.4f) else Color.White
    val borderColor = if (selected) BrandPrimaryEnd else CyanBorder
    val borderWidth = if (selected) 2.dp else 1.dp

    val cardShadow = if (selected) {
        Modifier.shadow(
            elevation = 6.dp,
            shape = RoundedCornerShape(24.dp),
            ambientColor = AmbientShadowColor,
            spotColor = AmbientShadowColor
        )
    } else {
        Modifier.shadow(
            elevation = 3.dp,
            shape = RoundedCornerShape(24.dp),
            ambientColor = AmbientShadowColor.copy(alpha = 0.5f),
            spotColor = AmbientShadowColor.copy(alpha = 0.5f)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(cardShadow)
            .background(
                color = containerColor,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                border = BorderStroke(borderWidth, borderColor),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextOnSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextOnSurfaceVariant
                )
            }

            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = BrandPrimaryEnd,
                    unselectedColor = OutlineColor.copy(alpha = 0.5f)
                )
            )
        }
    }
}
