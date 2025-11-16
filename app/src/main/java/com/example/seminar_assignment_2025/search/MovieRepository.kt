package com.example.seminar_assignment_2025.search

interface MovieRepository {
    suspend fun searchByTitle(query: String): List<Movie>
    suspend fun getMovieDetail(id: Int): MovieDetail
}



