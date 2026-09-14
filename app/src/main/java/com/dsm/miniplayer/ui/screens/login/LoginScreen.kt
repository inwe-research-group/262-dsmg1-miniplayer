package com.dsm.miniplayer.ui.screens.login

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsm.miniplayer.R
import com.dsm.miniplayer.ui.theme.Gray
import com.dsm.miniplayer.ui.theme.Green
import com.dsm.miniplayer.ui.theme.White
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(viewModel: LoginViewModel,
                onSignUp: () -> Unit = {},
                onToHome: () -> Unit = {}) {

    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current
    // FocusRequester para manejar el enfoque entre los campos
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    if (loginState.showDialog) {
        ResultDialog(
            success = loginState.success,
            message = loginState.message,
            onDismiss = {
                val wasSuccess = loginState.success
                viewModel.dismissDialog()
                if (wasSuccess) {
                    onToHome()
                }
            }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        //Logo centrado arriba
        Text(
            text = stringResource(R.string.login_logo_descripcion),
            color = White,
            fontSize = 42.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 32.dp)
        )

        Spacer(modifier = Modifier.height(64.dp))

        //Campos de entrada
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = loginState.usuarioSession.email,
                onValueChange = { viewModel.onEmailChanged(it)  },
                placeholder = { Text(stringResource(R.string.login_email), color = Gray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(emailFocusRequester),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    cursorColor = White,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Green,
                    unfocusedIndicatorColor = Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = loginState.usuarioSession.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                placeholder = { Text(stringResource(R.string.login_password), color = Gray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(passwordFocusRequester),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    cursorColor = White,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Green,
                    unfocusedIndicatorColor = Gray
                ),
                visualTransformation = if (loginState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (loginState.passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    val description = if (loginState.passwordVisible) stringResource(R.string.login_password_ocultar) else stringResource(R.string.login_password_mostrar)

                    IconButton(onClick = { viewModel.onPasswordVisibilityToggled() }) {
                        Icon(
                            imageVector = image,
                            contentDescription = description,
                            tint = White
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        //Botón verde tipo Spotify
        Button(
            onClick = {
                viewModel.login()
                Log.d("LoginScreen", "Intento de inicio de sesión para el email: ${loginState.usuarioSession.email}")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green), // Verde Spotify
            shape = RoundedCornerShape(50)
        ) {
            Text(text = stringResource(R.string.login_inicio), color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
        val annotatedText = buildAnnotatedString {
            append("¿No tienes cuenta? ")

            pushStringAnnotation(tag = "signup", annotation = "signup")
            withStyle(style = SpanStyle(color = Color.Green, fontWeight = FontWeight.Bold)) {
                append("Regístrate gratis")
            }
            pop()
        }

        ClickableText(
            text = annotatedText,
            onClick = { offset ->
                annotatedText.getStringAnnotations(tag = "signup", start = offset, end = offset)
                    .firstOrNull()?.let {
                        // Llamamos al callback en lugar de usar navController
                        onSignUp()
                    }
            },
            modifier = Modifier.padding(bottom = 32.dp),
            style = TextStyle(color = Color.White, fontSize = 14.sp)
        )

    }

}
@Composable
fun ResultDialog(success: Boolean, message: String, onDismiss: () -> Unit) {
    AlertDialog(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        onDismissRequest = onDismiss,
        title = { Text(if (success) "Inicio de sesión" else "Mensaje") },
        text = { Text(message) },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("OK")
            }
        }
    )
}