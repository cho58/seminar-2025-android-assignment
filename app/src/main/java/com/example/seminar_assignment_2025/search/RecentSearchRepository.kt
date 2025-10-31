package com.example.seminar_assignment_2025.search

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.dataStore by preferencesDataStore(name = "search_prefs")

class RecentSearchRepository(private val context: Context) {
    private val KEY = stringPreferencesKey("recent_list")
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getRecent(): List<String> {
        val prefs = context.dataStore.data.first()
        val jsonStr = prefs[KEY] ?: return emptyList()
        return try {
            json.decodeFromString(jsonStr)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun add(keyword: String) {
        val list = getRecent().toMutableList()
        list.remove(keyword)
        list.add(0, keyword)
        if (list.size > 10) list.removeAt(list.lastIndex)
        save(list)
    }

    suspend fun delete(keyword: String) {
        val list = getRecent().filter { it != keyword }
        save(list)
    }

    suspend fun clear() {
        save(emptyList())
    }

    private suspend fun save(list: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[KEY] = json.encodeToString(list)
        }
    }
}
