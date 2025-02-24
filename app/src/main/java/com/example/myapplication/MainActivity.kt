package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.screens.CharacteDetailsScreen
import com.example.myapplication.screens.CharacteDetailsViewModel
import com.example.myapplication.screens.CharacterEpisodesScreen
import com.example.myapplication.screens.HomeScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.mylibrary.KtorClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
     lateinit var ktorClient : KtorClient


       override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController, startDestination = "home_screen"){
                        composable("home_screen"){
                            HomeScreen( onCharacterSelected = { characterId->
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
                    }
                }
            }
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

