package com.example.seminar_assignment_2025.search

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

val Context.dataStore by preferencesDataStore(name = "recent_searches")

class RecentSearchRepository(private val context: Context) {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private companion object {
        val SEARCHES_KEY = stringPreferencesKey("json_searches")
    }

    fun getRecentSearches(): Flow<List<String>> {
        return context.dataStore.data.map { preferences ->
            // 키에서 JSON 문자열을 읽어옵니다.
            val jsonString = preferences[SEARCHES_KEY]
            if (jsonString.isNullOrEmpty()) {
                emptyList()
            } else {
                // JSON 문자열을 List<String>으로 역직렬화합니다.
                json.decodeFromString<List<String>>(jsonString)
            }
        }
    }

    suspend fun addSearch(query: String) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[SEARCHES_KEY] ?: "[]"
            val list = json.decodeFromString<MutableList<String>>(currentJson)

            // 중복 제거 및 맨 앞에 추가
            list.remove(query)
            list.add(0, query)
            // (선택) 목록 크기 제한 (예: 10개)
            // while (list.size > 10) { list.removeLast() }

            // List<String>을 JSON 문자열로 직렬화하여 저장합니다.
            preferences[SEARCHES_KEY] = json.encodeToString(list)
        }
    }

    suspend fun deleteSearch(query: String) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[SEARCHES_KEY] ?: "[]"
            val list = json.decodeFromString<MutableList<String>>(currentJson)
            list.remove(query)
            preferences[SEARCHES_KEY] = json.encodeToString(list)
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences[SEARCHES_KEY] = "[]" // 빈 리스트로 덮어쓰기
        }
    }

}
