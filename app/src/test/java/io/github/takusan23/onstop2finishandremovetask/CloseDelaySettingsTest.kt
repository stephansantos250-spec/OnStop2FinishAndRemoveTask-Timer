package io.github.takusan23.onstop2finishandremovetask

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import io.github.takusan23.onstop2finishandremovetask.tool.CloseDelaySettings
import io.github.takusan23.onstop2finishandremovetask.tool.PreferenceKeys
import io.github.takusan23.onstop2finishandremovetask.tool.RegisterAppListTool
import io.github.takusan23.onstop2finishandremovetask.tool.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = Application::class)
class CloseDelaySettingsTest {
    @Test fun secondsArePersistedAndClampedWithoutChangingSelectedApps() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Application>()
        context.dataStore.edit { it.remove(PreferenceKeys.KEY_CLOSE_DELAY_SECONDS); it.remove(PreferenceKeys.KEY_CLOSE_DELAY_MINUTES) }
        RegisterAppListTool.saveApplicationIdList(context, listOf("sample.app"))
        assertEquals(60, CloseDelaySettings.observe(context).first())
        CloseDelaySettings.save(context, 27)
        assertEquals(27, CloseDelaySettings.observe(context).first())
        CloseDelaySettings.save(context, 10)
        assertEquals(20, CloseDelaySettings.observe(context).first())
        CloseDelaySettings.save(context, 120)
        assertEquals(60, CloseDelaySettings.observe(context).first())
        assertEquals(listOf("sample.app"), RegisterAppListTool.readApplicationIdList(context))
    }

    @Test fun legacyMinutesAreCappedAtTheNewMaximum() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Application>()
        context.dataStore.edit {
            it.remove(PreferenceKeys.KEY_CLOSE_DELAY_SECONDS)
            it[PreferenceKeys.KEY_CLOSE_DELAY_MINUTES] = 5
        }
        assertEquals(60, CloseDelaySettings.observe(context).first())
        CloseDelaySettings.save(context, 20)
        assertEquals(20, CloseDelaySettings.observe(context).first())
    }
}
