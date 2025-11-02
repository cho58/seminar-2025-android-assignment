package com.example.seminar_assignment_2025.ui.search

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.seminar_assignment_2025.data.Movie
import com.example.seminar_assignment_2025.data.MovieRepository
import com.example.seminar_assignment_2025.data.MovieRepositoryImpl
import com.example.seminar_assignment_2025.data.SearchHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchHistoryRepository: SearchHistoryRepository,
    private val movieRepository: MovieRepository
) : ViewModel() {

    val searchHistory: StateFlow<List<String>> = searchHistoryRepository.searchHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchResults = MutableStateFlow<List<Movie>>(emptyList())
    val searchResults: StateFlow<List<Movie>> = _searchResults.asStateFlow()

    fun addSearchTerm(term: String) {
        viewModelScope.launch {
            searchHistoryRepository.addSearchTerm(term)
        }
    }

    fun removeSearchTerm(term: String) {
        viewModelScope.launch {
            searchHistoryRepository.removeSearchTerm(term)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchHistoryRepository.clearSearchHistory()
        }
    }

    fun searchByTitle(query: String) {
        viewModelScope.launch {
            _searchResults.value = movieRepository.searchByTitle(query)
        }
    }

    fun getGenreName(id: Int): String {
        return movieRepository.getGenreName(id)
    }
}

class SearchViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(
                searchHistoryRepository = SearchHistoryRepository(application),
                movieRepository = MovieRepositoryImpl()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}