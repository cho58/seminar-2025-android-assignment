package com.example.seminar_assignment_2025.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seminar_assignment_2025.search.domain.Movie
import com.example.seminar_assignment_2025.search.domain.MovieDetail
import com.example.seminar_assignment_2025.search.data.repository.MovieRepository
import com.example.seminar_assignment_2025.search.data.local.RecentSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val recentRepo: RecentSearchRepository,
    private val movieRepo: MovieRepository
) : ViewModel() {

    val recentSearches = recentRepo.getRecentSearches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Movie>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _selectedMovieDetail = MutableStateFlow<MovieDetail?>(null)
    val selectedMovieDetail = _selectedMovieDetail.asStateFlow()

    fun onClearQuery() {
        _searchQuery.value = ""
    }

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
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

    fun recentSearchItemClicked(keyword: String) {
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
