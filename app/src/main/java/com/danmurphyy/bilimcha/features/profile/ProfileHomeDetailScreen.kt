package com.danmurphyy.bilimcha.features.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.danmurphyy.bilimcha.main.showToast
import com.danmurphyy.bilimcha.navigations.LocalBackStackController
import com.danmurphyy.bilimcha.navigations.LocalSheetController
import com.danmurphyy.bilimcha.navigations.MainHomeKey
import com.danmurphyy.bilimcha.navigations.ProfileHomeDetailKey
import com.danmurphyy.bilimcha.ui.theme.KidsABC
import com.danmurphyy.bilimcha.ui.theme.KidsAnimals
import com.danmurphyy.bilimcha.ui.theme.KidsNumbers
import com.danmurphyy.bilimcha.uibases.*

class ProfileHomeDetailScreen(
    override val featureKey: ProfileHomeDetailKey,
) : BaseScreen<ProfileHomeDetailKey> {

    @Composable
    override fun Content() {
        val navigation = LocalBackStackController.current
        val sheetController = LocalSheetController.current
        val context = LocalContext.current
        val vm: ProfileHomeDetailVm = hiltViewModel()
        val uiState by vm.state.collectAsState()

        val profileDetails = featureKey.data

        val tabs = remember {
            listOf(
                FooterTab(MainHomeKey, "Home", Icons.Filled.Home) {
                    navigation.popTo(MainHomeKey)
                },
                FooterTab(ProfileHomeDetailKey::class, "Profile", Icons.Filled.AccountCircle) {
                    // Already on Profile
                },
            )
        }

        LaunchedEffect(profileDetails) {
            vm.uiEvent(ProfileHomeDetailContract.Intent.OnGetDetails(profileDetails))
        }

        LaunchedEffect(Unit) {
            vm.effect.collect { effect ->
                when (effect) {
                    is ProfileHomeDetailContract.Effect.ShowMessage -> showToast(context, effect.message)
                    ProfileHomeDetailContract.Effect.Logout -> navigation.clearAndPush(MainHomeKey)
                    ProfileHomeDetailContract.Effect.OnDeleteAccount -> navigation.clearAndPush(MainHomeKey)
                    ProfileHomeDetailContract.Effect.NavigateBack -> navigation.pop()
                }
            }
        }

        Scaffold(
            modifier = Modifier.statusBarsPadding(),
            topBar = {
                AppHeader(
                    title = "My Profile",
                    showBackButton = false,
                    rightContent = {
                        IconButton(
                            onClick = {
                                sheetController.show(
                                    DialogBottomSheet(
                                        data = DialogSheetData(
                                            title = "Logout",
                                            isDialog = false,
                                            subtitle = "Are you sure you want to logout?",
                                            onDismiss = { sheetController.clear() },
                                            onConfirm = {
                                                sheetController.clear()
                                                vm.uiEvent(ProfileHomeDetailContract.Intent.OnLogout)
                                            },
                                        )
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Logout",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            },
            bottomBar = {
                BottomFooter(navigation = navigation, tabs = tabs)
            },
            containerColor = Color(0xFFF8FBFF),
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->
            var visible by remember { mutableStateOf(value = false) }
            LaunchedEffect(Unit) { visible = true }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -40 }
                    ) {
                        ProfileAvatarSection(uiState.name)
                    }
                }

                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(600, 100)) + slideInVertically(tween(600, 100)) { 40 }
                    ) {
                        TotalProgressCard(
                            progress = uiState.totalProgress,
                            correct = uiState.totalCorrect,
                            total = uiState.totalPossible
                        )
                    }
                }

                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(600, 200)) + slideInVertically(tween(600, 200)) { 40 }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            InteractiveProfileCard(
                                title = "Edit Profile",
                                icon = Icons.Default.Edit,
                                color = KidsNumbers,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    sheetController.show(
                                        EditProfileSheet(
                                            state = uiState,
                                            onIntent = { vm.uiEvent(it) },
                                            onDeleteRequest = {
                                                sheetController.show(
                                                    DialogBottomSheet(
                                                        data = DialogSheetData(
                                                            title = "Delete Account",
                                                            isDialog = false,
                                                            subtitle = "Are you sure you want to delete your account?",
                                                            onDismiss = { sheetController.clear() },
                                                            onConfirm = {
                                                                sheetController.clear()
                                                                vm.uiEvent(ProfileHomeDetailContract.Intent.OnDeleteAccount)
                                                            },
                                                        )
                                                    )
                                                )
                                            },
                                            onDismiss = { sheetController.clear() }
                                        )
                                    )
                                }
                            )
                            InteractiveProfileCard(
                                title = "Statistics",
                                icon = Icons.Default.BarChart,
                                color = KidsABC,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    sheetController.show(
                                        StatisticsSheet(categories = uiState.categoriesProgress)
                                    )
                                }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }

    @Composable
    private fun InteractiveProfileCard(
        title: String,
        icon: ImageVector,
        color: Color,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Card(
            onClick = onClick,
            modifier = modifier.height(120.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }

    @Composable
    private fun ProfileAvatarSection(name: String) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(KidsNumbers, KidsABC))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.firstOrNull()?.toString()?.uppercase() ?: "?",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Kid Profile", color = Color.Gray, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
    }

    @Composable
    private fun TotalProgressCard(progress: Float, correct: Int, total: Int) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = KidsNumbers)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Total Achievement", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = "$correct / $total lessons completed! 🌟", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(60.dp),
                        color = Color.White,
                        strokeWidth = 6.dp,
                        trackColor = Color.White.copy(alpha = 0.3f),
                    )
                    Text(text = "${(progress * 100).toInt()}%", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
