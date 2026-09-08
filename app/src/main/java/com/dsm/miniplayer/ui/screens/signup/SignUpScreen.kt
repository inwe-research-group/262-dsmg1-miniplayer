package com.dsm.miniplayer.ui.screens.signup

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsm.miniplayer.R
import com.dsm.miniplayer.ui.theme.Gray
import com.dsm.miniplayer.ui.theme.Green
import com.dsm.miniplayer.ui.theme.White
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    onLogin: () -> Unit = {}
)
{
    val signUpState by viewModel.signUpState.collectAsState()
    // FocusRequester para navegación de enfoque entre campos
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    if (signUpState.showDialog) {
        val shouldRedirect = signUpState.success || signUpState.redirectToLogin
        ResultDialog(
            success = signUpState.success,
            message = signUpState.message,
            onDismiss = {
                viewModel.dismissDialog()
                if (shouldRedirect) {
                    onLogin()
                }
            }
        )
    }
    //diseño de elementos IU
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
                value = signUpState.email,
                onValueChange = { viewModel.onEmailChanged(it) },
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
                value = signUpState.password,
                onValueChange = { viewModel.onPasswordChanged(it)},
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
                visualTransformation = if (signUpState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (signUpState.passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    val description = if (signUpState.passwordVisible) stringResource(R.string.login_password_ocultar) else stringResource(R.string.login_password_mostrar)

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
                viewModel.signUp()
                Log.d("SignUpScreen", "SignUpSubmitted: ${signUpState.email}")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green), // Verde Spotify
            shape = RoundedCornerShape(50)
        ) {
            Text(text = stringResource(R.string.signup), color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

}
@Composable
fun ResultDialog(success: Boolean, message: String, onDismiss: () -> Unit) {
    AlertDialog(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        onDismissRequest = onDismiss,
        title = { Text(if (success) "Registro exitoso" else "Error") },
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