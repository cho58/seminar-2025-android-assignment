package com.example.seminar_assignment_2025.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "search_history")

class SearchHistoryRepository(private val context: Context) {

    private val searchHistoryKey = stringPreferencesKey("search_history_list")

    val searchHistory: Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            val jsonString = preferences[searchHistoryKey]
            if (jsonString.isNullOrEmpty()) {
                emptyList()
            } else {
                Json.decodeFromString<List<String>>(jsonString)
            }
        }

    suspend fun addSearchTerm(term: String) {
        context.dataStore.edit { preferences ->
            val currentList = preferences[searchHistoryKey]?.let { json ->
                Json.decodeFromString<List<String>>(json)
            } ?: emptyList()
            val updatedList = (listOf(term) + currentList).distinct().take(10) //최근 10개만 저장
            preferences[searchHistoryKey] = Json.encodeToString(updatedList)
        }
    }

    suspend fun removeSearchTerm(term: String) {
        context.dataStore.edit { preferences ->
            val currentList = preferences[searchHistoryKey]?.let { json ->
                Json.decodeFromString<List<String>>(json)
            } ?: emptyList()
            val updatedList = currentList.filter { it != term }
            preferences[searchHistoryKey] = Json.encodeToString(updatedList)
        }
    }

    suspend fun clearSearchHistory() {
        context.dataStore.edit { preferences ->
            preferences.remove(searchHistoryKey)
        }
    }
}
