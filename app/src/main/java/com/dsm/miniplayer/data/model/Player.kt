package com.dsm.miniplayer.data.model

data class Player(
    val currentSongIndex: Int = -1,//val currentSongId: String = "",
    val isPlaying: Boolean = false,
    val position: Long = 0L
)

