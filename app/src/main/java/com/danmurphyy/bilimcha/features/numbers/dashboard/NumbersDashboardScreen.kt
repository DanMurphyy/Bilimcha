@file:OptIn(ExperimentalMaterial3Api::class)

package com.danmurphyy.bilimcha.features.numbers.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.danmurphyy.bilimcha.data.models.NumbersRange
import com.danmurphyy.bilimcha.navigations.LocalBackStackController
import com.danmurphyy.bilimcha.navigations.LocalSheetController
import com.danmurphyy.bilimcha.navigations.NumbersDashboardKey
import com.danmurphyy.bilimcha.navigations.NumbersPracticeKey
import com.danmurphyy.bilimcha.navigations.NumbersTestKey
import com.danmurphyy.bilimcha.navigations.VisualityType
import com.danmurphyy.bilimcha.ui.theme.KidsNumbers
import com.danmurphyy.bilimcha.uibases.AppHeader
import com.danmurphyy.bilimcha.uibases.BaseScreen
import com.danmurphyy.bilimcha.uibases.DialogBottomSheet
import com.danmurphyy.bilimcha.uibases.DialogSheetData
import com.danmurphyy.bilimcha.uibases.shimmer
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.size
import com.danmurphyy.bilimcha.navigations.QuickRegistrationKey

class NumbersDashboardScreen(override val featureKey: NumbersDashboardKey) :
    BaseScreen<NumbersDashboardKey> {

    @Composable
    override fun Content() {
        val navigation = LocalBackStackController.current
        val sheetController = LocalSheetController.current
        val themeColor = KidsNumbers
        val vm: NumbersDashboardVm = hiltViewModel()
        val state by vm.state.collectAsState()

        var isTransitionFinished by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(300.milliseconds) // Delay to let the transition finish
            isTransitionFinished = true
        }

        LaunchedEffect(state.showRegistrationWarning) {
            if (state.showRegistrationWarning) {
                sheetController.show(
                    DialogBottomSheet(
                        data = DialogSheetData(
                            title = "Registration Required!",
                            canDismiss = false,
                            subtitle = "You haven't registered yet. If you continue without an account, your learning progress and test results may be lost!",
                            icon = Icons.Default.Warning,
                            iconColor = Color.Red,
                            dismissText = "Later",
                            confirmText = "Register Now",
                            onDismiss = {
                                vm.uiEvent(NumbersDashboardContract.Intent.DismissRegistrationWarning)
                                sheetController.clear()
                            },
                            onConfirm = {
                                vm.uiEvent(NumbersDashboardContract.Intent.NavigateToRegistration)
                                sheetController.clear()
                            }
                        )
                    )
                )
            }
        }

        LaunchedEffect(state.isSettingsDialogOpen) {
            if (state.isSettingsDialogOpen) {
                sheetController.show(
                    DialogBottomSheet(
                        data = DialogSheetData(
                            title = "Additional Settings",
                            isDialog = true,
                            canDismiss = false,
                            dismissText = "Close",
                            content = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    HorizontalDivider(
                                        color = Color.LightGray.copy(alpha = 0.3f),
                                        thickness = 1.dp
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    SettingsGrid(state, themeColor, vm)
                                }
                            },
                            onDismiss = {
                                vm.uiEvent(NumbersDashboardContract.Intent.ToggleSettingsDialog)
                                sheetController.clear()
                            }
                        )
                    )
                )
            }
        }

        LaunchedEffect(Unit) {
            vm.effect.collect { effect ->
                when (effect) {
                    is NumbersDashboardContract.Effect.NavigateToPractice -> {
                        navigation.push(
                            NumbersPracticeKey(
                                range = effect.range,
                                language = effect.language,
                                visualityType = effect.visualityType,
                                isRepeat = effect.isRepeat,
                                isAutoMode = effect.isAutoMode
                            )
                        )
                    }

                    is NumbersDashboardContract.Effect.NavigateToTest -> {
                        navigation.push(
                            NumbersTestKey(
                                range = effect.range,
                                language = effect.language,
                                visualityType = effect.visualityType,
                                isRepeat = effect.isRepeat
                            )
                        )
                    }

                    NumbersDashboardContract.Effect.NavigateToRegistration -> {
                        navigation.push(QuickRegistrationKey)
                    }
                }
            }
        }

        Scaffold(
            topBar = {
                AppHeader(
                    title = "Numbers Practice",
                    showBackButton = true,
                    onBackClick = { navigation.pop() },
                    rightContent = {
                        AdditionalSettingsToggle(themeColor) {
                            vm.uiEvent(NumbersDashboardContract.Intent.ToggleSettingsDialog)
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(4.dp))

                    ProgressCard(state.progress, themeColor)

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)

                    if (state.isLoading) {
                        DashboardShimmer()
                    } else {
                        HorizontalDivider(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )

                        RangeSelectionSection(state, vm)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                DashboardActionButtons(themeColor, vm)
            }
        }
    }
}

