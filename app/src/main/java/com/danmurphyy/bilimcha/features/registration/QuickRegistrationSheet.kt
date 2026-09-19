package com.danmurphyy.bilimcha.features.registration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.danmurphyy.bilimcha.ui.theme.KidsNumbers
import com.danmurphyy.bilimcha.uibases.SheetContent
import java.text.SimpleDateFormat
import java.util.*

class QuickRegistrationSheet(
    private val title: String = "Welcome! 🌟",
    private val subtitle: String = "Let's get to know you better!",
    private val onDismiss: () -> Unit,
) : SheetContent {

    override val initialFullExpand = true
    override val isDialog = false
    override val canDismiss = true

    override fun onDismissed() {
        onDismiss()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val vm: QuickRegistrationVm = hiltViewModel()
        val state by vm.state.collectAsState()
        val themeColor = KidsNumbers

        val isFormValid = state.name.isNotBlank() && state.birthDate != null

        var showDatePicker by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            vm.effect.collect { effect ->
                when (effect) {
                    is QuickRegistrationContract.Effect.NavigateToHome -> {
                        onDismiss()
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .padding(24.dp)
                .navigationBarsPadding()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = themeColor
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { vm.uiEvent(QuickRegistrationContract.Intent.ChangeName(it)) },
                        label = { Text("What's your name?") },
                        placeholder = { Text("Enter your name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Face,
                                contentDescription = null,
                                tint = themeColor
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColor,
                            focusedLabelColor = themeColor
                        ),
                        singleLine = true
                    )

                    val dateText = state.birthDate?.let {
                        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(it))
                    } ?: "Select your birthday"

                    OutlinedTextField(
                        value = dateText,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("When is your birthday?") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Cake,
                                contentDescription = null,
                                tint = themeColor
                            )
                        },
                        trailingIcon = {
                            TextButton(onClick = { showDatePicker = true }) {
                                Text("Pick", color = themeColor, fontWeight = FontWeight.Bold)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColor,
                            focusedLabelColor = themeColor
                        )
                    )

                    OutlinedTextField(
                        value = state.country,
                        onValueChange = { vm.uiEvent(QuickRegistrationContract.Intent.ChangeCountry(it)) },
                        label = { Text("Where are you from?") },
                        placeholder = { Text("Enter your country") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Public,
                                contentDescription = null,
                                tint = themeColor
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColor,
                            focusedLabelColor = themeColor
                        ),
                        singleLine = true
                    )
                }


                Spacer(modifier = Modifier.height(32.dp))

                AnimatedVisibility(visible = isFormValid) {
                    Button(
                        onClick = { vm.uiEvent(QuickRegistrationContract.Intent.Save) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                        enabled = !state.isSaving
                    ) {
                        Text(
                            text = if (state.isSaving) "SAVING..." else "LET'S START! 🚀",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

            }

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    vm.uiEvent(QuickRegistrationContract.Intent.ChangeBirthDate(it))
                                }
                                showDatePicker = false
                            }
                        ) {
                            Text("OK", color = themeColor, fontWeight = FontWeight.Bold)
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
    }
}
