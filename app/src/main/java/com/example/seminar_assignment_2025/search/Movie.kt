package com.example.seminar_assignment_2025.search

import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val adult: Boolean = false,
    val backdrop_path: String? = null,
    val genre_ids: List<Int> = emptyList(),
    val id: Int,
    val original_language: String = "",
    val original_title: String = "",
    val overview: String = "",
    val popularity: Double = 0.0,
    val poster_path: String? = null,
    val release_date: String = "",
    val title: String,
    val video: Boolean = false,
    val vote_average: Double,
    val vote_count: Int
)


