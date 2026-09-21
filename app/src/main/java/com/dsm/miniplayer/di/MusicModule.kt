package com.dsm.miniplayer.di

import com.dsm.miniplayer.data.repository.MusicRepository
import com.dsm.miniplayer.ui.screens.player.MusicViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val musicModule = module {
    single { MusicRepository() }
    viewModel { MusicViewModel(get()) }
}