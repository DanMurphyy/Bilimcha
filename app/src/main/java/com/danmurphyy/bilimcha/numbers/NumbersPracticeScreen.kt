package com.danmurphyy.bilimcha.numbers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danmurphyy.bilimcha.navigations.NumbersPracticeKey
import com.danmurphyy.bilimcha.navigations.VisualityType
import com.danmurphyy.bilimcha.ui.theme.KidsNumbers
import com.danmurphyy.bilimcha.uibases.AppHeader
import com.danmurphyy.bilimcha.uibases.BaseScreen
import com.danmurphyy.bilimcha.uibases.SoundPlayer

class NumbersPracticeScreen(override val featureKey: NumbersPracticeKey) : BaseScreen<NumbersPracticeKey> {
    @Composable
    override fun Content() {
        val context = LocalContext.current
        var selectedLanguage by remember { mutableStateOf(featureKey.language) }

        Scaffold(
            topBar = {
                AppHeader(
                    title = "Learn Numbers",
                    showBackButton = true
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("en" to "English", "ru" to "Russian").forEach { (code, label) ->
                        FilterChip(
                            selected = selectedLanguage == code,
                            onClick = { selectedLanguage = code },
                            label = { Text(label) }
                        )
                    }
                }

                val filteredNumbers = NumbersRepository.numbers.filter { 
                    it.value in featureKey.fromValue..featureKey.toValue 
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredNumbers) { number ->
                        NumberCard(number, selectedLanguage, featureKey.visualityType) {
                            val audioUrl = when (selectedLanguage) {
                                "en" -> number.audioEn
                                "ru" -> number.audioRu
                                else -> number.audioEn
                            }
                            SoundPlayer.playSound(context, audioUrl)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberCard(number: Number, language: String, visualityType: VisualityType, onClick: () -> Unit) {
    val name = when (language) {
        "en" -> number.nameEn
        "ru" -> number.nameRu
        else -> number.nameEn
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = KidsNumbers.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (visualityType == VisualityType.Symbols || visualityType == VisualityType.Both) {
                Text(
                    text = number.value.toString(),
                    fontSize = if (visualityType == VisualityType.Both) 40.sp else 60.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsNumbers
                )
            }
            if (visualityType == VisualityType.Text || visualityType == VisualityType.Both) {
                Text(
                    text = name,
                    fontSize = if (visualityType == VisualityType.Both) 18.sp else 28.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
