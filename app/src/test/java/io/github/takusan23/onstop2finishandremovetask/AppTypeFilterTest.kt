package io.github.takusan23.onstop2finishandremovetask

import io.github.takusan23.onstop2finishandremovetask.tool.AppTypeFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppTypeFilterTest {
    @Test fun allShowsBothKinds() {
        assertTrue(AppTypeFilter.ALL.matches(false))
        assertTrue(AppTypeFilter.ALL.matches(true))
    }

    @Test fun installedExcludesSystemApps() {
        assertTrue(AppTypeFilter.INSTALLED.matches(false))
        assertFalse(AppTypeFilter.INSTALLED.matches(true))
    }

    @Test fun systemExcludesUserApps() {
        assertFalse(AppTypeFilter.SYSTEM.matches(false))
        assertTrue(AppTypeFilter.SYSTEM.matches(true))
    }

    @Test fun categoryFiltersPartitionTheList() {
        val apps = listOf("user.a" to false, "system.a" to true, "user.b" to false, "system.b" to true)
        fun packages(filter: AppTypeFilter) = apps.filter { filter.matches(it.second) }.map { it.first }.toSet()
        val installed = packages(AppTypeFilter.INSTALLED)
        val system = packages(AppTypeFilter.SYSTEM)
        assertEquals(setOf("user.a", "user.b"), installed)
        assertEquals(setOf("system.a", "system.b"), system)
        assertTrue(installed.intersect(system).isEmpty())
        assertEquals(packages(AppTypeFilter.ALL), installed + system)
    }
}
