package io.github.takusan23.onstop2finishandremovetask.tool

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

object CloseDelaySettings {
    const val DEFAULT_MINUTES = 1

    fun observe(context: Context) = context.dataStore.data.map {
        (it[PreferenceKeys.KEY_CLOSE_DELAY_MINUTES] ?: DEFAULT_MINUTES).coerceIn(1, 5)
    }.distinctUntilChanged()

    suspend fun save(context: Context, minutes: Int) {
        context.dataStore.edit {
            it[PreferenceKeys.KEY_CLOSE_DELAY_MINUTES] = minutes.coerceIn(1, 5)
        }
    }
}
