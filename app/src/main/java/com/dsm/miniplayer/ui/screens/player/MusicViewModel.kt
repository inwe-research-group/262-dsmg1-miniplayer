package com.dsm.miniplayer.ui.screens.player

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsm.miniplayer.data.model.Player
import com.dsm.miniplayer.data.model.Song
import com.dsm.miniplayer.data.model.SongWithArtist
import com.dsm.miniplayer.data.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MusicViewModel(private val repo: MusicRepository) : ViewModel(){
    private val _songs = MutableStateFlow<List<SongWithArtist>>(emptyList())
    val songs: StateFlow<List<SongWithArtist>> = _songs

    private val _player = MutableStateFlow<Player?>(null)
    //Crea un flujo observable que puede contener un Player o null, iniciando en null.
    val player: StateFlow<Player?> = _player

    private var mediaPlayer: MediaPlayer? = null

    init {
        loadSongs()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            try {
                val newSongs = repo.getSongsWithArtists()
                Log.d("MusicViewModel", "Loaded songs: $newSongs")

                if (_songs.value != newSongs) // solo si realmente cambia
                    _songs.value = newSongs
                if (_player.value == null)
                //?: operador elvis | Obtén el player desde repo.Si devuelve null,crea un Player vacío.
                    _player.value = repo.getPlayer() ?: Player()
            } catch (e: Exception) {
                Log.e("MusicViewModel", "Error loading songs: ${e.message}", e)
            }
        }
    }

    fun playSongAt(index: Int) {
        if (index !in _songs.value.indices) return
        val song = _songs.value[index]
        if (song.song.url.isBlank()) {
            Log.e("MusicViewModel", "Song URL is empty for index $index")
            return
        }

        if (_player.value?.currentSongIndex == index && _player.value?.isPlaying == true && mediaPlayer?.isPlaying == true) {
            return // ya está en reproducción, no hacer nada
        }

        stopSong()

        try {
            val cleanUrl = song.song.url.trim()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(cleanUrl)
                setOnPreparedListener { mp ->
                    mp.start()
                    val newPlayer = Player(
                        currentSongIndex = index,
                        isPlaying = true,
                        position = 0
                    )
                    repo.updatePlayer(newPlayer)
                    _player.value = newPlayer
                }
                setOnErrorListener { mp, what, extra ->
                    Log.e("MusicViewModel", "MediaPlayer error: what=$what, extra=$extra")
                    try {
                        mp.reset()
                        mp.release()
                    } catch (e: Exception) {
                        Log.e("MusicViewModel", "Error releasing failed MediaPlayer: ${e.message}")
                    }
                    mediaPlayer = null
                    val newPlayer = _player.value?.copy(isPlaying = false) ?: Player(currentSongIndex = index, isPlaying = false)
                    repo.updatePlayer(newPlayer)
                    _player.value = newPlayer
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("MusicViewModel", "Error setting data source for song ${song.song.title}: ${e.message}", e)
            stopSong()
            val newPlayer = _player.value?.copy(isPlaying = false) ?: Player(currentSongIndex = index, isPlaying = false)
            _player.value = newPlayer
        }
    }

    fun pauseSong() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                }
            }
        } catch (e: Exception) {
            Log.e("MusicViewModel", "Error pausing mediaPlayer: ${e.message}")
        }
        _player.value?.let {
            if (it.isPlaying) { // solo actualiza si cambia
                val newPlayer = it.copy(isPlaying = false)
                repo.updatePlayer(newPlayer)
                _player.value = newPlayer
            }
        }
    }
    //volver a ejecutar la musica
    fun resumeSong() {
        val currentMp = mediaPlayer
        if (currentMp != null) {
            try {
                currentMp.start()
                _player.value?.let {
                    if (!it.isPlaying) { // solo actualiza si cambia
                        val newPlayer = it.copy(isPlaying = true)
                        repo.updatePlayer(newPlayer)
                        _player.value = newPlayer
                    }
                }
            } catch (e: Exception) {
                Log.e("MusicViewModel", "Error resuming mediaPlayer: ${e.message}")
                stopSong()
                val currentIndex = _player.value?.currentSongIndex ?: 0
                playSongAt(currentIndex)
            }
        } else {
            val currentIndex = _player.value?.currentSongIndex ?: -1
            val targetIndex = if (currentIndex in _songs.value.indices) currentIndex else 0
            playSongAt(targetIndex)
        }
    }

    fun playNext() {
        val songsList = _songs.value
        if (songsList.isEmpty()) return
        val current = _player.value
        val currentIndex = current?.currentSongIndex ?: -1
        val nextIndex = if (currentIndex >= 0) (currentIndex + 1) % songsList.size else 0
        playSongAt(nextIndex)
    }

    fun playPrevious() {
        val songsList = _songs.value
        if (songsList.isEmpty()) return
        val current = _player.value
        val currentIndex = current?.currentSongIndex ?: 0
        val prevIndex = if (currentIndex - 1 < 0) {
            songsList.size - 1
        } else {
            currentIndex - 1
        }
        playSongAt(prevIndex)
    }

    fun stopSong() {
        try {
            mediaPlayer?.let {
                try {
                    if (it.isPlaying) {
                        it.stop()
                    }
                } catch (e: Exception) {
                    Log.e("MusicViewModel", "Error stopping mediaPlayer state: ${e.message}")
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.e("MusicViewModel", "Error releasing mediaPlayer: ${e.message}")
        } finally {
            mediaPlayer = null
        }
    }






}