@Composable
private fun DashboardShimmer() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Settings Shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .shimmer()
        )

        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)

        // Range Shimmer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .shimmer()
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .shimmer()
            )
        }
    }
}

@Composable
private fun ProgressCard(progress: Float, themeColor: Color) {
    val animProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "progressAnimation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = themeColor.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Learning Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = themeColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { animProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = themeColor,
                strokeCap = StrokeCap.Round
            )
            Text(
                text = "${(animProgress * 100).toInt()}% completed",
                modifier = Modifier.align(Alignment.End),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun AdditionalSettingsToggle(themeColor: Color, onClick: () -> Unit) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.08f,
            animationSpec = repeatable(
                iterations = 4,
                animation = tween(500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        scale.animateTo(1f, tween(300))
    }

    Box(
        modifier = Modifier
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .scale(scale.value)
                .clickable { onClick() },
            shape = RoundedCornerShape(50),
            colors = CardDefaults.cardColors(containerColor = themeColor.copy(alpha = 0.1f)),
            border = BorderStroke(2.dp, themeColor.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "⚙️ Settings",
                    color = themeColor,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun SettingsGrid(
    state: NumbersDashboardContract.State,
    themeColor: Color,
    vm: NumbersDashboardVm,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SwitchOption(
                label = "Practice Mode",
                value = if (state.isAutoMode) "Auto" else "Manual",
                checked = state.isAutoMode,
                themeColor = themeColor,
                onCheckedChange = { vm.uiEvent(NumbersDashboardContract.Intent.ToggleAutoMode(it)) }
            )

            Spacer(modifier = Modifier.width(1.dp))

            SwitchOption(
                label = "Repeat",
                value = if (state.isRepeat) "On" else "Off",
                checked = state.isRepeat,
                themeColor = themeColor,
                onCheckedChange = { vm.uiEvent(NumbersDashboardContract.Intent.ToggleRepeat(it)) }
            )
        }

        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            VisualitySelector(state.visualityType, state.visualityTypes, themeColor) {
                vm.uiEvent(NumbersDashboardContract.Intent.ChangeVisuality(it))
            }

            Spacer(modifier = Modifier.width(1.dp))

            LanguageSelector(state.selectedLanguage, state.languages, themeColor) {
                vm.uiEvent(NumbersDashboardContract.Intent.ChangeLanguage(it))
            }
        }
    }
}

@Composable
private fun SwitchOption(
    label: String,
    value: String,
    checked: Boolean,
    themeColor: Color,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = themeColor),
            modifier = Modifier.scale(0.8f)
        )
    }
}

