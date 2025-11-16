package com.example.seminar_assignment_2025.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.seminar_assignment_2025.R

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

                            // Icon의 기본 크기를 사용하거나 Modifier로 지정
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
                    voteAverage = movie.vote_average
                )
            }


            // 2. 장르 (칩, 40% 회색)
            item {
                val genres = movie.genres.map { it.name }
                FlowRow(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
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
                    content = movie.budget.toString()
                )
            }

            item {
                DetailSection(
                    title = "Revenue",
                    content = movie.revenue.toString()
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
    voteAverage: Double
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(301.dp)
            .graphicsLayer(clip = false)
    ) {

        AsyncImage(
            model = "https://image.tmdb.org/t/p/w780$backdropPath",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 300f, // 그라데이션 시작 위치
                        endY = Float.POSITIVE_INFINITY
                    )
                )
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
                    .height(246.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 제목 + 평점
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
                // 평점
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = String.format("%.1f", voteAverage),
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