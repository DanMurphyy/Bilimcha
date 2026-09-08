package com.danmurphyy.bilimcha.numbers.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.danmurphyy.bilimcha.navigations.LocalBackStackController
import com.danmurphyy.bilimcha.navigations.NumbersDashboardKey
import com.danmurphyy.bilimcha.navigations.NumbersPracticeKey
import com.danmurphyy.bilimcha.navigations.NumbersTestKey
import com.danmurphyy.bilimcha.navigations.VisualityType
import com.danmurphyy.bilimcha.ui.theme.KidsNumbers
import com.danmurphyy.bilimcha.uibases.AppHeader
import com.danmurphyy.bilimcha.uibases.BaseScreen
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Immutable
class NumbersDashboardScreen(override val featureKey: NumbersDashboardKey) :
    BaseScreen<NumbersDashboardKey> {

    @Composable
    override fun Content() {
        val navigation = LocalBackStackController.current
        val themeColor = KidsNumbers
        val vm: NumbersDashboardVm = hiltViewModel()
        val state by vm.state.collectAsState()

        var isTransitionFinished by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(300.milliseconds) // Delay to let the transition finish
            isTransitionFinished = true
        }

        LaunchedEffect(Unit) {
            vm.effect.collect { effect ->
                when (effect) {
                    is NumbersDashboardContracts.Effect.NavigateToPractice -> {
                        navigation.push(
                            NumbersPracticeKey(
                                fromValue = effect.from,
                                toValue = effect.to,
                                language = effect.language,
                                visualityType = effect.visualityType,
                                isRepeat = effect.isRepeat
                            )
                        )
                    }

                    is NumbersDashboardContracts.Effect.NavigateToTest -> {
                        navigation.push(
                            NumbersTestKey(
                                fromValue = effect.from,
                                toValue = effect.to,
                                language = effect.language,
                                visualityType = effect.visualityType,
                                isRepeat = effect.isRepeat
                            )
                        )
                    }
                }
            }
        }

        Scaffold(
            topBar = {
                AppHeader(
                    title = "Numbers Practice",
                    showBackButton = true,
                    onBackClick = { navigation.pop() }
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

                    AdditionalSettingsToggle(state.isAdditionalVisible, themeColor) {
                        vm.uiEvent(NumbersDashboardContracts.Intent.ToggleAdditionalVisibility)
                    }

                    if (isTransitionFinished) {
                        AnimatedVisibility(
                            visible = state.isAdditionalVisible,
                            enter = expandVertically(animationSpec = tween(500)) + fadeIn(tween(500)),
                            exit = shrinkVertically(animationSpec = tween(500)) + fadeOut(tween(500))
                        ) {
                            SettingsGrid(state, themeColor, vm)
                        }

                        HorizontalDivider(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )

                        RangeSelectionSection(state, vm)
                    } else {
                        // Placeholder to keep layout stable during transition
                        Spacer(modifier = Modifier.height(200.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                DashboardActionButtons(themeColor, vm)
            }
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
private fun AdditionalSettingsToggle(isVisible: Boolean, themeColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isVisible) "↑ Additional" else "↓ Additional",
            color = themeColor.copy(alpha = 0.7f),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun SettingsGrid(
    state: NumbersDashboardContracts.State,
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
                onCheckedChange = { vm.uiEvent(NumbersDashboardContracts.Intent.ToggleAutoMode(it)) }
            )

            Spacer(modifier = Modifier.width(1.dp))

            SwitchOption(
                label = "Repeat",
                value = if (state.isRepeat) "On" else "Off",
                checked = state.isRepeat,
                themeColor = themeColor,
                onCheckedChange = { vm.uiEvent(NumbersDashboardContracts.Intent.ToggleRepeat(it)) }
            )
        }

        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            VisualitySelector(state.visualityType, state.visualityTypes, themeColor) {
                vm.uiEvent(NumbersDashboardContracts.Intent.ChangeVisuality(it))
            }

            Spacer(modifier = Modifier.width(1.dp))

            LanguageSelector(state.selectedLanguage, state.languages, themeColor) {
                vm.uiEvent(NumbersDashboardContracts.Intent.ChangeLanguage(it))
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
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            types.forEach { type ->
                VisualityOptionCard(
                    type = type,
                    isSelected = selectedType == type,
                    onClick = { onSelect(type) },
                    themeColor = themeColor,
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
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            languages.forEach { (code, label) ->
                FilterChip(
                    selected = selectedCode == code,
                    onClick = { onSelect(code) },
                    label = { Text(text = label, fontSize = 12.sp) },
                    modifier = Modifier.height(32.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = themeColor.copy(alpha = 0.2f),
                        selectedLabelColor = themeColor
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedCode == code,
                        selectedBorderColor = themeColor,
                        selectedBorderWidth = 1.dp
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RangeSelectionSection(
    state: NumbersDashboardContracts.State,
    vm: NumbersDashboardVm,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Choose Range",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RangeDropdown(
                label = "From",
                value = state.selectedFrom.toString(),
                expanded = state.expandedFrom,
                options = state.fromOptions,
                onToggle = { vm.uiEvent(NumbersDashboardContracts.Intent.ToggleExpandedFrom) },
                onSelect = {
                    vm.uiEvent(NumbersDashboardContracts.Intent.SelectFrom(it))
                    vm.uiEvent(NumbersDashboardContracts.Intent.ToggleExpandedFrom)
                },
                modifier = Modifier.weight(1f)
            )

            RangeDropdown(
                label = "To",
                value = state.selectedTo.toString(),
                expanded = state.expandedTo,
                options = state.toOptions,
                onToggle = { vm.uiEvent(NumbersDashboardContracts.Intent.ToggleExpandedTo) },
                onSelect = {
                    vm.uiEvent(NumbersDashboardContracts.Intent.SelectTo(it))
                    vm.uiEvent(NumbersDashboardContracts.Intent.ToggleExpandedTo)
                },
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "* Minimum range is 5 numbers",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RangeDropdown(
    label: String,
    value: String,
    expanded: Boolean,
    options: List<Int>,
    onToggle: () -> Unit,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onToggle() },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onToggle
        ) {
            options.forEach { number ->
                DropdownMenuItem(
                    text = { Text(number.toString()) },
                    onClick = { onSelect(number) },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
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
            onClick = { vm.uiEvent(NumbersDashboardContracts.Intent.StartPractice) },
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = themeColor),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 2.dp
            )
        ) {
            Text(
                text = "🚀 LEARN",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }

        Button(
            onClick = { vm.uiEvent(NumbersDashboardContracts.Intent.StartTest) },
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(
                topStart = 8.dp,
                bottomEnd = 8.dp,
                topEnd = 32.dp,
                bottomStart = 32.dp
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = themeColor
            ),
            border = BorderStroke(3.dp, themeColor),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp,
                pressedElevation = 2.dp
            )
        ) {
            Text(
                text = "🎯 START TEST",
                fontSize = 22.sp,
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
) {
    Card(
        modifier = Modifier
            .wrapContentSize()
            .height(50.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) themeColor.copy(alpha = 0.15f) else Color.White
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) themeColor else Color.LightGray.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .height(50.dp)
                .width(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (type) {
                VisualityType.Symbols -> {
                    Text("1", fontSize = 20.sp, fontWeight = FontWeight.Black, color = themeColor)
                }

                VisualityType.Text -> {
                    Text("One", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = themeColor)
                }

                VisualityType.Both -> {
                    Text("1", fontSize = 20.sp, fontWeight = FontWeight.Black, color = themeColor)
                    Text(
                        "One",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = themeColor
                    )
                }
            }
        }
    }
}
