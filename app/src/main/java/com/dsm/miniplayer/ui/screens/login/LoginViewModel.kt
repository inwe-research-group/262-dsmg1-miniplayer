package com.dsm.miniplayer.ui.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.dsm.miniplayer.firebase.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UsuarioSessionData(
    val email: String = "",
    val password: String = ""
)

data class LoginState(
    val usuarioSession: UsuarioSessionData = UsuarioSessionData(),
    val passwordVisible: Boolean = false,
    val success: Boolean = false,
    val message: String = "",
    val showDialog: Boolean = false,
    val googleLoginSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val showResetPasswordDialog: Boolean = false,
    val resetPasswordEmail: String = ""
)
class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login() {
        val email = _loginState.value.usuarioSession.email.trim()
        val password = _loginState.value.usuarioSession.password

        if (email.isBlank() || password.isBlank()) {
            _loginState.update {
                it.copy(
                    success = false,
                    message = "El correo electrónico y la contraseña son obligatorios.",
                    showDialog = true
                )
            }
            return
        }

        Log.d("LoginViewModel", "Logging in with Local Auth: $email")
        _loginState.update { it.copy(isLoading = true) }

        AuthManager.login(email, password) { success, message ->
            val spanishMessage = translateMessage(message)
            Log.d("LoginViewModel", "Login success: $success — $spanishMessage")
            if (success) {
                _loginState.update {
                    it.copy(
                        success = true,
                        message = spanishMessage,
                        showDialog = true,
                        isLoading = false
                    )
                }
            } else {
                _loginState.update {
                    it.copy(
                        success = false,
                        message = spanishMessage,
                        showDialog = true,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun translateMessage(message: String): String {
        return when {
            message.contains("The supplied auth credential is incorrect, malformed or has expired.", ignoreCase = true) ->
                "El correo electrónico no es válido, a expirado o la contraseña es incorrecta."
            message.contains("badly formatted", ignoreCase = true) ->
                "El formato del correo electrónico no es válido."
            message.contains("no user record", ignoreCase = true) || message.contains("USER_NOT_FOUND", ignoreCase = true) ->
                "No existe un usuario registrado con este correo electrónico."
            message.contains("password is invalid", ignoreCase = true) || message.contains("INVALID_PASSWORD", ignoreCase = true) || message.contains("invalid-credential", ignoreCase = true) ->
                "La contraseña o el correo electrónico son incorrectos."
            message.contains("blocked all requests", ignoreCase = true) ->
                "Acceso bloqueado temporalmente por demasiados intentos fallidos. Intenta más tarde."
            else -> message
        }
    }
    fun dismissDialog() {
        _loginState.update { it.copy(showDialog = false) }
    }
    fun onEmailChanged(email: String) {
        _loginState.update { state ->
            state.copy(usuarioSession = state.usuarioSession.copy(email = email))
        }
    }

    fun onPasswordChanged(password: String) {
        _loginState.update { state ->
            state.copy(usuarioSession = state.usuarioSession.copy(password = password))
        }
    }

    fun onPasswordVisibilityToggled() {
        _loginState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }


}