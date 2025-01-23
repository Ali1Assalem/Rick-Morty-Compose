package com.example.myapplication.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.androidfactory.network.models.domain.CharacterStatus
import com.androidfactory.simplerick.components.common.CharacterNameComponent

@Composable
fun CharacterDetailsNamePlateComponent(characterName:String,characterStatus: CharacterStatus) {
    Column(Modifier.fillMaxWidth()
        ) {
        CharacterStatusComponent(characterStatus)
        CharacterNameComponent(characterName)
    }
}


@Preview
@Composable
fun CharacterDetailsNamePlateComponentPreview(){
    CharacterDetailsNamePlateComponent("Rick And Morty", CharacterStatus.Dead)
}