package com.example.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfactory.network.models.domain.CharacterStatus
import okhttp3.internal.wait

@Composable
fun CharacterStatusComponent(characterStatus: CharacterStatus){
    Row (
        modifier = Modifier.width(IntrinsicSize.Max)
            .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
            .border(width = 2.dp, color = characterStatus.color, shape = RoundedCornerShape(12.dp))
            .padding(top=12.dp, start = 12.dp, bottom = 12.dp, end = 12.dp)
    ){
        Text(text = "Status: ", fontSize = 20.sp)
        Text(text = characterStatus.displayName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview
@Composable
fun CharacterStatusAlive(){
    CharacterStatusComponent(CharacterStatus.Alive)
}

@Preview
@Composable
fun CharacterStatusDead(){
    CharacterStatusComponent(CharacterStatus.Dead)
}

@Preview
@Composable
fun CharacterStatusUnknown(){
    CharacterStatusComponent(CharacterStatus.Unknown)
}