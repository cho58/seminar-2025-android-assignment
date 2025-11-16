package com.example.seminar_assignment_2025.search.domain

import kotlinx.serialization.Serializable

@Serializable
data class MovieDetail(
    val adult: Boolean = false,
    val backdrop_path: String? = null,
    val belongs_to_collection: String? = null,
    val budget: Int = 0,
    val genres: List<Genre> = emptyList(),
    val homepage: String? = null,
    val id: Int = 0,
    val imdb_id: String? = null,
    val original_language: String = "",
    val original_title: String = "",
    val overview: String = "",
    val popularity: Double = 0.0,
    val poster_path: String? = null,
    val release_date: String = "",
    val revenue: Int = 0,
    val runtime: Int? = null,
    val status: String = "",
    val tagline: String? = null,
    val title: String,
    val video: Boolean = false,
    val vote_average: Double = 0.0,
    val vote_count: Int = 0,
)

@Serializable
data class Genre(
    val id: Int,
    val name: String,
)
