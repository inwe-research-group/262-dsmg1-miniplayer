package com.dsm.miniplayer.di

import com.dsm.miniplayer.ui.screens.login.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val loginModule = module {
    // ViewModel
    viewModel { LoginViewModel() }
}