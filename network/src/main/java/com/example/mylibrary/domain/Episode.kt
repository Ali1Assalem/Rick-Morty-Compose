package com.example.mylibrary.domain

data class Episode(
    val id: Int,
    val name: String,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val airDate: String,
    val characterIdsInEpisode: List<Int>
)