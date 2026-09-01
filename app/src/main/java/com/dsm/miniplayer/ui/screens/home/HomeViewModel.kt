package com.dsm.miniplayer.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsm.miniplayer.data.model.Artist
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeViewModel: ViewModel() {
    private var db: FirebaseFirestore = Firebase.firestore
    private val _artist= MutableStateFlow<List<Artist>>(emptyList())
    val artist : StateFlow<List<Artist>> = _artist

    init {
        //repeat(5){loadData()}//
        getArtists()
    }

    private fun getArtists(){
        viewModelScope.launch {
            val result: List<Artist> = withContext(Dispatchers.IO){
                getAllArtists()
            }
            Log.d("Firestore", "Artistas cargados: ${result.size}")
            _artist.value = result
        }
    }

    //1.-
    private suspend fun getAllArtists(): List<Artist>{
        return try{
            db.collection("artists").get().await().documents.mapNotNull {
                    snapshot-> snapshot.toObject(Artist::class.java)
            }

        }catch (e: Exception){
            Log.e("Firestore", "Error al obtener artistas: ${e.message}", e)
            emptyList()
        }

    }


}