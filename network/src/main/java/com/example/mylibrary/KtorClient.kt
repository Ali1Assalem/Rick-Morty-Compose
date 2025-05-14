package com.example.mylibrary

import com.androidfactory.network.domain.EpisodePage
import com.androidfactory.network.models.domain.Character
import com.androidfactory.network.models.remote.RemoteCharacter
import com.androidfactory.network.models.remote.RemoteEpisode
import com.androidfactory.network.models.remote.RemoteEpisodePage
import com.androidfactory.network.models.remote.toDomainCharacter
import com.androidfactory.network.models.remote.toDomainEpisode
import com.androidfactory.network.models.remote.toDomainEpisodePage
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
    }//eeeeeeeeeeeeeeeeeeeeeeeeeeeeeee

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

    suspend fun getEpisodesByPage(pageIndex: Int): ApiOperation<EpisodePage> {
        return safeApiCall {
            client.get("episode") {
                url {
                    parameters.append("page", pageIndex.toString())
                }
            }
                .body<RemoteEpisodePage>()
                .toDomainEpisodePage()
        }
    }

    suspend fun getAllEpisodes(): ApiOperation<List<Episode>> {
        val data = mutableListOf<Episode>()
        var exception: Exception? = null

        getEpisodesByPage(pageIndex = 1).onSuccess { firstPage ->
            val totalPageCount = firstPage.info.pages
            data.addAll(firstPage.episodes)

            repeat(totalPageCount - 1) { index ->
                getEpisodesByPage(pageIndex = index + 2).onSuccess { nextPage ->
                    data.addAll(nextPage.episodes)
                }.onFailure { error ->
                    exception = error
                }

                if (exception != null) { return@onSuccess }
            }
        }.onFailure {
            exception = it
        }

        return exception?.let { ApiOperation.Failure(it) } ?: ApiOperation.Success(data)
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