package com.example.seminar_assignment_2025.search.data.repository

import com.example.seminar_assignment_2025.search.data.remote.ApiService
import com.example.seminar_assignment_2025.search.domain.Movie
import com.example.seminar_assignment_2025.search.domain.MovieDetail
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : MovieRepository {

    override suspend fun searchByTitle(query: String): List<Movie> {
        if (query.isBlank()) return emptyList()

        return try {
            val response = apiService.searchMovie(query = query)
            response.results
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // 나중에 상세 API까지 쓸 거면 여기에 getMovieDetail(...) 도 추가 가능
    override suspend fun getMovieDetail(id: Int): MovieDetail {
        return apiService.getMovieDetail(movieId = id)
    }
}