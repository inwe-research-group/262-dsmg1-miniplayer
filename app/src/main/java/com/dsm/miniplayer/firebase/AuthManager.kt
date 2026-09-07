package com.dsm.miniplayer.firebase

import com.dsm.firebaseauth.data.model.Usuario
import com.dsm.firebaseauth.data.model.UsuarioSesion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

object AuthManager {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private const val TAG = "AuthManager"

    fun register(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName("$email")
                        .build()
                    user?.updateProfile(profileUpdates)
                    val current = UsuarioSesion.usuario.value
                    if (current != null) {
                        UsuarioSesion.usuario.value = current.copy(email = email)
                    } else {
                        UsuarioSesion.usuario.value = Usuario(email = email)
                    }

                    user?.sendEmailVerification()
                        ?.addOnSuccessListener {
                            callback(true, "Se le ha enviado un Correo para verificación, revise su bandeja de correo y/o " +
                                    "spam y valide su correo. Luego podrá ingresar a la app.")
                        }
                        ?.addOnFailureListener { e ->
                            callback(false, "Cuenta creada, pero error al enviar verificación: ${e.message}")
                        }
                } else {
                    callback(false, task.exception?.message ?: "Error al registrar.")
                }
            }
    }
}