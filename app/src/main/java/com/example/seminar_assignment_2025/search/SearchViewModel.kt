package com.example.seminar_assignment_2025.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(
    private val recentRepo: RecentSearchRepository,
    private val movieRepo: MovieRepository
) : ViewModel() {

    val recentSearches = recentRepo.getRecentSearches()
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Movie>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie = _selectedMovie.asStateFlow()

    fun onClearQuery() {
        _searchQuery.value = ""
    }

    // 스펙: 최근 검색어 아이템 클릭 (검색어로 채우기)
    fun RecentSearchItemClicked(query: String) {
        _searchQuery.value = query
    }

    // 스펙: 최근 검색어 개별 삭제 ('X')
    fun deleteRecent(query: String) {
        viewModelScope.launch {
            recentRepo.deleteSearch(query)
        }
    }

    // 스펙: 최근 검색어 전체 삭제
    fun clearRecent() {
        viewModelScope.launch {
            recentRepo.clearAll()
        }
    }
    fun onQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSearchClicked() {
        val query = _searchQuery.value
        if (query.isBlank()) return // 빈 검색어는 무시

        viewModelScope.launch {
            // 1. 영화 검색 실행
            _searchResults.value = movieRepo.searchByTitle(query)
            // 2. 최근 검색어에 저장
            recentRepo.addSearch(query)
        }
    }

    fun selectMovie(movie: Movie) {
        _selectedMovie.value = movie
    }
}

class SearchViewModelFactory(
    private val movieRepo: MovieRepository,
    private val recentSearchRepo: RecentSearchRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(recentSearchRepo, movieRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}