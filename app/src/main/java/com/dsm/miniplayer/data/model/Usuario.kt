package com.dsm.firebaseauth.data.model

import com.google.firebase.Timestamp

data class Usuario(
    val usuarioId: String = "",
    val tipoUsuarioId: Int = 1,
    val email: String = "",
    var createdAt : Timestamp? = Timestamp.now(),
    var updatedAt : Timestamp? = Timestamp.now()
)
