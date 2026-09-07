package com.dsm.miniplayer

import android.app.Application
import android.util.Log
import com.dsm.miniplayer.di.loginModule
import com.dsm.miniplayer.di.signUpModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MiniPlayerApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("MiniPlayerApp", "onCreate started")
        try {
            startKoin {
                androidLogger(Level.DEBUG)
                androidContext(this@MiniPlayerApp)
                modules(
                    listOf(
                        loginModule,
                        signUpModule
                    )
                )
            }
            Log.d("FirebaseAuthApp", "Koin started successfully")
        } catch (e: Exception) {
            Log.e("FirebaseAuthApp", "Error starting Koin", e)
        }
    }
}