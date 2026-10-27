package io.github.takusan23.onstop2finishandremovetask.tool

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

/** DataStore */
val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "settings")

object PreferenceKeys {

    val KEY_CLOSE_DELAY_MINUTES = intPreferencesKey("close_delay_minutes")

    /** onStop で finishAndRemoveTask するアプリの applicationId */
    val KEY_APPLICATION_ID_JSONARRAY = stringPreferencesKey("key_application_id_jsonarray")

}