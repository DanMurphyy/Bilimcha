package com.danmurphyy.bilimcha.features.numbers.testing

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.danmurphyy.bilimcha.features.numbers.Number
import com.danmurphyy.bilimcha.navigations.LocalBackStackController
import com.danmurphyy.bilimcha.navigations.NumbersTestKey
import com.danmurphyy.bilimcha.navigations.VisualityType
import com.danmurphyy.bilimcha.ui.theme.KidsNumbers
import com.danmurphyy.bilimcha.uibases.AppHeader
import com.danmurphyy.bilimcha.uibases.BaseScreen
import com.danmurphyy.bilimcha.uibases.SoundPlayer
import kotlin.random.Random

class NumbersTestScreen(override val featureKey: NumbersTestKey) : BaseScreen<NumbersTestKey> {
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigation = LocalBackStackController.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val vm: NumbersTestVm = hiltViewModel()
        val state by vm.state.collectAsState()

        LaunchedEffect(Unit) {
            vm.uiEvent(NumbersTestContract.Intent.Init(featureKey))
        }

        LaunchedEffect(Unit) {
            vm.effect.collect { effect ->
                when (effect) {
                    is NumbersTestContract.Effect.PlaySound -> SoundPlayer.playSound(context, effect.path)
                    NumbersTestContract.Effect.StopSound -> SoundPlayer.stopSound()
                    NumbersTestContract.Effect.Back -> navigation.pop()
                }
            }
        }

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> vm.uiEvent(NumbersTestContract.Intent.OnPause)
                    Lifecycle.Event.ON_RESUME -> vm.uiEvent(NumbersTestContract.Intent.OnResume)
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        if (state.showExitDialog) {
            AlertDialog(
                onDismissRequest = { vm.uiEvent(NumbersTestContract.Intent.DismissExitDialog) },
                title = { Text("Leave Test?") },
                text = { Text("Are you sure you want to leave? You may lose your current progress.") },
                confirmButton = {
                    TextButton(onClick = { vm.uiEvent(NumbersTestContract.Intent.ConfirmBack) }) {
                        Text("Leave", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { vm.uiEvent(NumbersTestContract.Intent.DismissExitDialog) }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Scaffold(
            topBar = {
                AppHeader(
                    title = "Numbers Test",
                    showBackButton = true,
                    onBackClick = { vm.uiEvent(NumbersTestContract.Intent.RequestBack) },
                    rightContent = {
                        FilterChip(
                            selected = state.isRepeatMode,
                            onClick = { vm.uiEvent(NumbersTestContract.Intent.ToggleRepeat) },
                            label = { Text(if (state.isRepeatMode) "Repeat: ON" else "Repeat: OFF", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = KidsNumbers.copy(alpha = 0.2f),
                                selectedLabelColor = KidsNumbers
                            )
                        )
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (state.isFinished) {
                    ResultCard(
                        correct = state.correctCount,
                        wrong = state.wrongCount,
                        onRepeat = { vm.uiEvent(NumbersTestContract.Intent.RepeatTest) },
                        onFinish = { navigation.pop() }
                    )
                } else {
                    TestContent(state, vm)
                    
                    if (state.isIntroActive) {
                        IntroOverlay(state.countdownValue)
                    }
                }
            }
        }
    }
}

@Composable
private fun IntroOverlay(countdown: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "bubbleTransition")
    
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubbleFloat"
    )

    val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta, KidsNumbers)
    
    val bubbleData = remember {
        List(15) {
            Triple(
                Random.nextFloat(), // x
                Random.nextFloat(), // y
                Random.nextInt(20, 60).dp // radius
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.95f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            bubbleData.forEachIndexed { index, (x, y, radius) ->
                drawCircle(
                    color = colors[index % colors.size].copy(alpha = 0.15f),
                    radius = radius.toPx(),
                    center = Offset(
                        x * size.width,
                        y * size.height + (if (index % 2 == 0) floatOffset else -floatOffset)
                    )
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Get Ready!",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = KidsNumbers
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            AnimatedContent(
                targetState = countdown,
                transitionSpec = {
                    (scaleIn(animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)) + fadeIn()) togetherWith
                            (scaleOut(animationSpec = tween(200)) + fadeOut())
                },
                label = "countdownAnimation"
            ) { targetCountdown ->
                Text(
                    text = targetCountdown.toString(),
                    fontSize = 120.sp,
                    fontWeight = FontWeight.Black,
                    color = KidsNumbers,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun TestContent(
    state: NumbersTestContract.State,
    vm: NumbersTestVm
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Progress text
        Text(
            text = "Number ${state.currentIndex + 1} of ${state.totalCount}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.weight(1f))

        // The 3 cards
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            state.options.forEach { number ->
                NumberOptionCard(
                    number = number,
                    visualityType = state.visualityType,
                    language = state.language,
                    isSelected = state.selectedOption == number,
                    isCorrect = state.isCorrectSelected,
                    isEnabled = state.isSelectionEnabled && !state.isIntroActive,
                    onClick = { vm.uiEvent(NumbersTestContract.Intent.SelectOption(number)) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun NumberOptionCard(
    number: Number,
    visualityType: VisualityType,
    language: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected && isCorrect == true -> Color.Green.copy(alpha = 0.2f)
        isSelected && isCorrect == false -> Color.Red.copy(alpha = 0.2f)
        else -> KidsNumbers.copy(alpha = 0.1f)
    }

    val borderColor = when {
        isSelected && isCorrect == true -> Color.Green
        isSelected && isCorrect == false -> Color.Red
        else -> Color.Transparent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(enabled = isEnabled) { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if (isSelected) BorderStroke(2.dp, borderColor) else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val name = if (language == "en") number.nameEn else number.nameRu
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (visualityType == VisualityType.Symbols || visualityType == VisualityType.Both) {
                    Text(
                        text = number.value.toString(),
                        fontSize = if (visualityType == VisualityType.Both) 32.sp else 48.sp,
                        fontWeight = FontWeight.Black,
                        color = KidsNumbers
                    )
                }
                if (visualityType == VisualityType.Text || visualityType == VisualityType.Both) {
                    Text(
                        text = name,
                        fontSize = if (visualityType == VisualityType.Both) 16.sp else 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultCard(
    correct: Int,
    wrong: Int,
    onRepeat: () -> Unit,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = KidsNumbers.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Test Result",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = KidsNumbers
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ResultStat("Correct", correct, Color.Green)
                    ResultStat("Wrong", wrong, Color.Red)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onRepeat,
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KidsNumbers)
                ) {
                    Text("Repeat", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(2.dp, KidsNumbers)
                ) {
                    Text("Finish", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = KidsNumbers)
                }
            }
        }
    }
}

@Composable
private fun ResultStat(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontWeight = FontWeight.Medium, color = Color.Gray)
        Text(text = value.toString(), fontSize = 40.sp, fontWeight = FontWeight.Black, color = color)
    }
}
