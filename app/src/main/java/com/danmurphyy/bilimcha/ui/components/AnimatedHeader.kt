package com.danmurphyy.bilimcha.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FloatingHeader(
    greeting: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")

    // We store the State objects instead of extracting the Float values immediately
    val floatAnim1 = infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float1"
    )

    // Animation for the second emoji (different speed/offset)
    val floatAnim2 = infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🌟",
                fontSize = 32.sp,
                // Accessing .value inside the lambda prevents recomposition
                modifier = Modifier.graphicsLayer { translationY = floatAnim1.value }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 28.sp
                )
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "🎈",
                fontSize = 32.sp,
                // Accessing .value inside the lambda prevents recomposition
                modifier = Modifier.graphicsLayer { translationY = floatAnim2.value }
            )
        }
        
        Text(
            text = "Let's learn and play!",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
