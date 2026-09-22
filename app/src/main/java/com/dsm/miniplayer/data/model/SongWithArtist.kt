package com.dsm.miniplayer.data.model

data class SongWithArtist(
    val song: Song = Song(),
    val artist: Artist? = null
)
