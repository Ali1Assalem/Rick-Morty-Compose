package com.example.myapplication.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidfactory.network.models.domain.Character
import com.androidfactory.simplerick.components.common.CharacterImage
import com.androidfactory.simplerick.components.common.CharacterNameComponent
import com.androidfactory.simplerick.components.episode.EpisodeRowComponent
import com.example.myapplication.LoadingState
import com.example.myapplication.components.DataPoint
import com.example.myapplication.components.DataPointComponent
import com.example.myapplication.components.SeasonHeader
import com.example.mylibrary.KtorClient
import com.example.mylibrary.domain.Episode
import kotlinx.coroutines.launch

@Composable
fun CharacterEpisodesScreen(characterId:Int,ktorClient: KtorClient){

    var characterState by remember { mutableStateOf<Character?>(null) }
    var episodesState by remember { mutableStateOf<List<Episode>>(emptyList()) }

    LaunchedEffect(Unit, block = {
        ktorClient.getCharachetrs(characterId).onSuccess { character ->
            characterState = character
            launch {
                ktorClient.getEpisodes(character.episodeIds).onSuccess { episode->
                    episodesState =episode
                }.onFailure {  }
            }
        }.onFailure {}
    })

    characterState?.let { character ->
        MainScreen(character,episodesState)
    }?: LoadingState()

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainScreen(character: Character,episodes: List<Episode>){
    val episodeBySeasonMap = episodes.groupBy { it.seasonNumber }

    LazyColumn(Modifier.padding(all = 10.dp)) {
        item { CharacterNameComponent(character.name) }
        item { Spacer(Modifier.height(10.dp)) }
        item { LazyRow {
            episodeBySeasonMap.forEach{ it->
                val title = "Season ${it.key}"
                val desc = "${it.value.size} Ep"
                item {
                    DataPointComponent(DataPoint(title,desc))
                    Spacer(Modifier.width(32.dp))
                }
            }
          }
        }
        item { Spacer(Modifier.height(10.dp)) }
        item { CharacterImage(character.imageUrl) }
        item { Spacer(Modifier.height(10.dp)) }
        episodeBySeasonMap.forEach{ mapEntry ->
            stickyHeader { SeasonHeader(seasonNumber = mapEntry.key) }
            item { Spacer(modifier = Modifier.height(15.dp)) }
            items(mapEntry.value) {episode ->
                EpisodeRowComponent(episode)
            }
        }
        item { Spacer(Modifier.height(10.dp)) }
    }
}