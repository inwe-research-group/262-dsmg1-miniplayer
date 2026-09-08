package com.dsm.miniplayer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dsm.miniplayer.ui.screens.home.HomeScreen
import com.dsm.miniplayer.ui.screens.initial.InitialScreen
import com.dsm.miniplayer.ui.screens.login.LoginScreen
import com.dsm.miniplayer.ui.screens.signup.SignUpScreen
import com.dsm.miniplayer.ui.screens.signup.SignUpViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavigationWrapper(
    navHostController: NavHostController,
    auth: FirebaseAuth
) {
    val signUpViewModel: SignUpViewModel = koinViewModel()

    NavHost(navController = navHostController, startDestination = "initial") {
        composable("initial") {
            InitialScreen(
                onLogin={navHostController.navigate("logIn")},
                onSignUp={navHostController.navigate("signUp")}
            )
        }

        composable("logIn") {
            LoginScreen(
                auth,
                onSignUp={navHostController.navigate("signUp")},
                onToHome={navHostController.navigate("home")}
            )

        }

        composable("signUp") {
            SignUpScreen(signUpViewModel,
                onLogin={navHostController.navigate("logIn")}
            )
        }

        composable("home") {
            HomeScreen()
        }
    }
}