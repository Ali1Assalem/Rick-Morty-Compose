package com.example.myapplication.screens

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.compose.SubcomposeAsyncImage
import com.androidfactory.network.models.domain.Character
import com.example.myapplication.repositories.CharacterRepository
import com.example.myapplication.LoadingState
import com.example.myapplication.components.CharacterDetailsNamePlateComponent
import com.example.myapplication.components.DataPoint
import com.example.myapplication.components.DataPointComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CharacteDetailsViewModel @Inject constructor(private val characterRepository: CharacterRepository) : ViewModel(){
    private val _internalStorageFlow = MutableStateFlow<CharacterDetailsViewState>(
        value = CharacterDetailsViewState.Loading
    )

    val stateFlow = _internalStorageFlow.asStateFlow()

    fun fetchCharacter(characterId:Int) = viewModelScope.launch {
        characterRepository.fetchCharacter(characterId).onSuccess {character->
            val dataPoints =  buildList {
                character.let{ character ->
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

            _internalStorageFlow.update {
                return@update CharacterDetailsViewState.Success(character,dataPoints)
            }
        }.onFailure {exception ->
            _internalStorageFlow.update {
                return@update CharacterDetailsViewState.Error(exception.message ?:"Unknown error occured")
            }
        }
    }
}

sealed interface CharacterDetailsViewState{
    object Loading:CharacterDetailsViewState
    data class Error(val message:String):CharacterDetailsViewState
    data class Success(
        val character: Character,
        val characterDataPoints: List<DataPoint>
    ):CharacterDetailsViewState
}


@Composable
fun CharacteDetailsScreen (viewModel: CharacteDetailsViewModel = hiltViewModel(),
                           characterId:Int,
                           onEpisodeClicked:(Int)->Unit){

    LaunchedEffect(key1 = Unit, block = {
        viewModel.fetchCharacter(characterId)
    })

    val state by viewModel.stateFlow.collectAsState()


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(all = 16.dp)) {

        when(val viewState = state){
            CharacterDetailsViewState.Loading -> item { LoadingState() }
            is CharacterDetailsViewState.Error -> TODO()
            is CharacterDetailsViewState.Success -> {
                val character = viewState.character
                val characterDataPoint = viewState.characterDataPoints

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

    }

}