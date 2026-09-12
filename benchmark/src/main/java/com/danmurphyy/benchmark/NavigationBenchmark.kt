package com.danmurphyy.benchmark

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

    @Test
    fun navigateToNumbers() = benchmarkRule.measureRepeated(
        packageName = "com.danmurphyy.bilimcha",
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.Full(),
        iterations = 3,
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

        // 3. Force RenderThread activity by performing a small scroll
        // This ensures the FrameTimingMetric has slices to observe
        device.findObject(By.scrollable(true))?.scroll(Direction.DOWN, 0.2f)

        device.waitForIdle()
    }
}