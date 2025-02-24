package com.example.mylibrary.domain

import com.androidfactory.network.models.domain.Character

data class CharacterPage(
    val info:Info,
    val characters:List<Character>
){
    data class Info(
        val count:Int,
        val pages:Int,
        val next:String?,
        val prev:String?,
    )
}
