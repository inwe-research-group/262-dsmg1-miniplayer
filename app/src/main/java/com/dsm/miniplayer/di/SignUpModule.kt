package com.dsm.miniplayer.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.dsm.miniplayer.ui.screens.signup.SignUpViewModel

val signUpModule = module {
    // ViewModel
    viewModel { SignUpViewModel() }
}