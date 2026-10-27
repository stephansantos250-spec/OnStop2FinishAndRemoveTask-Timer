package io.github.takusan23.onstop2finishandremovetask

import io.github.takusan23.onstop2finishandremovetask.tool.BackgroundTaskTimer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BackgroundTaskTimerTest {
    private val a = BackgroundTaskTimer.Task(1, "a", "A")
    private val b = BackgroundTaskTimer.Task(2, "b", "B")
    private val home = BackgroundTaskTimer.Task(3, "home", "Home")

    @Test fun eachSelectableDelayExpiresOnlyAfterItsDeadline() = runTest {
        for (minutes in 1..5) {
            val closed = mutableListOf<BackgroundTaskTimer.Task>()
            val timer = BackgroundTaskTimer(this, closed::add)
            timer.configure(setOf("a"), minutes)
            timer.onForeground(a)
            timer.onForeground(home)
            runCurrent()
            advanceTimeBy(minutes * 60_000L - 1)
            runCurrent()
            assertTrue(closed.isEmpty())
            advanceTimeBy(1)
            runCurrent()
            assertEquals(listOf(a), closed)
        }
    }

    @Test fun reopeningCancelsAndNextExitStartsFresh() = runTest {
        val closed = mutableListOf<BackgroundTaskTimer.Task>()
        val timer = BackgroundTaskTimer(this, closed::add)
        timer.configure(setOf("a"), 1)
        timer.onForeground(a)
        timer.onForeground(home)
        runCurrent()
        advanceTimeBy(30_000)
        timer.onForeground(a)
        advanceTimeBy(90_000)
        assertTrue(closed.isEmpty())
        timer.onForeground(home)
        runCurrent()
        advanceTimeBy(60_000)
        runCurrent()
        assertEquals(listOf(a), closed)
    }

    @Test fun switchingBetweenSelectedAppsKeepsIndependentTimers() = runTest {
        val closed = mutableListOf<BackgroundTaskTimer.Task>()
        val timer = BackgroundTaskTimer(this, closed::add)
        timer.configure(setOf("a", "b"), 1)
        timer.onForeground(a)
        timer.onForeground(b)
        runCurrent()
        advanceTimeBy(20_000)
        timer.onForeground(home)
        runCurrent()
        advanceTimeBy(40_000)
        runCurrent()
        assertEquals(listOf(a), closed)
        advanceTimeBy(20_000)
        runCurrent()
        assertEquals(listOf(a, b), closed)
    }

    @Test fun changingDelayRestartsPendingCountdown() = runTest {
        val closed = mutableListOf<BackgroundTaskTimer.Task>()
        val timer = BackgroundTaskTimer(this, closed::add)
        timer.configure(setOf("a"), 1)
        timer.onForeground(a)
        timer.onForeground(home)
        runCurrent()
        advanceTimeBy(30_000)
        timer.configure(setOf("a"), 2)
        runCurrent()
        advanceTimeBy(119_999)
        runCurrent()
        assertTrue(closed.isEmpty())
        advanceTimeBy(1)
        runCurrent()
        assertEquals(listOf(a), closed)
    }

    @Test fun deselectingAppCancelsItsTimer() = runTest {
        val closed = mutableListOf<BackgroundTaskTimer.Task>()
        val timer = BackgroundTaskTimer(this, closed::add)
        timer.configure(setOf("a"), 1)
        timer.onForeground(a)
        timer.onForeground(home)
        runCurrent()
        timer.configure(emptySet(), 1)
        advanceTimeBy(120_000)
        runCurrent()
        assertTrue(closed.isEmpty())
    }

    @Test fun removingTaskOrStoppingServiceCancelsPendingTimers() = runTest {
        val closed = mutableListOf<BackgroundTaskTimer.Task>()
        val timer = BackgroundTaskTimer(this, closed::add)
        timer.configure(setOf("a", "b"), 1)
        timer.onForeground(a)
        timer.onForeground(b)
        timer.onForeground(home)
        runCurrent()
        timer.onTaskRemoved(a.id)
        timer.cancelAll()
        advanceTimeBy(120_000)
        runCurrent()
        assertTrue(closed.isEmpty())
    }

    @Test fun anotherTaskOfSamePackageCancelsTheTimer() = runTest {
        val closed = mutableListOf<BackgroundTaskTimer.Task>()
        val timer = BackgroundTaskTimer(this, closed::add)
        timer.configure(setOf("a"), 1)
        timer.onForeground(a)
        timer.onForeground(home)
        runCurrent()
        timer.onForeground(a.copy(id = 99))
        advanceTimeBy(120_000)
        runCurrent()
        assertTrue(closed.isEmpty())
    }

    @Test fun unselectedAppsAreNeverClosed() = runTest {
        val closed = mutableListOf<BackgroundTaskTimer.Task>()
        val timer = BackgroundTaskTimer(this, closed::add)
        timer.configure(setOf("a"), 1)
        timer.onForeground(b)
        timer.onForeground(home)
        advanceTimeBy(120_000)
        runCurrent()
        assertTrue(closed.isEmpty())
    }
}