@Composable
private fun VisualitySelector(
    selectedType: VisualityType,
    types: List<VisualityType>,
    themeColor: Color,
    onSelect: (VisualityType) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "How it looks?",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            types.forEach { type ->
                VisualityOptionCard(
                    type = type,
                    isSelected = selectedType == type,
                    onClick = { onSelect(type) },
                    themeColor = themeColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun LanguageSelector(
    selectedCode: String,
    languages: List<Pair<String, String>>,
    themeColor: Color,
    onSelect: (String) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Language",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            languages.forEach { (code, label) ->
                FilterChip(
                    selected = selectedCode == code,
                    onClick = { onSelect(code) },
                    label = { Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.height(32.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = themeColor.copy(alpha = 0.2f),
                        selectedLabelColor = themeColor
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedCode == code,
                        selectedBorderColor = themeColor,
                        selectedBorderWidth = 1.5.dp
                    )
                )
            }
        }
    }
}

@Composable
private fun RangeSelectionSection(
    state: NumbersDashboardContract.State,
    vm: NumbersDashboardVm,
) {
    val ranges = remember { NumbersRange.entries }
    val themeColor = KidsNumbers

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Choose Range",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        val rows = ranges.chunked(2)
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { range ->
                    val isUnlocked = state.unlockedRanges.contains(range.id)
                    val isSelected = state.selectedRange == range
                    val label = when (range) {
                        NumbersRange.RANGE_0_10 -> "0-10"
                        NumbersRange.RANGE_11_20 -> "11-20"
                        NumbersRange.RANGE_21_30 -> "21-30"
                        NumbersRange.RANGE_TENS -> "40-90 (Tens)"
                        NumbersRange.RANGE_HUNDREDS -> "100-900 + 1M"
                        NumbersRange.RANGE_ALL_AVAILABLE -> "All"
                    }

                    val cardBgColor by animateColorAsState(
                        targetValue = when {
                            !isUnlocked -> Color.LightGray.copy(alpha = 0.2f)
                            isSelected -> themeColor
                            else -> Color.White
                        },
                        animationSpec = tween(durationMillis = 250),
                        label = "range_bg"
                    )
                    val cardTextColor by animateColorAsState(
                        targetValue = when {
                            !isUnlocked -> Color.Gray
                            isSelected -> Color.White
                            else -> Color.DarkGray
                        },
                        animationSpec = tween(durationMillis = 250),
                        label = "range_text"
                    )
                    val cardBorderColor by animateColorAsState(
                        targetValue = when {
                            !isUnlocked -> Color.LightGray.copy(alpha = 0.3f)
                            isSelected -> themeColor
                            else -> Color.LightGray.copy(alpha = 0.6f)
                        },
                        animationSpec = tween(durationMillis = 250),
                        label = "range_border"
                    )

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .clickable(enabled = isUnlocked) {
                                vm.uiEvent(NumbersDashboardContract.Intent.SelectRange(range))
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = cardBgColor
                        ),
                        border = BorderStroke(
                            width = 1.5.dp,
                            color = cardBorderColor
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (isSelected && isUnlocked) 6.dp else 0.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (!isUnlocked) {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.Gray
                                    )
                                }
                                Text(
                                    text = label,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = cardTextColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardActionButtons(themeColor: Color, vm: NumbersDashboardVm) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = { vm.uiEvent(NumbersDashboardContract.Intent.StartPractice) },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = themeColor),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp,
                pressedElevation = 2.dp
            )
        ) {
            Text(
                text = "🚀 LEARN",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }

        Button(
            onClick = { vm.uiEvent(NumbersDashboardContract.Intent.StartTest) },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = themeColor
            ),
            border = BorderStroke(2.5.dp, themeColor),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 1.dp
            )
        ) {
            Text(
                text = "🎯 START TEST",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun VisualityOptionCard(
    type: VisualityType,
    isSelected: Boolean,
    onClick: () -> Unit,
    themeColor: Color,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) themeColor else Color.White,
        animationSpec = tween(durationMillis = 250),
        label = "vis_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else themeColor,
        animationSpec = tween(durationMillis = 250),
        label = "vis_content"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) themeColor else Color.LightGray.copy(alpha = 0.5f),
        animationSpec = tween(durationMillis = 250),
        label = "vis_border"
    )

    Card(
        modifier = modifier
            .height(60.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(
            width = 1.5.dp,
            color = borderColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (type) {
                VisualityType.Symbols -> {
                    Text("1", fontSize = 22.sp, fontWeight = FontWeight.Black, color = contentColor)
                }

                VisualityType.Text -> {
                    Text("One", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = contentColor)
                }

                VisualityType.Both -> {
                    Text("1", fontSize = 20.sp, fontWeight = FontWeight.Black, color = contentColor)
                    Text(
                        "One",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }
            }
        }
    }
}
