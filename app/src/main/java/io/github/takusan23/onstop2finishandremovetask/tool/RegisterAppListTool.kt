package io.github.takusan23.onstop2finishandremovetask.tool

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray

/** DataStore にある JSON を読み書きするクラス */
object RegisterAppListTool {

    fun realtimeReadApplicationIdList(context: Context): Flow<List<String>> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.KEY_APPLICATION_ID_JSONARRAY]?.toJsonArray() ?: emptyList()
    }

    /** Atomic read-modify-write prevents lost selections during rapid toggles. */
    suspend fun setApplicationRegistered(context: Context, packageName: String, registered: Boolean) {
        context.dataStore.edit { preferences ->
            val apps = preferences[PreferenceKeys.KEY_APPLICATION_ID_JSONARRAY]?.toJsonArray()?.toMutableSet() ?: mutableSetOf()
            if (registered) apps.add(packageName) else apps.remove(packageName)
            preferences[PreferenceKeys.KEY_APPLICATION_ID_JSONARRAY] = JSONArray(apps.toList()).toString()
        }
    }

    suspend fun readApplicationIdList(context: Context): List<String> {
        val preference = context.dataStore.data.first()
        val jsonArrayString = preference[PreferenceKeys.KEY_APPLICATION_ID_JSONARRAY]
        return jsonArrayString?.toJsonArray() ?: emptyList()
    }

    suspend fun saveApplicationIdList(context: Context, applicationIdList: List<String>) {
        val jsonArray = JSONArray().also { array ->
            applicationIdList.forEach { array.put(it) }
        }
        val jsonArrayString = jsonArray.toString()
        context.dataStore.edit { it[PreferenceKeys.KEY_APPLICATION_ID_JSONARRAY] = jsonArrayString }
    }

    private fun String.toJsonArray(): List<String> {
        val jsonArray = JSONArray(this)
        return (0 until jsonArray.length()).map { jsonArray.getString(it) }
    }

}