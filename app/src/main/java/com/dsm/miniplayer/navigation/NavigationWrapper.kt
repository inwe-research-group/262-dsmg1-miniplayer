package com.dsm.miniplayer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dsm.miniplayer.ui.screens.initial.InitialScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavigationWrapper(
    navHostController: NavHostController,
    auth: FirebaseAuth
) {
    NavHost(navController = navHostController, startDestination = "initial") {
        composable("initial") {
            InitialScreen(
                onLogin={navHostController.navigate("logIn")},
                onSignUp={navHostController.navigate("signUp")}
            )
        }

        composable("logIn") {
            /*LoginScreen(
                auth,
                navigateToSignUp={navHostController.navigate("signUp")}
            )*/

        }

        composable("signUp") {
            // SignUpScreen()
        }

        composable("home") {
            // HomeScreen()
        }
    }
}