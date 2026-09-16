package com.danmurphyy.bilimcha.features.numbers.practice

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.danmurphyy.bilimcha.navigations.LocalBackStackController
import com.danmurphyy.bilimcha.navigations.NumbersPracticeKey
import com.danmurphyy.bilimcha.navigations.VisualityType
import com.danmurphyy.bilimcha.features.numbers.Number
import com.danmurphyy.bilimcha.ui.theme.KidsNumbers
import com.danmurphyy.bilimcha.uibases.AppHeader
import com.danmurphyy.bilimcha.uibases.BaseScreen
import com.danmurphyy.bilimcha.uibases.SoundPlayer
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class NumbersPracticeScreen(override val featureKey: NumbersPracticeKey) :
    BaseScreen<NumbersPracticeKey> {
    @Composable
    override fun Content() {
        val navigation = LocalBackStackController.current
        val context = LocalContext.current
        val vm: NumbersPracticeVm = hiltViewModel()
        val state by vm.state.collectAsState()

        LaunchedEffect(featureKey) {
            vm.uiEvent(NumbersPracticeContract.Intent.Init(featureKey))
        }

        LaunchedEffect(Unit) {
            vm.effect.collect { effect ->
                when (effect) {
                    is NumbersPracticeContract.Effect.PlayAudio -> {
                        SoundPlayer.playSound(context, effect.url)
                    }

                    NumbersPracticeContract.Effect.NavigateBack -> {
                        navigation.pop()
                    }
                }
            }
        }

        // Auto-play audio when number changes
        LaunchedEffect(state.currentIndex, state.isLoading, state.isFinished) {
            if (state.autoPlayAudio && !state.isLoading && !state.isFinished) {
                delay(200.milliseconds) // Small delay for visual transition
                vm.uiEvent(NumbersPracticeContract.Intent.PlayAudio)
            }
        }

        // Auto-advance logic
        LaunchedEffect(state.currentIndex, state.isLoading, state.isFinished) {
            if (state.autoAdvance && !state.isLoading && !state.isFinished) {
                delay(3000.milliseconds) // Wait for 4 seconds (audio + observation time)
                vm.uiEvent(NumbersPracticeContract.Intent.NextNumber)
            }
        }

        Scaffold(
            topBar = {
                AppHeader(
                    title = "Numbers Practice",
                    showBackButton = true,
                    onBackClick = { navigation.pop() }
                )
            },
            bottomBar = {
                if (!state.isFinished && !state.isLoading) {
                    if (!state.autoAdvance) {
                        PracticeBottomBar(
                            onPrevious = { vm.uiEvent(NumbersPracticeContract.Intent.PreviousNumber) },
                            onNext = { vm.uiEvent(NumbersPracticeContract.Intent.NextNumber) }
                        )
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = KidsNumbers)
                } else if (state.isFinished) {
                    FinishState(
                        onRepeat = { vm.uiEvent(NumbersPracticeContract.Intent.RepeatFromStart) },
                        onFinish = { vm.uiEvent(NumbersPracticeContract.Intent.Finish) }
                    )
                } else {
                    state.currentNumber?.let { number ->
                        AnimatedContent(
                            targetState = number,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(500)) togetherWith
                                        fadeOut(animationSpec = tween(500))
                            },
                            label = "NumberTransition"
                        ) { targetNumber ->
                            BigNumberCard(
                                number = targetNumber,
                                language = state.key?.language ?: "en",
                                visualityType = state.key?.visualityType ?: VisualityType.Both,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BigNumberCard(
    number: Number,
    language: String,
    visualityType: VisualityType,
) {
    val name = if (language == "ru") number.nameRu else number.nameEn

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f),
            shape = RoundedCornerShape(40.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(4.dp, KidsNumbers.copy(alpha = 0.3f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                when (visualityType) {
                    VisualityType.Text -> {
                        Text(
                            text = name,
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            color = KidsNumbers
                        )
                    }

                    VisualityType.Symbols -> {
                        Text(
                            text = number.value.toString(),
                            fontSize = 120.sp,
                            fontWeight = FontWeight.Black,
                            color = KidsNumbers
                        )
                    }

                    VisualityType.Both -> {
                        Text(
                            text = number.value.toString(),
                            fontSize = 120.sp,
                            fontWeight = FontWeight.Black,
                            color = KidsNumbers
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = name,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun PracticeBottomBar(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                onClick = onPrevious,
                colors = ButtonDefaults.buttonColors(containerColor = KidsNumbers),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(56.dp)
            ) {
                Text("Previous", fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }

            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(containerColor = KidsNumbers),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(56.dp)
            ) {
                Text("NEXT", fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
private fun FinishState(onRepeat: () -> Unit, onFinish: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🌟 WELL DONE! 🌟",
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            color = KidsNumbers
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "You learned all the numbers!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onRepeat,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = KidsNumbers)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("REPEAT AGAIN", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(2.dp, KidsNumbers)
        ) {
            Text("FINISH", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = KidsNumbers)
        }
    }
}
