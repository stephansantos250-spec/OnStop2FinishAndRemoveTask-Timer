package io.github.takusan23.onstop2finishandremovetask.tool

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

object CloseDelaySettings {
    const val MIN_SECONDS = 20
    const val MAX_SECONDS = 60
    const val DEFAULT_SECONDS = 60

    // Old installations stored minutes. Preserve a one-minute delay and cap longer
    // values at the new maximum; app selections are stored separately and untouched.
    fun observe(context: Context) = context.dataStore.data.map { preferences ->
        val seconds = preferences[PreferenceKeys.KEY_CLOSE_DELAY_SECONDS]
            ?: preferences[PreferenceKeys.KEY_CLOSE_DELAY_MINUTES]?.coerceIn(1, 5)?.times(60)
            ?: DEFAULT_SECONDS
        seconds.coerceIn(MIN_SECONDS, MAX_SECONDS)
    }.distinctUntilChanged()

    suspend fun save(context: Context, seconds: Int) {
        context.dataStore.edit {
            it[PreferenceKeys.KEY_CLOSE_DELAY_SECONDS] = seconds.coerceIn(MIN_SECONDS, MAX_SECONDS)
        }
    }
}
