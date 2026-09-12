package com.danmurphyy.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test class generates a basic startup baseline profile for the target package.
 *
 * We recommend you start with this but add important user flows to the profile to improve their performance.
 * Refer to the [baseline profile documentation](https://d.android.com/topic/performance/baselineprofiles)
 * for more information.
 *
 * You can run the generator with the "Generate Baseline Profile" run configuration in Android Studio or
 * the equivalent `generateBaselineProfile` gradle task:
 * ```
 * ./gradlew :app:generateReleaseBaselineProfile
 * ```
 * The run configuration runs the Gradle task and applies filtering to run only the generators.
 *
 * Check [documentation](https://d.android.com/topic/performance/benchmarking/macrobenchmark-instrumentation-args)
 * for more information about available instrumentation arguments.
 *
 * After you run the generator, you can verify the improvements running the [StartupBenchmarks] benchmark.
 *
 * When using this class to generate a baseline profile, only API 33+ or rooted API 28+ are supported.
 *
 * The minimum required version of androidx.benchmark to generate a baseline profile is 1.2.0.
 **/
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        // The application id for the running build variant is read from the instrumentation arguments.
        rule.collect(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),

            // See: https://d.android.com/topic/performance/baselineprofiles/dex-layout-optimizations
            includeInStartupProfile = true
        ) {
            // Start default activity for your app
            pressHome()
            startActivityAndWait()

            // 1. Numbers Flow
            device.wait(Until.findObject(By.text("Numbers")), 5000)?.click()
            device.wait(Until.hasObject(By.text("Numbers Practice")), 5000)

            // Interact with dashboard elements to capture their classes
            device.findObject(By.scrollable(true))?.scroll(Direction.DOWN, 0.3f)
            device.waitForIdle()

            // Start Learn
            device.wait(Until.findObject(By.text("🚀 LEARN")), 3000)?.click()
            device.waitForIdle()
            device.pressBack() // Back to Numbers Dashboard
            device.waitForIdle()

            // Start Test
            device.wait(Until.findObject(By.text("🎯 START TEST")), 3000)?.click()
            device.waitForIdle()
            device.pressBack() // Back to Numbers Dashboard
            device.waitForIdle()

            device.pressBack() // Back to Home
            device.waitForIdle()

            // 2. Alphabet Flow
            device.wait(Until.findObject(By.text("Alphabet")), 5000)?.click()
            device.wait(Until.hasObject(By.text("Alphabet")), 5000)

            // Start Learn
            device.wait(Until.findObject(By.text("🚀 LEARN")), 3000)?.click()
            device.waitForIdle()
            device.pressBack()
            device.waitForIdle()

            // Start Test (even if it's mock, captures UI interaction)
            device.wait(Until.findObject(By.text("🎯 START TEST")), 3000)?.click()
            device.waitForIdle()

            device.pressBack()
            device.waitForIdle()

            // 3. Animals Flow
            device.wait(Until.findObject(By.text("Animals")), 5000)?.click()
            device.wait(Until.hasObject(By.text("Animals")), 5000)

            device.wait(Until.findObject(By.text("🚀 LEARN")), 3000)?.click()
            device.waitForIdle()
            device.pressBack()
            device.waitForIdle()

            device.pressBack()
            device.waitForIdle()

            // 4. Profile Flow
            device.wait(Until.findObject(By.text("Profile")), 5000)?.click()
            device.wait(Until.hasObject(By.text("Logout")), 5000)

            device.findObject(By.text("Logout"))?.click()
            device.waitForIdle() // Logout triggers clearAndPush(MainHomeKey)
        }
    }
}