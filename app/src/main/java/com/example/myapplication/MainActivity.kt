package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.CharacteDetailsScreen
import com.example.myapplication.ui.CharacterEpisodesScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.mylibrary.KtorClient


class MainActivity : ComponentActivity() {

    private val ktorClient = KtorClient()


       override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController, startDestination = "characterDetails"){
                        composable("characterDetails"){
                            CharacteDetailsScreen(ktorClient,1){
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

