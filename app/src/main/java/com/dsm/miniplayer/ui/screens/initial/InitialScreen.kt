package com.dsm.miniplayer.ui.screens.initial

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsm.miniplayer.R
import com.dsm.miniplayer.ui.screens.login.LoginViewModel
import com.dsm.miniplayer.ui.theme.BackgroundButton
import com.dsm.miniplayer.ui.theme.Black
import com.dsm.miniplayer.ui.theme.Gray
import com.dsm.miniplayer.ui.theme.Green
import com.dsm.miniplayer.ui.theme.ShapeButton
import com.dsm.miniplayer.ui.theme.White
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

//private const val WEB_CLIENT_ID = "447068443896-6cu4jpjriea1i2o2uo8babprkbmk7sao.apps.googleusercontent.com"
private const val WEB_CLIENT_ID = "447068443896-qo91vptacj66nbv2ls8375qc9jt1ofro.apps.googleusercontent.com"
@Composable
fun InitialScreen(viewModel: LoginViewModel,
                  onLogin: () -> Unit = {},
                  onSignUp: () -> Unit = {},
                  onToHome: () -> Unit = {}){

    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()
    // Configuración de Google Sign-In
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember(context) { GoogleSignIn.getClient(context, gso) }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                viewModel.signInWithGoogle(idToken, account.id)
            }
        } catch (e: ApiException) {
            Log.e("InitialScreen", "Google Sign-In falló con código: ${e.statusCode}", e)
        }
    }
    // Efectos
    LaunchedEffect(loginState.googleLoginSuccess) {
        if (loginState.googleLoginSuccess) {
            viewModel.onGoogleLoginHandled()
            onToHome()
        }
    }

    //Diseño
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Gray, Black), startY = 0f, endY = 600f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.spotify),
            contentDescription = "",
            modifier = Modifier.clip(CircleShape)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            "Millions of songs.",
            color = White,
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Free on Spotify", color = White, fontSize = 38.sp, fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { onSignUp()},
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green)
        ) {
            Text(text = "Sign up free", color = Black, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        CustomButton(
            Modifier.clickable {
                googleSignInClient.signOut().addOnCompleteListener {
                    val signInIntent = googleSignInClient.signInIntent
                    googleLauncher.launch(signInIntent)
                }

            }, painterResource(id = R.drawable.google), "Continue with Google"
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomButton(
            Modifier.clickable { },
            painterResource(id = R.drawable.facebook),
            "Continue with Facebook"
        )
        Text(
            text = "Log In",
            color = White,
            modifier = Modifier
                .padding(24.dp)
                .clickable { onLogin()},
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
    }

}

@Composable
fun CustomButton(modifier: Modifier, painter: Painter, title: String) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 32.dp)
            .background(BackgroundButton)
            .border(2.dp, ShapeButton, CircleShape),
        contentAlignment = Alignment.CenterStart
    ) {
        Image(
            painter = painter,
            contentDescription = "",
            modifier = Modifier
                .padding(start = 16.dp)
                .size(16.dp)
        )
        Text(
            text = title,
            color = White,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}