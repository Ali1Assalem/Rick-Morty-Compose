package com.example.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.androidfactory.network.models.domain.Character
import com.example.myapplication.LoadingState
import com.example.myapplication.components.CharacterDetailsNamePlateComponent
import com.example.myapplication.components.DataPoint
import com.example.myapplication.components.DataPointComponent
import com.example.mylibrary.KtorClient

@Composable
fun CharacteDetailsScreen (ktorClient: KtorClient,characterId:Int,onEpisodeClicked:(Int)->Unit){

    var character by remember {
        mutableStateOf<Character?>(null)
    }

    val characterDataPoint:List<DataPoint> by remember {
        derivedStateOf {
            buildList {
                character?.let{ character ->
                    add(DataPoint("Last Unknown Location",character.location.name))
                    add(DataPoint("Species",character.species))
                    add(DataPoint("Gender",character.gender.displayName))
                    character.type.takeIf { it.isNotEmpty() }?.let { type->
                        add(DataPoint("Type",type))
                    }
                    add(DataPoint("Origin",character.origin.name))
                    add(DataPoint("Episode Count",character.episodeIds.size.toString()))
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit, block = {
        //delay(3000)
        ktorClient.getCharachetrs(characterId).onSuccess {
            character = it
        }.onFailure {
            //TODO
        }
    })

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(all = 16.dp)) {

        if(character == null){
            item { LoadingState() }
            return@LazyColumn
        }

        item { character?.let { CharacterDetailsNamePlateComponent(it.name,it.status) } }

        item { Spacer(Modifier.height(8.dp)) }

        //Image
        item {
            SubcomposeAsyncImage(
                model = character!!.imageUrl,
                contentDescription = "Character Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Gray),
                loading = { LoadingState() }
            )
        }

        item { Spacer(Modifier.height(8.dp)) }

        items(characterDataPoint){
            Spacer(Modifier.height(32.dp))
            DataPointComponent(it)
        }

        item { Spacer(Modifier.height(8.dp)) }

        // Button
        item {
            Text(
                text = "View all episodes",
                color = Color.Black,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .border(
                        width = 1.dp,
                        color = Color.Blue,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onEpisodeClicked(characterId)
                    }
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            )
        }

    }

}