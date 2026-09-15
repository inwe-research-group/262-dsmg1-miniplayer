package com.dsm.firebaseauth.data.model

data class Song(
    val artistId: Int = 0,
    val id: Int = 0,
    val title: String = "",
    val url: String = "" //URL a Firebase Storage o HTTP
)
