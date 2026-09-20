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
import androidx.compose.runtime.*
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
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditProfileSheet(
    private val initialState: ProfileHomeDetailContract.State,
    private val onSave: (ProfileHomeDetailContract.State) -> Unit,
    private val onDeleteRequest: () -> Unit,
    private val onDismiss: () -> Unit,
) : SheetContent {
    override val initialFullExpand = true

    override fun onDismissed() {
        onDismiss()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        var name by remember { mutableStateOf(initialState.name) }
        var username by remember { mutableStateOf(initialState.username) }
        var dob by remember { mutableStateOf(initialState.dob) }
        var country by remember { mutableStateOf(initialState.country) }
        
        var showDatePicker by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Edit Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                )

                IconButton(onClick = onDeleteRequest) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Account",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                item {
                    ProfileTextField(
                        value = name,
                        label = "Kid's Name",
                        icon = Icons.Default.Person,
                        onValueChange = { name = it }
                    )
                }
                item {
                    ProfileTextField(
                        value = username,
                        label = "Username",
                        icon = Icons.Default.AlternateEmail,
                        onValueChange = { username = it }
                    )
                }
                item {
                    ProfileTextField(
                        value = dob,
                        label = "Date of Birth",
                        icon = Icons.Default.DateRange,
                        readOnly = true,
                        trailingIcon = {
                            TextButton(onClick = { showDatePicker = true }) {
                                Text("Pick", color = KidsNumbers, fontWeight = FontWeight.Bold)
                            }
                        },
                        onValueChange = { dob = it }
                    )
                }
                item {
                    ProfileTextField(
                        value = country,
                        label = "Country",
                        icon = Icons.Default.Public,
                        onValueChange = { country = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onSave(
                        initialState.copy(
                            name = name,
                            username = username,
                            dob = dob,
                            country = country
                        )
                    )
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

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color.LightGray)
            ) {
                Text("Cancel", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let {
                                val dobStr = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                                    .format(Date(it))
                                dob = dobStr
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK", color = KidsNumbers, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }

    @Composable
    private fun ProfileTextField(
        value: String,
        label: String,
        icon: ImageVector,
        readOnly: Boolean = false,
        trailingIcon: @Composable (() -> Unit)? = null,
        onValueChange: (String) -> Unit,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = KidsNumbers) },
            trailingIcon = trailingIcon,
            readOnly = readOnly,
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
    private val categories: List<ProfileHomeDetailContract.CategoryProgress>,
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
