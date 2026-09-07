package com.dsm.firebaseauth.data.model

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Singleton que mantiene en memoria el estado de sesión del usuario actual.
 */
object UsuarioSesion {
    val usuario = MutableStateFlow<Usuario?>(null)
    val usuarioAuth = MutableStateFlow<UsuarioAuth?>(null)
    fun clear() {
        usuario.value = null
        usuarioAuth.value = null
    }
}
