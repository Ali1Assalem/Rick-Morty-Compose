package com.example.myapplication.repositories

import com.androidfactory.network.models.domain.Character
import com.example.mylibrary.ApiOperation
import com.example.mylibrary.KtorClient
import com.example.mylibrary.domain.CharacterPage
import javax.inject.Inject

class CharacterRepository @Inject constructor(private val ktorClient: KtorClient){

    suspend fun fetchCharacterPage(page:Int):ApiOperation<CharacterPage>{
        return ktorClient.getCharactersByPage(page)
    }

    suspend fun fetchCharacter(charachterId:Int): ApiOperation<Character> {
        return ktorClient.getCharachetrs(charachterId)
    }
}