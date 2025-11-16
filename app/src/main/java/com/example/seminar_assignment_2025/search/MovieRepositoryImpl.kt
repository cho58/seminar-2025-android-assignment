package com.example.seminar_assignment_2025.search

class MovieRepositoryImpl(
    private val apiService: ApiService = ApiProvider.api
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

    override suspend fun getMovieDetail(id: Int): MovieDetail {
        return apiService.getMovieDetail(movieId = id)
    }
}
