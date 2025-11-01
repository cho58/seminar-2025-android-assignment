package com.example.seminar_assignment_2025.ui.search

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seminar_assignment_2025.R
import com.example.seminar_assignment_2025.data.SearchHistoryRepository

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(SearchHistoryRepository(LocalContext.current.applicationContext as Application))
    )
) {
    val searchHistory by viewModel.searchHistory.collectAsState()
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            value = searchText,
            onValueChange = { searchText = it },
            onSearch = { viewModel.addSearchTerm(it) }
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
            }
        } else {
            // Show search results here if needed
        }

        if (searchText.text.isEmpty() && searchHistory.isEmpty()) {
            EmptySearchView()
        }
    }
}

@Composable
fun SearchBar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,onSearch: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("영화 검색...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (value.text.isNotEmpty()) {
                IconButton(onClick = { onValueChange(TextFieldValue("")) }) {
                    Icon(Icons.Default.Close, contentDescription = "clear text")
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            // Corrected parameters for background color
            focusedContainerColor = Color(0xFFF5F5F5),
            unfocusedContainerColor = Color(0xFFF5F5F5)
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
            items(history) {
                SearchHistoryItem(it, onHistoryClick, onDeleteClick)
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
        Icon(painterResource(id = R.drawable.ic_clock), contentDescription = null)
        Text(term, modifier = Modifier
            .weight(1f)
            .padding(horizontal = 8.dp))
        IconButton(onClick = { onDeleteClick(term) }) {
            Icon(Icons.Default.Close, contentDescription = "delete")
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
                    .padding(bottom = 16.dp)
            )
            Text("영화를 검색해보세요")
            Text(
                text = "상단 검색 바를 통해\n원하는 영화를 찾아보세요",
                textAlign = TextAlign.Center
            )
        }
    }
}