package com.dsm.miniplayer.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.koin.androidx.compose.koinViewModel

@Composable
fun MusicScreen(
    viewModel: MusicViewModel = koinViewModel(),
    onInitial: () -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var backgroundTimestamp by remember { mutableStateOf<Long?>(null) }
    val timeoutMillis = 30_000L // Intervalo de tiempo límite en segundo plano (30 segundos)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    // Guarda el tiempo en que pasó a segundo plano y pausa/detiene el audio
                    backgroundTimestamp = System.currentTimeMillis()
                    viewModel.pauseSong()
                }
                Lifecycle.Event.ON_START -> {
                    backgroundTimestamp?.let { timestamp ->
                        val elapsed = System.currentTimeMillis() - timestamp
                        backgroundTimestamp = null
                        if (elapsed >= timeoutMillis) {
                            // Superó el intervalo de tiempo: finaliza la ejecución y va a InitialScreen
                            viewModel.stopSong()
                            onInitial()
                        } else {
                            // Regresó dentro del tiempo: reanuda la música automáticamente
                            viewModel.resumeSong()
                        }
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.stopSong()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .padding(top = 16.dp, bottom=32.dp, start = 16.dp, end = 16.dp)
            //.padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            "🎵 Mi Spotify",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        // Lista de canciones
        SongList(viewModel, Modifier.weight(1f))
        // Controles de reproducción
        PlayerSection(viewModel)
    }
}

@Composable
fun SongList(viewModel: MusicViewModel, modifier: Modifier = Modifier) {
    val songs by viewModel.songs.collectAsState() // solo canciones

    LazyColumn(modifier = modifier) {
        itemsIndexed(songs) { index, song ->
            Text(
                text = song.title,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.playSongAt(index) }
                    .padding(12.dp)
            )
        }
    }
}

@Composable
fun PlayerSection(viewModel: MusicViewModel) {
    val songs by viewModel.songs.collectAsState()
    val player by viewModel.player.collectAsState() // solo player

    player?.let {
        val song = songs.getOrNull(it.currentSongIndex)
        PlayerControls(
            song = song,
            isPlaying = it.isPlaying,
            onPlayPause = {
                if (it.isPlaying) viewModel.pauseSong()
                else viewModel.resumeSong()
            },
            onNext = { viewModel.playNext() },
            onPrevious = { viewModel.playPrevious() }
        )
    }
}

