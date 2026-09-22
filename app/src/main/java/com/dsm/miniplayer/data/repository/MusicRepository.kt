package com.dsm.miniplayer.data.repository

import com.dsm.miniplayer.data.model.Artist
import com.dsm.miniplayer.data.model.Player
import com.dsm.miniplayer.data.model.Song
import com.dsm.miniplayer.data.model.SongArtist
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class MusicRepository {
    private val db = FirebaseDatabase.getInstance().reference
    private var dbf: FirebaseFirestore= Firebase.firestore

    suspend fun getSongs(): List<Song> {
        val snapshot = db.child("songs").get().await()
        //.await() → convierte la operación asíncrona de Firebase en una suspend function gracias a Kotlin
        return snapshot.children.mapNotNull { it.getValue(Song::class.java) }
    }

    suspend fun getPlayer(): Player? {
        val snapshot = db.child("player").get().await()
        return snapshot.getValue(Player::class.java)
    }

    suspend fun getArtists(): List<Artist> {
        val snapshot = dbf.collection("artists").get().await()
        return snapshot.documents.mapNotNull { it.toObject(Artist::class.java) }
    }

    suspend fun getSongsWithArtists(): List<SongArtist> = coroutineScope {
        val songsDeferred = async { getSongs() }
        val artistsDeferred = async { getArtists() }

        val songs = songsDeferred.await()
        val artistsMap = artistsDeferred.await().associateBy { it.artistId }

        songs.map { song ->
            SongArtist(
                song = song,
                artist = artistsMap[song.artistId]
            )
        }
    }

    fun updatePlayer(player: Player) {
        db.child("player").setValue(player)
    }
}