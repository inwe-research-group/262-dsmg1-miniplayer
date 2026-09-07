package com.dsm.firebaseauth.data.model

import com.google.firebase.Timestamp

data class UsuarioAuth(
    val usuarioAuthId: String = "",
    val usuarioId: String = "",
    val password: String? = null,
    val authProviderId: String? = null,
    val authProvider: String = "Local",
    val createdAt : Timestamp? = Timestamp.now(),
    val updatedAt : Timestamp? = Timestamp.now()
)
