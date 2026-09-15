package com.dsm.miniplayer.data.repository

import com.dsm.firebaseauth.data.model.Player
import com.dsm.firebaseauth.data.model.Song
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class MusicRepository {
    private val db = FirebaseDatabase.getInstance().reference

    suspend fun getSongs(): List<Song> {
        val snapshot = db.child("songs").get().await()
        //.await() → convierte la operación asíncrona de Firebase en una suspend function gracias a Kotlin
        return snapshot.children.mapNotNull { it.getValue(Song::class.java) }
    }

    suspend fun getPlayer(): Player? {
        val snapshot = db.child("player").get().await()
        return snapshot.getValue(Player::class.java)
    }

    fun updatePlayer(player: Player) {
        db.child("player").setValue(player)
    }
}