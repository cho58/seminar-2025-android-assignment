package com.example.seminar_assignment_2025.search

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json


interface MovieRepository {
    suspend fun searchByTitle(query: String): List<Movie>
}

class MovieRepositoryImpl : MovieRepository {

    // Json 파서 (설정 포함)
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true // 1. 'null'을 기본값으로 강제 변환 (JSON 크래시 2차 방지)
    }

    // 2. 파싱된 데이터를 캐시할 변수
    private var allMovies: List<Movie>? = null

    /**
     * 비동기로 영화 목록을 로드하고 캐시합니다. (ANR 해결)
     */
    private suspend fun getAllMovies(): List<Movie> {
        // 3. 캐시된 데이터가 있으면 즉시 반환
        if (allMovies != null) {
            return allMovies!!
        }

        // 4. (핵심) I/O 스레드에서 JSON 파싱 실행 (메인 스레드 차단 방지)
        return withContext(Dispatchers.IO) {
            val movies = json.decodeFromString<List<Movie>>(movieRawData)
            allMovies = movies // 5. 파싱 결과를 캐시에 저장
            movies
        }
    }

    /**
     * 스펙에 명시된 검색 함수
     */
    override suspend fun searchByTitle(query: String): List<Movie> {
        // 6. Default 스레드에서 필터링 실행
        return withContext(Dispatchers.Default) {
            getAllMovies().filter { movie ->
                movie.title.contains(query, ignoreCase = true)
            }
        }
    }
}

