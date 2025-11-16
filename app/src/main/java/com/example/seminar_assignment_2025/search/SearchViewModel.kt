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

    private val _selectedMovieDetail = MutableStateFlow<MovieDetail?>(null)
    val selectedMovieDetail = _selectedMovieDetail.asStateFlow()

    fun onClearQuery() {
        _searchQuery.value = ""
    }

    fun onSearchClicked() {
        viewModelScope.launch {
            val query = _searchQuery.value.trim()
            if (query.isBlank()) return@launch

            // 최근검색에 저장
            recentRepo.addSearch(query)

            _searchResults.value = movieRepo.searchByTitle(query)
        }
    }

    fun selectMovie(movie: Movie) {
        viewModelScope.launch {
            _selectedMovieDetail.value = null   // 먼저 로딩 상태로
            try {
                val detail = movieRepo.getMovieDetail(movie.id)
                _selectedMovieDetail.value = detail
            } catch (e: Exception) {
                e.printStackTrace()
                // 필요하면 여기서 에러 상태 관리
            }
        }
    }

    fun RecentSearchItemClicked(keyword: String) {
        viewModelScope.launch {
            _searchQuery.value = keyword
            _searchResults.value = movieRepo.searchByTitle(keyword)
        }
    }

    fun deleteRecent(keyword: String) {
        viewModelScope.launch {
            recentRepo.deleteSearch(keyword)
        }
    }

    fun clearRecent() {
        viewModelScope.launch {
            recentRepo.clearAll()
        }
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