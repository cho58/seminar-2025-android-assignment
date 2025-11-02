package com.example.seminar_assignment_2025.ui.search

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.seminar_assignment_2025.R
import com.example.seminar_assignment_2025.data.Movie
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import kotlin.math.roundToInt

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val searchHistory by viewModel.searchHistory.collectAsState()
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val searchResults by viewModel.searchResults.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            value = searchText,
            onValueChange = { searchText = it },
            onSearch = {
                if (searchText.text.isNotBlank()) {
                    viewModel.addSearchTerm(searchText.text)
                    viewModel.searchByTitle(searchText.text)
                }
            }
        )
        Divider(color = Color.LightGray, thickness = 1.dp)

        if (searchText.text.isEmpty()) {
            if (searchHistory.isNotEmpty()) {
                SearchHistoryList(
                    history = searchHistory,
                    onHistoryClick = { history -> searchText = TextFieldValue(history) },
                    onDeleteClick = { history -> viewModel.removeSearchTerm(history) },
                    onClearAll = { viewModel.clearSearchHistory() }
                )
            } else {
                EmptySearchView()
            }
        } else {
            SearchResultList(movies = searchResults, viewModel = viewModel) { movie ->
                val movieJson = Json.encodeToString(movie)
                val encodedMovieJson = URLEncoder.encode(movieJson, "UTF-8")
                navController.navigate("movieDetail/$encodedMovieJson")
            }
        }
    }
}

@Composable
fun SearchBar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onSearch: () -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("영화 검색...") },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.clickable { onSearch() }
            )
        },
        trailingIcon = {
            if (value.text.isNotEmpty()) {
                IconButton(onClick = { onValueChange(TextFieldValue("")) }) {
                    Icon(Icons.Default.Close, contentDescription = "clear text")
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5)
        )
    )
}

@Composable
fun SearchHistoryList(
    history: List<String>,
    onHistoryClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("최근 검색어")
            Text("전체 삭제", modifier = Modifier.clickable { onClearAll() })
        }
        LazyColumn {
            items(history) { term ->
                SearchHistoryItem(term, onHistoryClick, onDeleteClick)
            }
        }
    }
}

@Composable
fun SearchHistoryItem(term: String, onHistoryClick: (String) -> Unit, onDeleteClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onHistoryClick(term) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(id = R.drawable.ic_clock),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = term,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        )
        IconButton(onClick = { onDeleteClick(term) }) {
            Icon(Icons.Default.Close, contentDescription = "delete")
        }
    }
}

@Composable
fun SearchResultList(
    movies: List<Movie>,
    viewModel: SearchViewModel,
    onMovieClick: (Movie) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("검색 결과 ${movies.size}개")
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(movies) { movie ->
                MovieItem(movie = movie, viewModel = viewModel, onClick = { onMovieClick(movie) })
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, viewModel: SearchViewModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                contentDescription = movie.title,
                modifier = Modifier.size(100.dp, 150.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier
                .padding(start = 16.dp)
                .height(150.dp)) { 
                Text(movie.title)
                Spacer(modifier = Modifier.height(4.dp))
                Text(movie.releaseDate.substring(0, 4), color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(movie.genreIds.joinToString { viewModel.getGenreName(it) }, color = Color.Gray)

                Spacer(Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFFFC107)
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(((movie.voteAverage * 10).roundToInt() / 10.0).toString())
                }
            }
        }
    }
}

@Composable
fun EmptySearchView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.ic_movie),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .padding(bottom = 16.dp),
                tint = Color.LightGray
            )
            Text("영화를 검색해보세요")
            Text(
                text = "상단 검색 바를 통해\n원하는 영화를 찾아보세요",
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
    }
}
