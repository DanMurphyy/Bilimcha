package com.danmurphyy.bilimcha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import com.danmurphyy.bilimcha.navigations.AppNavigation
import com.danmurphyy.bilimcha.navigations.BackStackController
import com.danmurphyy.bilimcha.navigations.SheetController
import com.danmurphyy.bilimcha.navigations.appNavEntries
import com.danmurphyy.bilimcha.ui.theme.BilimchaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var backStackController: BackStackController

    @Inject
    lateinit var sheetController: SheetController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BilimchaTheme {
                AppNavigation(
                    innerPadding = PaddingValues(0.dp),
                    navEntries = appNavEntries,
                    backStackController = backStackController,
                    sheetController = sheetController
                )
            }
        }
    }
}
