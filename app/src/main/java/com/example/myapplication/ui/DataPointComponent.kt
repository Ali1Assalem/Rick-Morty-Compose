package com.example.myapplication.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class DataPoint(
    val title:String,
    val description:String
    )

@Composable
fun DataPointComponent(dataPoint: DataPoint) {
    Column{
        Text(
            text = dataPoint.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Blue
        )
        Text(
            text = dataPoint.description,
            fontSize = 24.sp,
            color = Color.Black
        )
    }
}