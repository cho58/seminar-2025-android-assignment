package com.example.seminar_assignment_2025.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.example.seminar_assignment_2025.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: SearchViewModel) {
    var query by remember { mutableStateOf("") }
    val recentList by viewModel.recentList.collectAsState()
    var isSearchFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        SearchBar(
            text = query,
            onTextChange = { viewModel.query.value = it },
            onSearch = {
                viewModel.addRecent(query)
                focusManager.clearFocus()
            },
            onClearText = { viewModel.query.value = "" },
            onFocusChange = { focused ->
                isSearchFocused = focused
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (query.isBlank() && isSearchFocused) {
            if (recentList.isEmpty()) {
                Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                ) {
                    Text("최근 검색어가 없습니다.", color = Color.Gray)
                }
            } else {
                RecentSearchSection(
                    items = recentList,
                    onClickItem = { keyword ->
                        viewModel.query.value = keyword
                        focusManager.clearFocus()
                        isSearchFocused = false
                    },
                    onDeleteItem = { keyword ->
                        viewModel.deleteRecent(keyword)
                    },
                    onClearAll = {
                        viewModel.clearAll()
                    }
                )
            }
        } else {
            Spacer(modifier = Modifier.height(32.dp))
            EmptyView(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            )
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
fun SearchBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClearText: () -> Unit,
    onFocusChange: (Boolean) -> Unit,   // ✅ 새로 추가
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = shape,
        color = Color(0xFFF5F5F5),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFF6D6D6D)
            )
            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { onSearch() }
                ),
                textStyle = LocalTextStyle.current.copy(
                    color = Color.Black,
                    fontSize = 14.sp
                ),
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { state ->
                        onFocusChange(state.isFocused)
                    },
                decorationBox = { inner ->
                    if (text.isEmpty()) {
                        Text("영화 검색...",
                            color = Color(0xFFB0B0B0),
                            fontSize = 14.sp
                        )
                    }
                    inner()
                }
            )

            if (text.isNotEmpty()) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "입력 지우기",
                    tint = Color(0xFF6D6D6D),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onClearText() }
                )
            }
        }
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


