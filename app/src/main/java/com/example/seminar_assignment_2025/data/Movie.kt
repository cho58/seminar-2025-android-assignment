package com.example.seminar_assignment_2025.data

import kotlinx.serialization.Serializable
import java.io.Serializable as JavaSerializable // Create an alias

@Serializable // for kotlinx.serialization (JSON)
data class Movie(
    val id: Int,
    val title: String,
    val releaseDate: String,
    val voteAverage: Double,
    val posterPath: String,
    val genreIds: List<Int>,
    val backdropPath: String,
    val overview: String,
    val popularity: Double
) : JavaSerializable