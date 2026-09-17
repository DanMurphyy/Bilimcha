package com.danmurphyy.bilimcha.features.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danmurphyy.bilimcha.ui.theme.KidsABC
import com.danmurphyy.bilimcha.ui.theme.KidsAnimals
import com.danmurphyy.bilimcha.ui.theme.KidsNumbers
import com.danmurphyy.bilimcha.uibases.SheetContent

class EditProfileSheet(
    private val state: ProfileHomeDetailContract.State,
    private val onIntent: (ProfileHomeDetailContract.Intent) -> Unit,
    private val onDeleteRequest: () -> Unit,
    private val onDismiss: () -> Unit
) : SheetContent {
    override val initialFullExpand = true

    @Composable
    override fun Content() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Edit Profile",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                item {
                    ProfileTextField(
                        value = state.name,
                        label = "Kid's Name",
                        icon = Icons.Default.Person,
                        onValueChange = { onIntent(ProfileHomeDetailContract.Intent.UpdateName(it)) }
                    )
                }
                item {
                    ProfileTextField(
                        value = state.username,
                        label = "Username",
                        icon = Icons.Default.AlternateEmail,
                        onValueChange = { onIntent(ProfileHomeDetailContract.Intent.UpdateUsername(it)) }
                    )
                }
                item {
                    ProfileTextField(
                        value = state.dob,
                        label = "Date of Birth",
                        icon = Icons.Default.DateRange,
                        onValueChange = { onIntent(ProfileHomeDetailContract.Intent.UpdateDob(it)) }
                    )
                }
                item {
                    ProfileTextField(
                        value = state.country,
                        label = "Country",
                        icon = Icons.Default.Public,
                        onValueChange = { onIntent(ProfileHomeDetailContract.Intent.UpdateCountry(it)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onIntent(ProfileHomeDetailContract.Intent.SaveProfile)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KidsNumbers)
            ) {
                Text("Save Changes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onDeleteRequest,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.DeleteOutline, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Account", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    @Composable
    private fun ProfileTextField(
        value: String,
        label: String,
        icon: ImageVector,
        onValueChange: (String) -> Unit
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = KidsNumbers) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = KidsNumbers,
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = KidsNumbers
            ),
            singleLine = true
        )
    }
}

class StatisticsSheet(
    private val categories: List<ProfileHomeDetailContract.CategoryProgress>
) : SheetContent {
    override val initialFullExpand = true

    @Composable
    override fun Content() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Learning Statistics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    CategoryProgressItem(category)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    @Composable
    private fun CategoryProgressItem(category: ProfileHomeDetailContract.CategoryProgress) {
        val color = when {
            category.name.contains("Numbers") -> KidsNumbers
            category.name.contains("ABC") -> KidsABC
            category.name.contains("Animals") -> KidsAnimals
            else -> KidsNumbers
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = category.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            text = "${category.correct} / ${category.total} learned",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Text(
                        text = "${(category.progress * 100).toInt()}%",
                        fontWeight = FontWeight.Black,
                        color = color,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { category.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = color,
                    trackColor = color.copy(alpha = 0.1f)
                )
            }
        }
    }
}
