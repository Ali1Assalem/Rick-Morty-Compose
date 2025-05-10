package com.example.myapplication.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfactory.network.models.domain.Character
import com.example.myapplication.LoadingState
import com.example.myapplication.components.CharacterGridItem
import com.example.myapplication.repositories.CharacterRepository
import com.example.mylibrary.domain.CharacterPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val characterRepository: CharacterRepository
) :ViewModel(){
    val _viewState = MutableStateFlow<HomeViewState>(HomeViewState.Loading)
    val viewState : StateFlow<HomeViewState> = _viewState.asStateFlow()

    val fetchedCharacterPages = mutableListOf<CharacterPage>()

    fun fetchInitialPage() = viewModelScope.launch(Dispatchers.IO) {
        if (fetchedCharacterPages.isNotEmpty()) return@launch
        val initialPage = characterRepository.fetchCharacterPage(1)
        initialPage.onSuccess { characterPage->
            fetchedCharacterPages.clear()
            fetchedCharacterPages.add(characterPage)
            _viewState.update {
                return@update HomeViewState.GridDisplay(characters = characterPage.characters)
            }

        }.onFailure {
            //TODO()
        }
    }

    fun fetchNextPage() = viewModelScope.launch(Dispatchers.IO){
        val nextPageIndex = fetchedCharacterPages.size + 1
        characterRepository.fetchCharacterPage(nextPageIndex).onSuccess {characterPage ->
            fetchedCharacterPages.add(characterPage)
            _viewState.update {currentState->
                val currentCharacters = (currentState as? HomeViewState.GridDisplay)?.characters?: emptyList()
                return@update HomeViewState.GridDisplay(characters = currentCharacters + characterPage.characters)
            }
        }.onFailure {  }
    }
}

sealed interface HomeViewState{
    object Loading : HomeViewState
    data class GridDisplay(
        val characters:List<Character> = emptyList()
    ): HomeViewState
}

@Composable
fun HomeScreen(onCharacterSelected:(Int) -> Unit,
               homeViewModel:HomeViewModel = hiltViewModel()){

    val viewState by homeViewModel.viewState.collectAsState()

    LaunchedEffect(key1 = Unit,block={homeViewModel.fetchInitialPage()})

    val scrollState = rememberLazyGridState()
    val fetchNextPage : Boolean by remember {
        derivedStateOf {
            val currentCharacterwsCount =
                (viewState as? HomeViewState.GridDisplay)?.characters?.size
                    ?: return@derivedStateOf false
            val lastDisplayedIndex = scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: return@derivedStateOf false
            return@derivedStateOf lastDisplayedIndex >= currentCharacterwsCount - 10
        }
    }

    LaunchedEffect(fetchNextPage, block = {
        if (fetchNextPage) homeViewModel.fetchNextPage()
    })

    when(val state = viewState){
        is HomeViewState.Loading -> {
            LoadingState()
        }
        is HomeViewState.GridDisplay ->{
            LazyVerticalGrid (
                state = scrollState,
                contentPadding = PaddingValues(all = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                columns = GridCells.Fixed(2),
                content = {
                    items(items = state.characters,key ={it.id}){
                        CharacterGridItem(modifier = Modifier, character = it) {
                            onCharacterSelected(it.id)
                        }
                    }
                }
            )
        }
    }

}