package com.example.myapplication.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.androidfactory.network.models.domain.CharacterStatus

@Composable
fun CharacterDetailsNamePlateComponent(characterName:String,characterStatus: CharacterStatus) {
    Column(Modifier.fillMaxWidth()
        ) {
        CharacterStatusComponent(characterStatus)
        Text(text = characterName, fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
    }
}

@Preview
@Composable
fun CharacterDetailsNamePlateComponentPreview(){
    CharacterDetailsNamePlateComponent("Rick And Morty", CharacterStatus.Dead)
}