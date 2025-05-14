package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.screens.AllEpisodesScreen
import com.example.myapplication.screens.CharacteDetailsScreen
import com.example.myapplication.screens.CharacteDetailsViewModel
import com.example.myapplication.screens.CharacterEpisodesScreen
import com.example.myapplication.screens.HomeScreen
import com.example.myapplication.screens.SearchScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.RickAction
import com.example.myapplication.ui.theme.RickPrimary
import com.example.mylibrary.KtorClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject


sealed class NavDestination(val title: String, val route: String, val icon: ImageVector) {
    object Home : NavDestination(title = "Home", route = "home_screen", icon = Icons.Filled.Home)
    object Episodes :
        NavDestination(title = "Episodes", route = "episodes", icon = Icons.Filled.PlayArrow)

    object Search : NavDestination(title = "Search", route = "search", icon = Icons.Filled.Search)
}


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var ktorClient : KtorClient


       override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val items = listOf(
                NavDestination.Home, NavDestination.Episodes, NavDestination.Search
            )
            var selectedIndex by remember { mutableIntStateOf(0) }

            MyApplicationTheme {
                Scaffold (
                    bottomBar = {
                        NavigationBar (
                            containerColor = RickPrimary
                        ) {
                            items.forEachIndexed { index, screen ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(imageVector = screen.icon, contentDescription = null)
                                    },
                                    label = { Text(screen.title) },
                                    selected = index == selectedIndex,
                                    onClick = {
                                        selectedIndex = index
                                        navController.navigate(screen.route) {
                                            // Pop up to the start destination of the graph to
                                            // avoid building up a large stack of destinations
                                            // on the back stack as users select items
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            // Avoid multiple copies of the same destination when
                                            // reselecting the same item
                                            launchSingleTop = true
                                            // Restore state when reselecting a previously selected item
                                            restoreState = true
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = RickAction,
                                        selectedTextColor = RickAction,
                                        indicatorColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                ){innerPadding ->
                    NavigationHost(
                        navController = navController,
                        ktorClient = ktorClient,
                        innerPadding = innerPadding
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationHost(
    navController: NavHostController,
    ktorClient: KtorClient,
    innerPadding:PaddingValues
) {
    NavHost(navController, startDestination = "home_screen", modifier = Modifier
        .background(color = RickPrimary)
        .padding(innerPadding)
    ){
        composable("home_screen"){
            HomeScreen( onCharacterSelected = { characterId ->
                navController.navigate("characterDetails/$characterId")
            })
        }
        composable(route = "characterDetails/{characterId}",
            arguments = listOf(navArgument("characterId"){type = NavType.IntType})
        ){backStackEntry->
            val characterId = backStackEntry.arguments?.getInt("characterId") ?:-1
            CharacteDetailsScreen(characterId = characterId){
                navController.navigate("characterEpisodes/$it")
            }
        }
        composable(
            route="characterEpisodes/{characterId}",
            arguments = listOf(navArgument("characterId"){type = NavType.IntType})
        ){ backStackEntry->
            val characterId = backStackEntry.arguments?.getInt("characterId") ?:-1
            CharacterEpisodesScreen(characterId,ktorClient)
        }
        composable(
            route="episodes",
        ){
            AllEpisodesScreen()
        }
        composable(
            route="search",
        ){
            SearchScreen()
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}