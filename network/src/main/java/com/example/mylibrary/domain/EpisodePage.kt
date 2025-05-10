package com.androidfactory.network.domain

import com.example.mylibrary.domain.Episode

data class EpisodePage(
    val info: Info,
    val episodes: List<Episode>
) {
    data class Info(
        val count: Int,
        val pages: Int,
        val next: String?,
        val prev: String?
    )
}
