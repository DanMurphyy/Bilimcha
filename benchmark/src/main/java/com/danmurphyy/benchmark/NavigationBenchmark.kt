package com.danmurphyy.benchmark

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    // Mode 1: No Optimization (Simulates a fresh install without profile)
    @Test
    fun navigateToNumbersNoCompilation() = navigateToNumbers(CompilationMode.None())

    // Mode 2: Baseline Profile Optimization (The "AOT" result)
    @Test
    fun navigateToNumbersWithBaselineProfile() = navigateToNumbers(
        CompilationMode.Partial(
            baselineProfileMode = BaselineProfileMode.Require
        )
    )

    private fun navigateToNumbers(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = "com.danmurphyy.bilimcha",
        metrics = listOf(FrameTimingMetric()),
        compilationMode = compilationMode,
        iterations = 5,
        startupMode = StartupMode.COLD
    ) {
        pressHome()
        startActivityAndWait()

        // 1. Locate and click the card
        val numbersCard = device.wait(Until.findObject(By.text("Numbers")), 5000)
            ?: throw IllegalStateException("Numbers card not found")
        numbersCard.click()

        // 2. Wait for the transition to complete
        device.wait(Until.hasObject(By.text("Numbers Practice")), 5000)

        // 3. Force some UI activity to ensure metrics capture frames
        device.findObject(By.scrollable(true))?.scroll(Direction.DOWN, 0.2f)

        device.waitForIdle()
    }
}