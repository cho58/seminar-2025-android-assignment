package com.example.seminar_assignment_2025.ui.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.seminar_assignment_2025.data.Movie
import com.example.seminar_assignment_2025.ui.search.SearchViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(movie: Movie, navController: NavController, viewModel: SearchViewModel) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(movie.title, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(width = 22.dp, height = 25.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                ),
                modifier = Modifier.height(35.dp)
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            item {
                Box(modifier = Modifier.height(301.dp)) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/original${movie.backdropPath}",
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f))
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Card(
                            modifier = Modifier.size(164.dp, 246.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            AsyncImage(
                                model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                                contentDescription = movie.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = movie.title,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            StarRating(rating = movie.voteAverage)
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        movie.genreIds.forEach { genreId ->
                            Chip(label = viewModel.getGenreName(genreId))
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Summary", style = MaterialTheme.typography.titleLarge, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(movie.overview, style = MaterialTheme.typography.bodyLarge, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Popularity", style = MaterialTheme.typography.titleLarge, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(movie.popularity.toString(), style = MaterialTheme.typography.bodyLarge, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun Chip(label: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 12.sp
        )
    }
}

@Composable
fun StarRating(rating: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = String.format("%.1f", rating),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )
        Spacer(Modifier.width(8.dp))
        Row {
            val starCount = (rating / 2).roundToInt().coerceIn(0, 5)
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (index < starCount) Color(0xFFFFC107) else Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
