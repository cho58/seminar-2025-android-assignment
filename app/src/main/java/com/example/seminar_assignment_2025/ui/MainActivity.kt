package com.example.seminar_assignment_2025.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.seminar_assignment_2025.game.Game2048Screen
import com.example.seminar_assignment_2025.search.presentation.SearchScreen
import com.example.seminar_assignment_2025.search.presentation.SearchViewModel
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.seminar_assignment_2025.R
import com.example.seminar_assignment_2025.search.presentation.MovieDetailScreen
import dagger.hilt.android.AndroidEntryPoint

sealed class Tab(val title: String) {
    data object Home : Tab("Home")
    data object Search : Tab("Search")
    data object AppTab : Tab("App")
    data object Game : Tab("Game")
    data object Profile : Tab("Profile")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: SearchViewModel by viewModels()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            var currentIndex by remember { mutableStateOf(0) }
            var previousIndex by remember { mutableStateOf(0) }

            NavHost(navController = navController,
                    startDestination = "main"
            ){

                // 3. "메인 화면" (기존의 탭 + Scaffold)
                composable("main") {
                    MainScreen(
                        viewModel = vm,
                        navController = navController,

                        currentIndex = currentIndex,
                        previousIndex = previousIndex,
                        onTabSelected = { newIndex ->
                            previousIndex = currentIndex
                            currentIndex = newIndex
                        }
                    )
                }

                // 4. "상세 화면" (신규)
                composable("detail") {
                    val movieDetail by vm.selectedMovieDetail.collectAsState()

                    MovieDetailScreen(
                        movie = movieDetail,
                        onBackClicked = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: SearchViewModel,
    navController: NavController,
   currentIndex: Int,
   previousIndex: Int,
   onTabSelected: (Int) -> Unit
) {
    val tabs = listOf(Tab.Home, Tab.Search, Tab.AppTab, Tab.Game, Tab.Profile)

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = currentIndex == index,
                        onClick = {
                            onTabSelected(index)
                        },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    Tab.Home -> Icons.Default.Home
                                    Tab.Search -> Icons.Default.Search
                                    Tab.AppTab -> Icons.Default.Apps
                                    Tab.Game -> Icons.Default.Gamepad
                                    Tab.Profile -> Icons.Default.AccountCircle
                                },
                                contentDescription = tab.title
                            )
                        },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        // 애니메이션 (이전 인덱스 vs 현재 인덱스 비교로 방향 결정)
        val isForward = currentIndex > previousIndex
        val duration = 220

        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = {
                val slideIn = slideInHorizontally(
                    animationSpec = tween(duration),
                    initialOffsetX = { if (isForward) it else -it }
                ) + fadeIn(tween(duration))
                val slideOut = slideOutHorizontally(
                    animationSpec = tween(duration),
                    targetOffsetX = { if (isForward) -it else it }
                ) + fadeOut(tween(duration))
                slideIn togetherWith slideOut using SizeTransform(clip = false)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { idx ->
            when (tabs[idx]) {
                Tab.Home    -> CenterText("Home")
                Tab.Search  -> SearchScreen(
                    viewModel = viewModel,
                    navController = navController
                )
                Tab.AppTab  -> CenterText("App")
                Tab.Game    -> Game2048Screen()
                Tab.Profile -> ProfileXmlHost()
            }
        }
    }
}

@Composable
private fun CenterText(text: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}

@Composable
private fun ProfileXmlHost() {
    val context = LocalContext.current
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            LayoutInflater.from(ctx).inflate(R.layout.activity_profile, null, false)
        }
    )
}
