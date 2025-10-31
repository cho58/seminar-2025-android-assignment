package com.example.seminar_assignment_2025.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val repo: RecentSearchRepository) : ViewModel() {

    private val _recentList = MutableStateFlow<List<String>>(emptyList())
    val recentList = _recentList.asStateFlow()

    var query = MutableStateFlow("")

    init {
        loadRecent()
    }

    fun loadRecent() {
        viewModelScope.launch {
            _recentList.value = repo.getRecent()
        }
    }

    fun addRecent(keyword: String) {
        viewModelScope.launch {
            repo.add(keyword)
            _recentList.value = repo.getRecent()
        }
    }

    fun deleteRecent(keyword: String) {
        viewModelScope.launch {
            repo.delete(keyword)
            _recentList.value = repo.getRecent()
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repo.clear()
            _recentList.value = emptyList()
        }
    }
}
