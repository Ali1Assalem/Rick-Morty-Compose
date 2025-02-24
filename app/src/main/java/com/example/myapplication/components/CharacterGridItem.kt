package com.example.myapplication.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfactory.network.models.domain.Character
import com.androidfactory.simplerick.components.common.CharacterImage


@Composable
fun CharacterGridItem(modifier: Modifier,character: Character,onClick:()->Unit){
    Column(modifier = Modifier
        .border(
        width = 1.dp,
        brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Blue)),
        shape = RoundedCornerShape(12.dp))
        .clip( RoundedCornerShape(12.dp))
        .clickable { onClick() }
    ){
        CharacterImage(character.imageUrl)
        Text(
            text = character.name,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 26.sp,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}