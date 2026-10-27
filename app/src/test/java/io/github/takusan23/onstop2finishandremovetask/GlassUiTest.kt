package io.github.takusan23.onstop2finishandremovetask

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import org.robolectric.shadows.ShadowDialog
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import io.github.takusan23.onstop2finishandremovetask.tool.AppTypeFilter
import io.github.takusan23.onstop2finishandremovetask.ui.screen.*
import io.github.takusan23.onstop2finishandremovetask.ui.theme.OnStop2FinishAndRemoveTaskTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = Application::class, qualifiers = "pt-rBR-w390dp-h844dp-port-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class GlassUiTest {
    @get:Rule val rule = createComposeRule()
    private val fixtures = listOf(
        AppListViewModel.AppInfo(true, false, "Calculadora", "preview.calculator", "1"),
        AppListViewModel.AppInfo(false, false, "Arquivos", "preview.files", "1"),
        AppListViewModel.AppInfo(true, true, "Relógio", "preview.clock", "1"),
        AppListViewModel.AppInfo(false, true, "Configurações", "preview.settings", "1")
    )
    private fun shot(name: String) {
        rule.waitForIdle()
        lateinit var bitmap: Bitmap
        rule.runOnIdle {
            val activity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED).first()
            val dialog = ShadowDialog.getLatestDialog()?.takeIf { it.isShowing }
            val view = activity.window.decorView
            check(view.width > 0 && view.height > 0)
            bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            dialog?.window?.decorView?.draw(canvas)
        }
        val directory = File(requireNotNull(System.getProperty("glass.screenshots.dir")))
        directory.mkdirs()
        File(directory, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
    @Test fun mainLight() {
        rule.setContent { OnStop2FinishAndRemoveTaskTheme(darkTheme = false) {
            GlassAppListContent(fixtures, AppListViewModel.SearchOption(), 2, 3, loadIcons = false)
        } }
        rule.onNodeWithText("Todos").assertIsDisplayed()
        shot("main-light")
    }
    @Test fun mainDark() {
        rule.setContent { OnStop2FinishAndRemoveTaskTheme(darkTheme = true) {
            GlassAppListContent(fixtures, AppListViewModel.SearchOption(), 2, 3, loadIcons = false)
        } }
        shot("main-dark")
    }
    @Test fun settingsLightAndPresetInteraction() {
        var selected = 3
        rule.setContent { OnStop2FinishAndRemoveTaskTheme(darkTheme = false) {
            GlassSettingsContent(3, onMinutes = { selected = it })
        } }
        shot("settings-light")
        rule.onNodeWithText("5").performClick()
        rule.runOnIdle { assertEquals(5, selected) }
    }
    @Test fun settingsDark() {
        rule.setContent { OnStop2FinishAndRemoveTaskTheme(darkTheme = true) { GlassSettingsContent(3) } }
        shot("settings-dark")
    }
    @Test fun emptyCategoryHasActionableMessage() {
        rule.setContent { OnStop2FinishAndRemoveTaskTheme(darkTheme = false) {
            GlassAppListContent(emptyList(), AppListViewModel.SearchOption(appType = AppTypeFilter.INSTALLED), 0, 1, loadIcons = false)
        } }
        rule.onNodeWithText("Nenhum app por aqui").assertIsDisplayed()
        shot("empty-light")
    }
    @Test @Config(qualifiers = "pt-rBR-w320dp-h740dp-port-mdpi") fun narrowLargeText() {
        rule.setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, 1.25f)) {
                OnStop2FinishAndRemoveTaskTheme(darkTheme = false) {
                    GlassAppListContent(fixtures, AppListViewModel.SearchOption(appType = AppTypeFilter.INSTALLED), 2, 5, loadIcons = false)
                }
            }
        }
        rule.onNodeWithText("Instalados").assertIsDisplayed()
        shot("main-narrow-large-text")
    }
    @Test fun connectScreen() {
        rule.setContent { OnStop2FinishAndRemoveTaskTheme(darkTheme = false) { GlassShizukuContent(false) } }
        shot("connect-light")
    }
    @Test fun permissionScreen() {
        rule.setContent { OnStop2FinishAndRemoveTaskTheme(darkTheme = true) { GlassShizukuContent(true) } }
        shot("permission-dark")
    }
    @Test fun licensesScreen() {
        rule.setContent { OnStop2FinishAndRemoveTaskTheme(darkTheme = false) { LicenseScreen() } }
        shot("licenses-light")
    }
    @Test fun searchSheet() {
        rule.setContent { OnStop2FinishAndRemoveTaskTheme(darkTheme = false) {
            GlassAppListContent(fixtures, AppListViewModel.SearchOption(), 2, 3, loadIcons = false)
            SearchBottomSheet({}, AppListViewModel.SearchOption(), {})
        } }
        shot("search-light")
    }
    @Test fun categorySelectionCallback() {
        var selected = AppTypeFilter.ALL
        rule.setContent { OnStop2FinishAndRemoveTaskTheme(darkTheme = false) {
            GlassAppListContent(fixtures, AppListViewModel.SearchOption(), 2, 3,
                onOption = { selected = it.appType }, loadIcons = false)
        } }
        rule.onNodeWithText("Sistema").performClick()
        rule.runOnIdle { assertEquals(AppTypeFilter.SYSTEM, selected) }
    }
    @Test fun selectingAnAppUpdatesItsCard() {
        var apps by mutableStateOf(fixtures)
        rule.setContent { OnStop2FinishAndRemoveTaskTheme(darkTheme = false) {
            GlassAppListContent(apps, AppListViewModel.SearchOption(), apps.count { it.isRegistered }, 3,
                onCheck = { checked, packageName ->
                    apps = apps.map { if (it.packageName == packageName) it.copy(isRegistered = checked) else it }
                }, loadIcons = false)
        } }
        rule.onNodeWithText("Arquivos").performScrollTo().performClick()
        rule.runOnIdle { assertEquals(true, apps.first { it.packageName == "preview.files" }.isRegistered) }
        shot("selection-light")
    }
    @Test fun systemCategoryDark() {
        rule.setContent { OnStop2FinishAndRemoveTaskTheme(darkTheme = true) {
            GlassAppListContent(fixtures.filter { it.isSystemApp }, AppListViewModel.SearchOption(appType = AppTypeFilter.SYSTEM), 2, 3, loadIcons = false)
        } }
        shot("system-dark")
    }
}
