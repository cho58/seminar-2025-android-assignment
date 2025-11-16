package com.example.seminar_assignment_2025.search.data.remote

import com.example.seminar_assignment_2025.search.domain.ApiResponse
import com.example.seminar_assignment_2025.search.domain.Movie
import com.example.seminar_assignment_2025.search.domain.MovieDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    /**
     * 영화를 검색합니다. (GET 요청)
     * @param query 검색할 키워드
     * @param language 응답 언어 (기본값 ko-KR)
     * @return ApiResponse<Movie> 형태의 응답
     */
    @GET("search/movie")
    suspend fun searchMovie(
        @Query("query") query: String,
        @Query("language") language: String = "en-US",
        @Query("include_adult") includeAdult: Boolean = false,
    ): ApiResponse<Movie>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US",
    ): MovieDetail

}