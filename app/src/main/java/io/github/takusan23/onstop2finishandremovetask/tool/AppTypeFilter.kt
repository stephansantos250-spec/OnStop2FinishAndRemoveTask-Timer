package io.github.takusan23.onstop2finishandremovetask.tool

/** Display filtering only: switching categories never changes registered apps. */
enum class AppTypeFilter {
    ALL, INSTALLED, SYSTEM;

    fun matches(isSystemApp: Boolean): Boolean = when (this) {
        ALL -> true
        INSTALLED -> !isSystemApp
        SYSTEM -> isSystemApp
    }
}
