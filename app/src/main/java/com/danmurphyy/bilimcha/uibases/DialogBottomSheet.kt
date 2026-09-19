package com.danmurphyy.bilimcha.uibases

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class DialogSheetData(
    val isDialog: Boolean = true,
    val canDismiss: Boolean = true,
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector? = null,
    val iconColor: Color = Color(0xFF2E7D32),
    val description: String? = null,
    val dismissText: String = "Dismiss",
    val confirmText: String = "Yes",
    val content: (@Composable () -> Unit)? = null,
    val onDismiss: () -> Unit,
    val onConfirm: (() -> Unit)? = null,
)

class DialogBottomSheet(
    private val data: DialogSheetData,
) : SheetContent {

    override val isDialog = data.isDialog
    override val canDismiss = data.canDismiss
    override val initialFullExpand = false

    @Composable
    override fun Content() {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                data.subtitle?.let {
                    Row(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )

                        if (data.icon != null) {
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                imageVector = data.icon,
                                contentDescription = null,
                                tint = data.iconColor
                            )
                        }
                    }
                }

                data.content?.let {
                    it()
                    Spacer(modifier = Modifier.padding(bottom = 20.dp))
                }

                data.description?.let {
                    Row(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(Modifier.width(4.dp))

                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFF9A825)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { data.onDismiss() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(data.dismissText)
                    }

                    data.onConfirm?.let { onConfirm ->
                        Button(
                            onClick = { onConfirm() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(data.confirmText)
                        }
                    }
                }
            }
        }
    }
}
