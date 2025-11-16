package com.example.seminar_assignment_2025.search.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.seminar_assignment_2025.R
import com.example.seminar_assignment_2025.search.domain.MovieDetail
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MovieDetailScreen(
    movie: MovieDetail?,
    onBackClicked: () -> Unit // 뒤로가기 람다
) {
    Scaffold(
        topBar = {
            // 1. 상단 툴바
            CenterAlignedTopAppBar (
                title = { 
                    Text(
                        text = movie?.title ?: "",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    ) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = "아이콘 설명",

                            modifier = Modifier.size(15.88.dp),

                            tint = Color.Black
                        )
                    }
                },
                modifier = Modifier.height(56.dp)

            )
        }
    ) { innerPadding ->
        if (movie == null) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        // 2. 스크롤 가능한 본문
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 1. 상단 이미지 (Backdrop + Poster 오버레이)
            item {
                ImageHeader(
                    backdropPath = movie.backdrop_path,
                    posterPath = movie.poster_path,
                    title = movie.title,
                    voteAverage = movie.vote_average,
                    runtimeMinutes = movie.runtime,
                    releaseDate = movie.release_date,
                    isAdult = movie.adult
                )
            }


            // 2. 장르
            item {
                val genres = movie.genres.map { it.name }
                Row(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    genres.forEach { genreName ->
                        GenreChip(text = genreName)
                    }
                }
            }


            // 3. Summary
            item {
                DetailSection(
                    title = "Summary",
                    content = movie.overview
                )
            }

            item {
                DetailSection(
                    title = "Original Title",
                    content = movie.original_title
                )
            }

            item {
                DetailSection(
                    title = "Status",
                    content = movie.status
                )
            }

            item {
                DetailSection(
                    title = "Budget",
                    content = formatCurrency(movie.budget)
                )
            }

            item {
                DetailSection(
                    title = "Revenue",
                    content = formatCurrency(movie.revenue)
                )
            }
        }
    }
}

@Composable
private fun ImageHeader(
    backdropPath: String?,
    posterPath: String?,
    title: String,
    voteAverage: Double,
    runtimeMinutes: Int?,
    releaseDate: String,
    isAdult: Boolean
) {
    val runtimeText = formatRuntime(runtimeMinutes)
    val yearText = if (releaseDate.length >= 4) releaseDate.substring(0, 4) else ""
    val ageLabel = if (isAdult) "R18+" else "All Ages"
    val ageColor = if (isAdult) Color(0xFFFF3B30) else Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(301.dp)
            .graphicsLayer(clip = false)
    ) {
        // 배경(backdrop)
        AsyncImage(
            model = "https://image.tmdb.org/t/p/original$backdropPath",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 40% 블랙 오버레이
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .offset(12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // 포스터
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500$posterPath",
                contentDescription = "Poster",
                modifier = Modifier
                    .width(164.dp)
                    .height(246.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 제목 + 평점 + 러닝타임/연도/연령 라벨
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 평점 + 별
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = String.format(Locale.US, "%.1f", voteAverage),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    val rating = (voteAverage / 2.0).toFloat()

                    Row {
                        val starColor = Color(0xFFFFC107)
                        val starSize = Modifier.size(11.dp)

                        repeat(5) { index ->
                            when {
                                rating >= index + 1 -> {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = "Full Star",
                                        tint = starColor,
                                        modifier = starSize
                                    )
                                }
                                rating >= index + 0.5 -> {
                                    Icon(
                                        imageVector = Icons.Filled.StarHalf,
                                        contentDescription = "Half Star",
                                        tint = starColor,
                                        modifier = starSize
                                    )
                                }
                                else -> {
                                    Icon(
                                        imageVector = Icons.Outlined.Star,
                                        contentDescription = "Empty Star",
                                        tint = starColor.copy(alpha = 0.5f),
                                        modifier = starSize
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 1h 40m · 2003 · R18+
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (runtimeText.isNotEmpty()) {
                        Text(
                            text = runtimeText,
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                    if (yearText.isNotEmpty()) {
                        if (runtimeText.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = yearText,
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = ageLabel,
                        color = ageColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun GenreChip(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

/**
 * Summary, Popularity 등 텍스트 섹션
 */
@Composable
private fun DetailSection(title: String, content: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp
        )
    }
}

private fun formatRuntime(runtimeMinutes: Int?): String {
    if (runtimeMinutes == null || runtimeMinutes <= 0) return ""
    val hours = runtimeMinutes / 60
    val minutes = runtimeMinutes % 60
    return if (hours > 0) {
        if (minutes > 0) "${hours}h ${minutes}m" else "${hours}h"
    } else {
        "${minutes}m"
    }
}

private fun formatCurrency(amount: Int): String {
    if (amount <= 0) return "-"
    val formatted = String.format(Locale.US, "%,d", amount)
    return "$$formatted"
}