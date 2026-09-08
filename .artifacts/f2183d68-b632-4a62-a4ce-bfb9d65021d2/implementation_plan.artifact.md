# Add Repeat Toggle to Numbers Dashboard

The goal is to add a "Repeat" toggle to the "Additional" settings group in the `NumbersDashboardScreen` and pass this setting to the subsequent Practice and Test screens via navigation keys.

## Proposed Changes

### Navigation

#### [MODIFY] [NavKeys.kt](file:///Users/apple/Bilimcha/app/src/main/java/com/danmurphyy/bilimcha/navigations/NavKeys.kt)
- Add `isRepeat: Boolean = false` to `NumbersPracticeKey`.
- Add `isRepeat: Boolean = false` to `NumbersTestKey`.

### Dashboard Feature

#### [MODIFY] [NumbersDashboardContracts.kt](file:///Users/apple/Bilimcha/app/src/main/java/com/danmurphyy/bilimcha/numbers/dashboard/NumbersDashboardContracts.kt)
- Add `isRepeat: Boolean = false` to `State`.
- Add `data class ToggleRepeat(val enabled: Boolean) : Intent` to `Intent`.
- Update `Effect.NavigateToPractice` and `Effect.NavigateToTest` to include `isRepeat`.

#### [MODIFY] [NumbersDashboardVm.kt](file:///Users/apple/Bilimcha/app/src/main/java/com/danmurphyy/bilimcha/numbers/dashboard/NumbersDashboardVm.kt)
- Implement `ToggleRepeat` intent handler.
- Pass `isRepeat` in navigation effects (`StartPractice`, `StartTest`).

#### [MODIFY] [NumbersDashboardScreen.kt](file:///Users/apple/Bilimcha/app/src/main/java/com/danmurphyy/bilimcha/numbers/dashboard/NumbersDashboardScreen.kt)
- Add a "Repeat" switch to the "Additional" group UI.
- Update navigation logic in `LaunchedEffect` to pass the `isRepeat` parameter.

## Verification Plan

### Manual Verification
- Open the Numbers Dashboard.
- Expand the "Additional" section.
- Verify the "Repeat" toggle is present and clickable.
- Toggle "Repeat" and start "Learn" or "Test".
- Verify (via debugging or temporary UI logs) that the `isRepeat` value is correctly passed to `NumbersPracticeScreen` or `NumbersTestScreen`.
