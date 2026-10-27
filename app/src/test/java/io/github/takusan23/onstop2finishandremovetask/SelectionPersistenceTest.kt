package io.github.takusan23.onstop2finishandremovetask

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import io.github.takusan23.onstop2finishandremovetask.tool.RegisterAppListTool
import kotlinx.coroutines.*
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = Application::class)
class SelectionPersistenceTest {
    @Test fun rapidTogglesDoNotLoseOtherSelections() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Application>()
        RegisterAppListTool.saveApplicationIdList(context, emptyList())
        coroutineScope {
            (1..20).map { id -> async(Dispatchers.Default) {
                RegisterAppListTool.setApplicationRegistered(context, "sample.app.$id", true)
            } }.awaitAll()
        }
        assertEquals((1..20).map { "sample.app.$it" }.toSet(), RegisterAppListTool.readApplicationIdList(context).toSet())
        coroutineScope {
            (1..10).map { id -> async(Dispatchers.Default) {
                RegisterAppListTool.setApplicationRegistered(context, "sample.app.$id", false)
            } }.awaitAll()
        }
        assertEquals((11..20).map { "sample.app.$it" }.toSet(), RegisterAppListTool.readApplicationIdList(context).toSet())
    }
}
