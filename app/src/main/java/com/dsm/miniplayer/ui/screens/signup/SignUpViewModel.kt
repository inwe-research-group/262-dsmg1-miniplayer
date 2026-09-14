package com.dsm.miniplayer.ui.screens.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import com.dsm.miniplayer.firebase.AuthManager
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SignUpState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val tipoUsuarioId: Int = 1,
    val success: Boolean = false,
    val message: String = "",
    val showDialog: Boolean = false,
    val redirectToLogin: Boolean = false
)
class SignUpViewModel : ViewModel() {
    private val _signUpState = MutableStateFlow(SignUpState())
    val signUpState: StateFlow<SignUpState> = _signUpState.asStateFlow()

    fun signUp() {
        val currentState = _signUpState.value
        val email = currentState.email.trim()
        val password = currentState.password

        if (email.isBlank() || password.isBlank()) {
            _signUpState.update {
                it.copy(showDialog = true, success = false, message = "Por favor, complete todos los campos obligatorios.")
            }
            return
        }
        Log.d("SignUpViewModel", "Procediendo con registro de nuevo usuario: $email")
        AuthManager.register(email, password) { success, message ->
            val spanishMessage = translateMessage(message)
            Log.d("SignUpViewModel", "SignUp success: $success — $spanishMessage")
            if (success) {
                // Limpiar casillas del formulario tras registro exitoso
                _signUpState.value = SignUpState(
                    showDialog = true,
                    success = true,
                    redirectToLogin = true,
                    message = spanishMessage
                )
            } else {
                val isAlreadyInUse = message.contains("already in use", ignoreCase = true) || message.contains("ALREADY_IN_USE", ignoreCase = true)
                _signUpState.update {
                    it.copy(
                        showDialog = true,
                        success = false,
                        redirectToLogin = isAlreadyInUse,
                        message = spanishMessage
                    )
                }
            }
        }

    }
    fun dismissDialog() {
        if (_signUpState.value.success || _signUpState.value.redirectToLogin) {
            _signUpState.value = SignUpState()
        } else {
            _signUpState.update { it.copy(showDialog = false) }
        }
    }

    fun onEmailChanged(email: String) {
        _signUpState.update { it.copy(email = email) }
    }
    fun onPasswordChanged(password: String) {
        _signUpState.update { it.copy(password = password) }
    }
    fun onPasswordVisibilityToggled() {
        _signUpState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun resetForm() {
        _signUpState.value = SignUpState()
    }
    private fun translateMessage(message: String): String {
        return when {
            message.contains("already in use", ignoreCase = true) || message.contains("ALREADY_IN_USE", ignoreCase = true) ->
                "El correo electrónico indicado ya se encuentra registrado. Ingrese su usuario y contraseña para ingresar a la App."
            message.contains("at least 6 characters", ignoreCase = true) || message.contains("weak password", ignoreCase = true) || message.contains("WEAK_PASSWORD", ignoreCase = true) ->
                "La contraseña debe tener al menos 6 caracteres."
            message.contains("badly formatted", ignoreCase = true) || message.contains("invalid email", ignoreCase = true) || message.contains("INVALID_EMAIL", ignoreCase = true) ->
                "El formato del correo electrónico no es válido."
            message.contains("no user record", ignoreCase = true) || message.contains("USER_NOT_FOUND", ignoreCase = true) ->
                "No existe un usuario registrado con este correo electrónico."
            message.contains("password is invalid", ignoreCase = true) || message.contains("INVALID_PASSWORD", ignoreCase = true) || message.contains("invalid-credential", ignoreCase = true) ->
                "La contraseña o el correo electrónico son incorrectos."
            message.contains("blocked all requests", ignoreCase = true) ->
                "Acceso bloqueado temporalmente por demasiados intentos fallidos. Intenta más tarde."
            message.contains("network error", ignoreCase = true) || message.contains("network", ignoreCase = true) ->
                "Error de red. Revisa tu conexión a internet."
            else -> message
        }
    }
}