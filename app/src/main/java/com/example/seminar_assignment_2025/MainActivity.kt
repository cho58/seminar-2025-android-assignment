package com.example.seminar_assignment_2025

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.seminar_assignment_2025.ui.GameScreen
import com.example.seminar_assignment_2025.ui.search.SearchScreen
import com.example.seminar_assignment_2025.ui.detail.MovieDetailScreen
import com.example.seminar_assignment_2025.data.Movie
import com.example.seminar_assignment_2025.ui.search.SearchViewModel
import com.example.seminar_assignment_2025.ui.search.SearchViewModelFactory
import kotlinx.serialization.json.Json
import java.net.URLDecoder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val searchViewModel: SearchViewModel = viewModel(factory = SearchViewModelFactory(application))

            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                NavHost(navController, startDestination = NavItem.Home.route, Modifier.padding(innerPadding)) {
                    composable(NavItem.Home.route) { HomeScreen(Modifier.padding(innerPadding)) }
                    composable(NavItem.Search.route) { SearchScreen(navController = navController, viewModel = searchViewModel) }
                    composable(NavItem.App.route) { AppScreen(Modifier.padding(innerPadding)) }
                    composable(NavItem.Game.route) { GameScreen() }
                    composable(NavItem.Profile.route) { ProfileScreen(Modifier.padding(innerPadding)) }
                    composable(
                        route = "movieDetail/{movieJson}",
                        arguments = listOf(navArgument("movieJson") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val movieJson = backStackEntry.arguments?.getString("movieJson")?.let {
                            URLDecoder.decode(it, "UTF-8")
                        }
                        if (movieJson != null) {
                            val movie = Json.decodeFromString<Movie>(movieJson)
                            MovieDetailScreen(movie = movie, navController = navController, viewModel = searchViewModel)
                        }
                    }
                }
            }
        }
    }
}


enum class NavItem(val route: String, val label: String, val icon: ImageVector) {
    Home("home", "Home", Icons.Filled.Home),
    Search("search", "Search", Icons.Filled.Search),
    App("app", "App", Icons.Filled.ShoppingCart),
    Game("game", "Game", Icons.Filled.PlayArrow),
    Profile("profile", "Profile", Icons.Filled.Person)
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    var selectedMenu by remember { mutableStateOf(NavItem.Home.route) }

    NavigationBar {
        NavItem.values().forEach { item ->
            NavigationBarItem(
                selected = selectedMenu == item.route,
                onClick = {
                    selectedMenu = item.route
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

@Composable fun HomeScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Home",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable fun AppScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "App",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable fun ProfileScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Profile",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}