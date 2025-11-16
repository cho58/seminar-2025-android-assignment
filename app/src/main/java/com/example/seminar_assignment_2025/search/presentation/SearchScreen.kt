package com.example.seminar_assignment_2025.search.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.example.seminar_assignment_2025.R
import coil.compose.AsyncImage
import androidx.navigation.NavController
import com.example.seminar_assignment_2025.search.domain.GenreMapper
import com.example.seminar_assignment_2025.search.domain.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: SearchViewModel, navController: NavController) {
    val query by viewModel.searchQuery.collectAsState()
    val recentList by viewModel.recentSearches.collectAsState()
    val searchResult by viewModel.searchResults.collectAsState()
    var isSearchFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .onFocusChanged { focusState ->
                    isSearchFocused = focusState.isFocused
                },
            placeholder = { Text("영화 검색...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = viewModel::onClearQuery) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear query")
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.onSearchClicked()
            }),
        )
        Spacer(modifier = Modifier.height(16.dp))

        when {
            // 1) 검색 결과가 있으면 → 결과 리스트
            searchResult.isNotEmpty() -> {
                Text(
                    text = "검색 결과 ${searchResult.size}개",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(searchResult) { movie ->
                        MovieItem(
                            movie = movie,
                            onClick = {
                                // 3. ViewModel에 영화 선택 + 상세 페이지로 이동
                                viewModel.selectMovie(movie)
                                navController.navigate("detail")
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            // 2) 검색창이 비어 있고, 포커스가 있을 때 → 최근 검색어
            query.isBlank() && isSearchFocused -> {
                if (recentList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("최근 검색어가 없습니다.", color = Color.Gray)
                    }
                } else {
                    RecentSearchSection(
                        items = recentList,
                        onClickItem = { keyword ->
                            viewModel.recentSearchItemClicked(keyword)
                            focusManager.clearFocus()
                            isSearchFocused = false
                        },
                        onDeleteItem = { kw -> viewModel.deleteRecent(kw) },
                        onClearAll = { viewModel.clearRecent() }
                    )
                }
            }

            else -> {
                EmptyView(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
fun EmptyView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.movie_svgrepo_com),
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = Color(0xFFB0B0B0)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "영화를 검색해보세요",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "상단 검색 바를 통해 원하는 영화를 찾아보세요",
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}


@Composable
fun RecentSearchSection(
    items: List<String>,
    onClickItem: (String) -> Unit,
    onDeleteItem: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("최근 검색어", fontWeight = FontWeight.SemiBold)
            Text(
                "전체 삭제",
                color = Color(0xFF2166FF),
                modifier = Modifier.clickable { onClearAll() }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        items.forEach { keyword ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickItem(keyword) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.AccessTime, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(keyword, modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.Close,
                    contentDescription = "삭제",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onDeleteItem(keyword) }
                )
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onClick: () -> Unit) {
    // 스펙: 포스터 URL 조합
    val imageUrl = "https://image.tmdb.org/t/p/w500${movie.poster_path}"

    // 스펙: 연도 (release_date에서 앞 4자리)
    val year = movie.release_date.take(4)

    // 스펙: 장르 (ID 변환)
    val genres = GenreMapper.getGenreNames(movie.genre_ids)

    // 스펙: 평점 (소수점 첫째 자리)
    val rating = String.format("%.1f", movie.vote_average)

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. 포스터 (Coil 사용)
            AsyncImage(
                model = imageUrl,
                contentDescription = movie.title,
                modifier = Modifier
                    .width(100.dp)
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 2. 영화 정보
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(movie.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color=Color.Black
                    )
                    Text(
                        text = year,
                        fontSize = 14.sp,
                        color = Color(0xFF424242)
                    )
                    Text(
                        text = genres,
                        fontSize = 14.sp,
                        color = Color(0xFFD3D3D3)
                    )
                }

                // 3. 평점 (별 아이콘 + 숫자)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(rating,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

