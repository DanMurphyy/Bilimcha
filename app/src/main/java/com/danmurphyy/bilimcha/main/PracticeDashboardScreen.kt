package com.danmurphyy.bilimcha.main

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danmurphyy.bilimcha.navigations.LocalBackStackController
import com.danmurphyy.bilimcha.navigations.PracticeDashboardKey
import com.danmurphyy.bilimcha.navigations.PracticeType
import com.danmurphyy.bilimcha.ui.theme.KidsABC
import com.danmurphyy.bilimcha.ui.theme.KidsAnimals
import com.danmurphyy.bilimcha.ui.theme.KidsNumbers
import com.danmurphyy.bilimcha.uibases.AppHeader
import com.danmurphyy.bilimcha.uibases.BaseScreen


class PracticeDashboardScreen(override val featureKey: PracticeDashboardKey) :
    BaseScreen<PracticeDashboardKey> {
    @Composable
    override fun Content() {
        val navigation = LocalBackStackController.current
        val context = LocalContext.current

        val practiceTypeTile = when (featureKey.practiceType) {
            PracticeType.Alphabet -> "Alphabet"
            PracticeType.Animals -> "Animals"
            else -> "Practice"
        }

        val themeColor = when (featureKey.practiceType) {
            PracticeType.Alphabet -> KidsABC
            PracticeType.Animals -> KidsAnimals
            else -> KidsNumbers
        }

        // State for dashboard
        var isAutoMode by remember { mutableStateOf(true) }
        var rangeFrom by remember { mutableStateOf("1") }
        var rangeTo by remember { mutableStateOf("10") }
        var progress by remember { mutableFloatStateOf(0f) } // Initial progress 0
        val animProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(durationMillis = 1000),
            label = "progressAnimation"
        )

        LaunchedEffect(Unit) {
            progress = 0.3f // Target mock progress
        }

        Scaffold(
            topBar = {
                AppHeader(
                    title = practiceTypeTile,
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
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Progress Section
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

                // Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isAutoMode) "Practice Mode: Auto" else "Practice Mode: Manual",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = isAutoMode,
                        onCheckedChange = { isAutoMode = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = themeColor)
                    )
                }

                // Range Selection (Show only for Numbers/Alphabet)
                if (featureKey.practiceType != PracticeType.Animals) {
                    Text(
                        text = "Choose Range",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = rangeFrom,
                            onValueChange = { rangeFrom = it },
                            label = { Text("From") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = rangeTo,
                            onValueChange = { rangeTo = it },
                            label = { Text("To") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            when (featureKey.practiceType) {
                                PracticeType.Alphabet -> navigation.push(com.danmurphyy.bilimcha.navigations.AbcKey)
                                PracticeType.Animals -> navigation.push(com.danmurphyy.bilimcha.navigations.AnimalsKey)
                                else -> {}
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = themeColor
                        ),
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
                        onClick = {
                            if (featureKey.practiceType == PracticeType.Animals) {
                                Toast.makeText(
                                    context,
                                    "Animals Test Coming Soon!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Logic for Test
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp),
                        shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp, topEnd = 32.dp, bottomStart = 32.dp),
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

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}