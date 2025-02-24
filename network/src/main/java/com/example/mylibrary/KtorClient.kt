package com.example.mylibrary

import com.androidfactory.network.models.domain.Character
import com.androidfactory.network.models.remote.RemoteCharacter
import com.androidfactory.network.models.remote.RemoteEpisode
import com.androidfactory.network.models.remote.toDomainCharacter
import com.androidfactory.network.models.remote.toDomainEpisode
import com.example.mylibrary.domain.CharacterPage
import com.example.mylibrary.domain.Episode
import com.example.mylibrary.remote.RemoteCharacterPage
import com.example.mylibrary.remote.toDomainCharacterPage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class KtorClient {
    private val client = HttpClient(OkHttp){
        defaultRequest { url("https://rickandmortyapi.com/api/") }

        install(Logging){
            logger = Logger.SIMPLE
        }

        install(ContentNegotiation){
            json(Json{
                ignoreUnknownKeys = true
            })
        }
    }

    private var characterCache = mutableMapOf<Int,Character>()

    suspend fun getCharachetrs(id:Int):ApiOperation<Character>{
        characterCache[id]?.let {  return ApiOperation.Success(it)}
        return safeApiCall{
            client.get("character/$id")
                .body<RemoteCharacter>()
                .toDomainCharacter()
                .also { characterCache[id]=it }
        }
    }

    suspend fun getEpisode(episodesId:Int):ApiOperation<Episode>{
        return safeApiCall{
            client.get("episode/$episodesId")
                .body<RemoteEpisode>()
                .toDomainEpisode()
        }
    }

    suspend fun getEpisodes(episodesIds:List<Int>):ApiOperation<List<Episode>>{
        return if(episodesIds.size == 1){
            getEpisode(episodesIds[0]).mapSuccess { listOf(it) }
        }
        else{
            val idsCommaSeparated = episodesIds.joinToString(",")
            safeApiCall{
                client.get("episode/$idsCommaSeparated")
                    .body<List<RemoteEpisode>>()
                    .map { it.toDomainEpisode() }
            }
        }

    }

    suspend fun getCharactersByPage(pageNumber:Int):ApiOperation<CharacterPage>{
        return safeApiCall{
            client.get("character/?page=$pageNumber")
                .body<RemoteCharacterPage>().toDomainCharacterPage()
        }
    }

    private inline fun <T> safeApiCall(apiCall: () -> T): ApiOperation<T> {
        return try {
            ApiOperation.Success(data = apiCall())
        } catch (e: Exception) {
            ApiOperation.Failure(exception = e)
        }
    }

}

sealed interface ApiOperation<T> {
    data class Success<T>(val data: T) : ApiOperation<T>
    data class Failure<T>(val exception: Exception) : ApiOperation<T>

    fun <R> mapSuccess(transform: (T) -> R): ApiOperation<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Failure -> Failure(exception)
        }
    }

    suspend fun onSuccess(block: suspend (T) -> Unit): ApiOperation<T> {
        if (this is Success) block(data)
        return this
    }

    fun onFailure(block: (Exception) -> Unit): ApiOperation<T> {
        if (this is Failure) block(exception)
        return this
    }
}