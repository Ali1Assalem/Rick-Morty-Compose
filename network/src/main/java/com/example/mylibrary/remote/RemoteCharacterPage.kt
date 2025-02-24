package com.example.mylibrary.remote

import com.androidfactory.network.models.remote.RemoteCharacter
import com.androidfactory.network.models.remote.toDomainCharacter
import com.example.mylibrary.domain.CharacterPage
import kotlinx.serialization.Serializable

@Serializable
data class RemoteCharacterPage(
    val info:Info,
    val results:List<RemoteCharacter>
) {
    @Serializable
    data class Info(
        val count:Int,
        val pages:Int,
        val next:String?,
        val prev:String?,
    )
}

fun RemoteCharacterPage.toDomainCharacterPage():CharacterPage{
    return CharacterPage(info = CharacterPage.Info(
        count = info.count,
            pages = info.pages,
            next = info.next,
            prev = info.prev
        ),
        characters = results.map { it.toDomainCharacter() }
    )
}