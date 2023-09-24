package com.hfad.ismlarmanosi2023

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    init {
        Log.d("AppLogging", "MyApp initialized")
    }
}