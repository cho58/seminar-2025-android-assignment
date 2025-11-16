package com.example.seminar_assignment_2025.search.data.repository

import com.example.seminar_assignment_2025.search.domain.Movie
import com.example.seminar_assignment_2025.search.domain.MovieDetail

interface MovieRepository {
    suspend fun searchByTitle(query: String): List<Movie>
    suspend fun getMovieDetail(id: Int): MovieDetail
}



